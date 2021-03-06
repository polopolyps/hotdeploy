package com.polopoly.ps.hotdeploy.state;

import com.polopoly.ps.hotdeploy.file.DeploymentFile;

public interface FileChecksums extends Iterable<DeploymentFile> {
	void deleteChecksums(DeploymentFile file);

	void setChecksums(DeploymentFile file, long quickChecksum, long slowChecksum);

	void persist() throws CouldNotUpdateStateException;

	long getQuickChecksum(DeploymentFile file);

	long getSlowChecksum(DeploymentFile file);

	boolean areAllChangesPersisted();

	String getAdditionalInformation(DeploymentFile file)
			throws NoInformationStoredException;

	void setAdditionalInformation(DeploymentFile file,
			String additionalInformation);
}
