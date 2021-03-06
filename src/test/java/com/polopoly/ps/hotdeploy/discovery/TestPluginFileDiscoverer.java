package com.polopoly.ps.hotdeploy.discovery;

import java.util.List;

import com.polopoly.ps.hotdeploy.discovery.PluginFileDiscoverer;
import com.polopoly.ps.hotdeploy.file.DeploymentFile;
import com.polopoly.ps.hotdeploy.file.JarDeploymentFile;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestPluginFileDiscoverer extends TestCase {
	public void testFilesFound() throws Exception {
		List<DeploymentFile> files = new PluginFileDiscoverer()
				.getFilesToImport();

		Assert.assertEquals(3, files.size());

		Assert.assertEquals("bootstrap.xml",
				((JarDeploymentFile) files.get(0)).getNameWithinJar());
		Assert.assertEquals("otherplugincontent.xml",
				((JarDeploymentFile) files.get(1)).getNameWithinJar());
		Assert.assertEquals("content/plugincontentindirectory.xml",
				((JarDeploymentFile) files.get(2)).getNameWithinJar());
	}
}
