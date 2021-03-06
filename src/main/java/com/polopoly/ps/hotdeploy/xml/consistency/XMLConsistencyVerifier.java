package com.polopoly.ps.hotdeploy.xml.consistency;

import static com.polopoly.ps.hotdeploy.client.Major.INPUT_TEMPLATE;
import static com.polopoly.ps.hotdeploy.xml.consistency.ParameterConstants.DIRECTORY_ARGUMENT;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.polopoly.ps.hotdeploy.DiscovererMainClass;
import com.polopoly.ps.hotdeploy.client.Major;
import com.polopoly.ps.hotdeploy.discovery.FileDiscoverer;
import com.polopoly.ps.hotdeploy.discovery.NotApplicableException;
import com.polopoly.ps.hotdeploy.file.DeploymentFile;
import com.polopoly.ps.hotdeploy.xml.allcontent.AllContent;
import com.polopoly.ps.hotdeploy.xml.parser.ContentXmlParser;
import com.polopoly.ps.hotdeploy.xml.parser.ParseCallback;
import com.polopoly.ps.hotdeploy.xml.parser.ParseContext;
import com.polopoly.ps.hotdeploy.xml.present.PresentContentAware;
import com.polopoly.ps.hotdeploy.xml.present.PresentFileWriter;


/**
 * Verifies that content XML is consistent and warns in non-existing fields are
 * referenced.
 * 
 * @author AndreasE
 */
public class XMLConsistencyVerifier extends DiscovererMainClass implements
		ParseCallback, PresentContentAware {
	private static final Logger logger = Logger
			.getLogger(XMLConsistencyVerifier.class.getName());

	private Set<String> inputTemplates = new HashSet<String>(100);

	private Map<String, String> contentTemplateByExternalId = new HashMap<String, String>(
			100);

	private Set<String> nonFoundContent = new HashSet<String>(100);

	private Set<String> nonFoundTemplates = new HashSet<String>(100);

	private Set<String> nonFoundClasses = new HashSet<String>(100);

	private Set<String> unusedTemplates = new HashSet<String>(100);

	private Collection<File> classDirectories = new ArrayList<File>();

	private Collection<DeploymentFile> filesToVerify = new ArrayList<DeploymentFile>();

	private boolean validateClassReferences = true;

	private AllContent allContent = new AllContent();

	private File writePresentFilesDirectory;

	XMLConsistencyVerifier(XMLConsistencyVerifier verifier,
			List<DeploymentFile> filesToVerify) {
		this(filesToVerify);

		contentTemplateByExternalId
				.putAll(verifier.contentTemplateByExternalId);
		inputTemplates.addAll(verifier.inputTemplates);
		this.classDirectories.addAll(verifier.classDirectories);
	}

	public XMLConsistencyVerifier() {
	}

	public void setWritePresentFilesDirectory(File directory) {
		this.writePresentFilesDirectory = directory;
	}

	@Override
	protected void addDirectory(File directory) {
		super.addDirectory(directory);
	}

	void discoverFiles(FileDiscoverer discoverer) {
		try {
			List<DeploymentFile> theseFiles = discoverer.getFilesToImport();

			if (theseFiles.isEmpty()) {
				logger.log(Level.WARNING, "Found no files to verify using "
						+ discoverer + ".");
			} else {
				logger.log(Level.INFO,
						discoverer + " identified " + theseFiles.size()
								+ " file(s) to verify.");
			}

			filesToVerify.addAll(theseFiles);
		} catch (NotApplicableException e) {
			logger.log(Level.INFO, "Cannot apply discovery strategy "
					+ discoverer + ": " + e.getMessage(), e);
		}
	}

	public XMLConsistencyVerifier(List<DeploymentFile> filesToVerify) {
		this.filesToVerify = filesToVerify;
	}

	/**
	 * Verify all XML files specified in the _import_order file in the specified
	 * directory.
	 * 
	 * @return true if the XML is consistent, false if it is not.
	 */
	public VerifyResult verify() {
		validateDirectories();

		for (FileDiscoverer discoverer : getDiscoverers()) {
			discoverFiles(discoverer);
		}

		if (filesToVerify == null || filesToVerify.isEmpty()) {
			System.err
					.println("No files found. Did you specify the parameter --"
							+ DIRECTORY_ARGUMENT + "?");
			System.exit(1);
		}

		logger.log(Level.INFO, "Starting verification of content XML in "
				+ filesToVerify.size() + " file(s).");

		for (DeploymentFile file : filesToVerify) {
			logger.log(Level.FINE, "Parsing " + file + "...");

			new ContentXmlParser().parse(file, this);
		}

		logger.log(Level.INFO, "Verification of " + filesToVerify.size()
				+ " content XML files finished.");

		VerifyResult result = new VerifyResult();

		result.nonFoundContent = nonFoundContent;
		result.nonFoundTemplates = nonFoundTemplates;
		result.nonFoundClasses = nonFoundClasses;
		result.unusedTemplates = unusedTemplates;

		writePresentFiles();

		return result;
	}

	private void writePresentFiles() {
		if (writePresentFilesDirectory == null) {
			return;
		}

		new PresentFileWriter(writePresentFilesDirectory)
				.writePresentFiles(allContent);
	}

	public void contentFound(ParseContext context, String externalId,
			Major major, String inputTemplate) {
		if (major == INPUT_TEMPLATE) {
			if (inputTemplates.add(externalId)) {
				unusedTemplates.add(externalId);
			}
		} else {
			if (inputTemplate != null && !inputTemplate.equals("")) {
				contentReferenceFound(context, INPUT_TEMPLATE, inputTemplate);
			}

			logger.log(Level.FINE, "Found content " + externalId
					+ " with input template " + inputTemplate + ".");

			contentTemplateByExternalId.put(externalId, inputTemplate);
		}

		allContent.add(major, externalId);
	}

	public void contentReferenceFound(ParseContext context, Major major,
			String externalId) {
		if (major == INPUT_TEMPLATE) {
			if (!isPresentInputTemplate(externalId)) {
				nonFoundTemplates.add(externalId);
				logger.log(Level.WARNING, "Undefined template " + externalId
						+ " was referenced in " + context.getFile());
			}
			unusedTemplates.remove(externalId);
		} else if (!isPresentContent(externalId)) {
			if (isPresentInputTemplate(externalId)) {
				unusedTemplates.remove(externalId);
			} else {
				nonFoundContent.add(externalId);
				logger.log(Level.WARNING, "Undefined content " + externalId
						+ " was referenced in " + context.getFile());
			}
		}
	}

	protected boolean isPresentContent(String externalId) {
		return contentTemplateByExternalId.containsKey(externalId);
	}

	protected boolean isPresentInputTemplate(String externalId) {
		return inputTemplates.contains(externalId);
	}

	public void classReferenceFound(DeploymentFile file, String className) {
		if (!isValidateClassReferences()) {
			return;
		}

		if (className.startsWith("com.polopoly")) {
			return;
		}

		StringBuffer fileName = new StringBuffer(className.length() + 10);

		for (int i = 0; i < className.length(); i++) {
			char ch = className.charAt(i);

			if (ch == '.') {
				fileName.append(File.separatorChar);
			} else {
				fileName.append(ch);
			}
		}

		fileName.append(".class");

		boolean found = false;

		for (File classDirectory : classDirectories) {
			if ((new File(classDirectory, fileName.toString())).exists()) {
				found = true;
				break;
			}
		}

		if (!found) {
			try {
				Class.forName(className);
			} catch (ClassNotFoundException e) {
				logger.log(Level.WARNING, "Unknown class " + className
						+ " was referenced in file " + file + ".");
				nonFoundClasses.add(className);
			}
		}
	}

	public void addClassDirectory(File classDirectory) {
		classDirectories.add(classDirectory);
	}

	public boolean areErrorsFound() {
		return !nonFoundContent.isEmpty() || !nonFoundTemplates.isEmpty()
				|| !nonFoundClasses.isEmpty();
	}

	public void presentContent(String externalId) {
		contentTemplateByExternalId.put(externalId, null);
	}

	public void presentTemplate(String inputTemplate) {
		inputTemplates.add(inputTemplate);
	}

	public void setValidateClassReferences(boolean validateClassReferences) {
		this.validateClassReferences = validateClassReferences;
	}

	public boolean isValidateClassReferences() {
		return validateClassReferences;
	}

	public Collection<DeploymentFile> getFiles() {
		return filesToVerify;
	}
}
