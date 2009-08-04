package com.polopoly.pcmd.tool;

import java.util.List;

import com.polopoly.pcmd.bootstrap.BootstrapFileGenerator;
import com.polopoly.pcmd.tool.parameters.HotdeployBootstrapParameters;
import com.polopoly.util.client.PolopolyContext;

import example.deploy.hotdeploy.file.DeploymentFile;

public class HotdeployGenerateBootstrapTool implements Tool<HotdeployBootstrapParameters> {
    public HotdeployBootstrapParameters createParameters() {
        return new HotdeployBootstrapParameters();
    }

    public void execute(PolopolyContext context, HotdeployBootstrapParameters parameters) {
        List<DeploymentFile> deploymentFiles = parameters.discoverFiles();

        if (deploymentFiles.size() == 0) {
            System.err.println("No content XML files found in " + parameters.getFileOrDirectory() + ".");
            System.exit(1);
        }

        boolean force = parameters.isForce();
        boolean bootstrapNonCreated = parameters.isBootstrapNonCreated();
        boolean ignorePresent = parameters.isIgnorePresent();

        BootstrapFileGenerator generator = new BootstrapFileGenerator();

        generator.setForce(force);
        generator.setBootstrapNonCreated(bootstrapNonCreated);
        generator.setIgnorePresent(ignorePresent);

        generator.generateBootstrap(parameters.getDirectory(), deploymentFiles);
    }

    public String getHelp() {
        return "Generates a bootstrap.xml file with the content needed for the files in the specified directory to import properly.";
    }
}