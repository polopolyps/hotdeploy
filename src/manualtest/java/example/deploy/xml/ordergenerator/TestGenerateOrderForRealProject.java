package com.polopoly.ps.hotdeploy.xml.ordergenerator;

import java.io.File;
import java.util.Collections;
import java.util.List;

import com.polopoly.ps.hotdeploy.discovery.FileDiscoverer;
import com.polopoly.ps.hotdeploy.discovery.importorder.ImportOrderFileDiscoverer;
import com.polopoly.ps.hotdeploy.file.DeploymentFile;

public class TestGenerateOrderForRealProject {

    public void testDiscoverMtvuFiles() {
        DiscovereredFilesAggregator filesInDirectory =
            new DiscovereredFilesAggregator(Collections.singleton((FileDiscoverer)
                    new ImportOrderFileDiscoverer(new File("/projects/mtvu-trunk/src/resources"))));

        ImportOrderGenerator generator = new ImportOrderGenerator();
        List<DeploymentFile> result = generator.generate(filesInDirectory.getFiles());

        for (DeploymentFile deploymentFile : result) {
            System.out.println(deploymentFile);
        }
    }

}
