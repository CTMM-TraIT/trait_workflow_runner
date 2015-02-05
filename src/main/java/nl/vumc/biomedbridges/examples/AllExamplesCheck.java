/**
 * Copyright 2015 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.examples;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import nl.vumc.biomedbridges.core.Constants;
import nl.vumc.biomedbridges.core.DefaultWorkflowEngineFactory;
import nl.vumc.biomedbridges.core.DefaultWorkflowFactory;
import nl.vumc.biomedbridges.core.WorkflowEngineFactory;
import nl.vumc.biomedbridges.core.WorkflowFactory;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.varia.LevelRangeFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class checks all of (working) examples on a number of servers and reports the results.
 *
 * You can run all examples using the following Maven command:
 * mvn compile exec:java -Dexec.mainClass="nl.vumc.biomedbridges.examples.AllExamplesCheck"
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class AllExamplesCheck {
    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(AllExamplesCheck.class);

    /**
     * Hidden constructor. Only the main method of this class is meant to be used.
     */
    private AllExamplesCheck() {
    }

    /**
     * Main method.
     *
     * @param arguments unused command-line arguments.
     */
    // CHECKSTYLE_OFF: UncommentedMain
    public static void main(final String[] arguments) {
        new AllExamplesCheck().checkAllExamples();
    }
    // CHECKSTYLE_ON: UncommentedMain

    /**
     * Run all examples on all Galaxy servers.
     */
    // todo: restore SpellCheckingInspection later.
    @SuppressWarnings("SpellCheckingInspection")
    private void checkAllExamples() {
        LogManager.getRootLogger().getAppender("console-appender").addFilter(createAppenderFilter());

        final List<Class<? extends AbstractBaseExample>> exampleClasses = Arrays.asList(
                ConcatenateExample.class, GrepExample.class, HistogramExample.class, LineCountExample.class
                //, RandomLinesExample.class, RemoveTopAndLeftExample.class, RnaSeqDgeExample.class
        );

//      todo: self signed certificate for Vancis servers? Error when trying to use the Galaxy API:
//      - PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid
//        certification path to requested target
//      - http://stackoverflow.com/questions/6908948/java-sun-security-provider-certpath-suncertpathbuilderexception-unable-to-find
//      + "The problem appears when your server has self signed certificate. To workaround it you can add this certificate to
//        the list of trusted certificates of your JVM."

        final List<String> serverUrls = Arrays.asList(
//                Constants.CENTRAL_GALAXY_URL,
//                Constants.VANCIS_PRO_GALAXY_URL, Constants.VANCIS_ACC_GALAXY_URL,
                Constants.THE_HYVE_GALAXY_URL
        );

        for (final String serverUrl : serverUrls) {
            final boolean hopefullyOnline = getGalaxyServerOnline(serverUrl) || serverUrl.contains(".ctmm-trait.nl/");
            logger.warn("Galaxy server " + serverUrl + " is {}.", hopefullyOnline ? "hopefully online" : "not available");
            if (hopefullyOnline)
                runExamplesOnServer(serverUrl, exampleClasses);
            logger.warn("");
            logger.warn("");
        }
    }

    /**
     * Create a filter to limit logging messages.
     *
     * @return the appender filter.
     */
    private LevelRangeFilter createAppenderFilter() {
        final LevelRangeFilter logAppenderFilter = new LevelRangeFilter();
        logAppenderFilter.setAcceptOnMatch(true);
        logAppenderFilter.setLevelMin(Level.WARN);
        logAppenderFilter.setLevelMax(Level.FATAL);

        return logAppenderFilter;
    }

    /**
     * Determine whether a Galaxy server appears to be online.
     *
     * @param serverUrl the URL of the Galaxy server.
     * @return whether it appears to be online.
     */
    private boolean getGalaxyServerOnline(final String serverUrl) {
        boolean serverOnline;
        final int minimumExpectedContentLength = 64;

        try {
            final StringBuilder content = new StringBuilder();
            final InputStream inputStream = new URL(serverUrl).openStream();
            int nextByte;
            while ((nextByte = inputStream.read()) != -1)
                content.append((char) nextByte);
            logger.debug("AllExamplesCheck.getGalaxyServerOnline - content length: {}", content.length());
            serverOnline = content.length() > minimumExpectedContentLength;
        } catch (final IOException e) {
            serverOnline = false;
        }

        return serverOnline;
    }

    /**
     * Run all examples on a specific Galaxy server.
     *
     * @param serverUrl      the URL of the Galaxy server.
     * @param exampleClasses the example classes to run.
     */
    private void runExamplesOnServer(final String serverUrl,
                                     final List<Class<? extends AbstractBaseExample>> exampleClasses) {
        logger.warn("Running examples:");
        for (final Class<? extends AbstractBaseExample> exampleClass : exampleClasses) {
            final DefaultWorkflowEngineFactory workflowEngineFactory = new DefaultWorkflowEngineFactory();
            final DefaultWorkflowFactory workflowFactory = new DefaultWorkflowFactory();
            try {
                logger.warn("- The example {} is running...", exampleClass.getName());
                final AbstractBaseExample example = exampleClass
                        .getConstructor(WorkflowEngineFactory.class, WorkflowFactory.class)
                        .newInstance(workflowEngineFactory, workflowFactory);
                example.setHttpLogging(false);
                final boolean result = example.runExample(serverUrl);
                logger.warn("- The example {} ran " + (result ? "" : "un") + "successfully.", exampleClass.getName());
                logger.warn("");
            } catch (final ReflectiveOperationException e) {
                e.printStackTrace();
            }
        }
    }
}
