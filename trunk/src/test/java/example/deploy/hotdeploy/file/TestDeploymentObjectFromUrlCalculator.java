package example.deploy.hotdeploy.file;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestDeploymentObjectFromUrlCalculator extends TestCase {

	public void testNormalFile() throws Exception {
		Assert.assertTrue(toDeploymentObject("/_import_order") instanceof FileDeploymentFile);
	}

	public void testJarFile() throws Exception {
		Assert.assertTrue(toDeploymentObject("/b/c.xml") instanceof JarDeploymentFile);
	}

	private DeploymentObject toDeploymentObject(String resourceName)
			throws Exception {
		return new DeploymentObjectFromUrlCalculator(getClass().getResource(
				resourceName)).toDeploymentObject();
	}

}
