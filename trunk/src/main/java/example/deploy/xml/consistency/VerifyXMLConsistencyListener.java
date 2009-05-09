package example.deploy.xml.consistency;

import java.io.File;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import example.deploy.hotdeploy.discovery.DefaultDiscoverers;

/**
 * Servlet listener that verifies that content XML is consistent and warns in
 * non-existing fields are referenced.
 * @author AndreasE
 */
public class VerifyXMLConsistencyListener implements ServletContextListener {
    public void contextDestroyed(ServletContextEvent event) {

    }

    public void contextInitialized(final ServletContextEvent event) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                // let deployment finish first.
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                }

                XMLConsistencyVerifier verifier =
                    new XMLConsistencyVerifier(DefaultDiscoverers.getDiscoverers());

                verifier.setRootDirectory(new File(event.getServletContext().getRealPath("/")));

                verifier.verify();
            }
        };

        thread.run();
    }
}