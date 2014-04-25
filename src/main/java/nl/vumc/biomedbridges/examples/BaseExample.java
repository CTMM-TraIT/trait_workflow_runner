/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.biomedbridges.examples;

import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;

/**
 * This class contains shared functionality for the workflow running examples.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class BaseExample {
    /**
     * The Galaxy server (instance URL) to use.
     *
     * Note: the API key is read from the .blend.properties file to keep the API key out of the GitHub repository. See
     * the GalaxyConfiguration class for more information on how to use this configuration file. Please change the
     * Galaxy server and the API key together to keep them in sync.
     */
    protected static final String GALAXY_INSTANCE_URL = "https://usegalaxy.org/";

    /**
     * The number of milliseconds in a second.
     */
    private static final int MILLISECONDS_PER_SECOND = 1000;

    /**
     * The start time of this example (in milliseconds).
     */
    private long startTime;

    /**
     * Initialize running an example by configuring the logging and storing the start time.
     *
     * @param logger the logger to use.
     * @param name   the name of the example.
     */
    public void initializeExample(final Logger logger, final String name) {
        DOMConfigurator.configure(BaseExample.class.getClassLoader().getResource("log4j.xml"));
        logger.info("========================================");
        logger.info(name + " has started.");

        startTime = System.currentTimeMillis();
    }

    /**
     * Finish running an example by logging the duration.
     *
     * @param logger the logger to use.
     */
    public void finishExample(final Logger logger) {
        final double durationSeconds = (System.currentTimeMillis() - startTime) / (float) MILLISECONDS_PER_SECOND;
        logger.info("");
        logger.info(String.format("Running the workflow took %1.2f seconds.", durationSeconds));
    }
}
