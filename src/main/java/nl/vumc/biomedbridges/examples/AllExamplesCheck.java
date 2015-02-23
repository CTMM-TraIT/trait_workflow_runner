/**
 * Copyright 2015 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.examples;

import com.github.jmchilton.blend4j.galaxy.GalaxyResponseException;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.vumc.biomedbridges.core.Constants;
import nl.vumc.biomedbridges.core.DefaultWorkflowEngineFactory;
import nl.vumc.biomedbridges.core.DefaultWorkflowFactory;
import nl.vumc.biomedbridges.core.WorkflowEngineFactory;
import nl.vumc.biomedbridges.core.WorkflowFactory;

import org.apache.log4j.Appender;
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
     * The list of Galaxy servers to check during Continuous Integration (CI).
     */
    protected static final List<String> CI_GALAXY_SERVER_URLS = Arrays.asList(
            Constants.CENTRAL_GALAXY_URL, Constants.THE_HYVE_GALAXY_URL
    );

    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(AllExamplesCheck.class);

    /**
     * The list of all Galaxy servers to check.
     */
    private static final List<String> ALL_GALAXY_SERVER_URLS = Arrays.asList(
//            Constants.CENTRAL_GALAXY_URL,
//            Constants.VANCIS_PRO_GALAXY_URL
//            Constants.VANCIS_ACC_GALAXY_URL,
            Constants.THE_HYVE_GALAXY_URL
//            Constants.LOCAL_HOST_GALAXY_INSTANCE_URL
    );

    /**
     * The list of all example classes to check.
     */
    private static final List<Class<? extends AbstractBaseExample>> ALL_EXAMPLE_CLASSES = Arrays.asList(
            ConcatenateExample.class,
//            GrepExample.class,
//            HistogramExample.class,
//            LineCountExample.class,
////            RandomLinesExample.class,
////            RemoveTopAndLeftExample.class,
            RnaSeqDgeExample.class
    ).subList(1, 2);

    /**
     * These example classes will be skipped on all Vancis servers, because the required tools are not available.
     */
    private static final List<Class<? extends AbstractBaseExample>> SKIP_EXAMPLES_VANCIS = Arrays.asList(
            ConcatenateExample.class, GrepExample.class, HistogramExample.class, RandomLinesExample.class,
            RemoveTopAndLeftExample.class
    );

    /**
     * This map contains server URLs and a list of example classes to skip for a specific server.
     */
    private static final ImmutableMap<String, List<Class<? extends AbstractBaseExample>>> SKIP_EXAMPLES = ImmutableMap.of(
            Constants.VANCIS_PRO_GALAXY_URL, SKIP_EXAMPLES_VANCIS,
            Constants.VANCIS_ACC_GALAXY_URL, SKIP_EXAMPLES_VANCIS,
            // todo: the histogram tool does not work as expected on the central Galaxy server.
            Constants.CENTRAL_GALAXY_URL, Arrays.asList(HistogramExample.class, RnaSeqDgeExample.class),
            // The histogram tool is not available on the Galaxy server at The Hyve. Why is RNA-Seq failing?
            Constants.THE_HYVE_GALAXY_URL, Arrays.asList(HistogramExample.class, RemoveTopAndLeftExample.class /*RnaSeqDgeExample.class*/),
            Constants.LOCAL_HOST_GALAXY_INSTANCE_URL, Arrays.asList(HistogramExample.class, AbstractBaseExample.class)
    );

    /**
     * The server names for which the line count example needs a small adjustment in the expected output.
     */
    private static final List<String> FIX_LINE_COUNT_SERVERS = Arrays.asList(
            Constants.VANCIS_PRO_GALAXY_URL, Constants.VANCIS_ACC_GALAXY_URL, Constants.THE_HYVE_GALAXY_URL
    );

    /**
     * Hidden constructor (protected for testing). Only the main and checkAllExamples methods of this class are meant to
     * be used.
     */
    protected AllExamplesCheck() {
    }

    /**
     * Main method.
     *
     * @param arguments unused command-line arguments.
     */
    // CHECKSTYLE_OFF: UncommentedMain
    public static void main(final String[] arguments) {
        new AllExamplesCheck().checkAllExamples(ALL_GALAXY_SERVER_URLS);
    }
    // CHECKSTYLE_ON: UncommentedMain

    /**
     * Run all examples on all Galaxy servers.
     *
     * @param galaxyServerUrls the list with Galaxy server URLs to check.
     * @return a short report of the results: [server name]": "[success rate]" ["[failures]"]" separated by "; " for
     * each server.
     */
    protected String checkAllExamples(final List<String> galaxyServerUrls) {
        final long startTime = System.currentTimeMillis();
        final StringBuilder report = new StringBuilder();

        final Appender consoleAppender = LogManager.getRootLogger().getAppender("console-appender");
        if (consoleAppender != null)
            consoleAppender.addFilter(createAppenderFilter());

        final List<String> summary = new ArrayList<>();
        for (final String serverUrl : galaxyServerUrls) {
            final boolean serverOnline = getGalaxyServerOnline(serverUrl);
            logger.warn("Galaxy server " + serverUrl + " is {}.", serverOnline ? "online" : "not available");
            if (serverOnline) {
                final String serverReport = runExamplesOnServer(serverUrl, ALL_EXAMPLE_CLASSES, SKIP_EXAMPLES);
                summary.add(serverReport);
                report.append(report.length() == 0 ? "" : "; ");
                report.append(serverReport);
            }
            logger.warn("");
            logger.warn("");
        }

        printSummary(summary);
        logger.warn("");
        logger.warn("Checking all examples took {} seconds.", (System.currentTimeMillis() - startTime) / 1000);

        return report.toString();
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
     * @param skipExamples   the examples to skip for specific servers.
     * @return the server report: [server URL]: [successful example count] '/' [attempt count] " [" failures "]".
     */
    private String runExamplesOnServer(final String serverUrl,
                                       final List<Class<? extends AbstractBaseExample>> exampleClasses,
                                       final ImmutableMap<String, List<Class<? extends AbstractBaseExample>>> skipExamples) {
        logger.warn("Running examples:");
        int successCount = 0;
        int skipCount = 0;
        final List<String> failures = new ArrayList<>();
        for (final Class<? extends AbstractBaseExample> exampleClass : exampleClasses) {
            if (!skipExamples.containsKey(serverUrl) || !skipExamples.get(serverUrl).contains(exampleClass)) {
                if (runExampleOnServer(serverUrl, exampleClass))
                    successCount++;
                else
                    failures.add(exampleClass.getSimpleName());
            } else {
                logger.warn("- The example {} is skipped for server {}.", exampleClass.getSimpleName(), serverUrl);
                skipCount++;
            }
        }
        logger.warn("");
        final String successRate = String.format("%d/%d", successCount, exampleClasses.size() - skipCount);
        logger.warn("Success rate: {}.", successRate);
        logger.warn("");
        logger.warn("");

        return serverUrl + ": " + successRate + " [" + Joiner.on(", ").join(failures) + "]";
    }

    /**
     * Run an example on a specific Galaxy server.
     *
     * @param serverUrl    the URL of the Galaxy server.
     * @param exampleClass the example class to run.
     * @return whether the example ran successfully.
     */
    private boolean runExampleOnServer(final String serverUrl, final Class<? extends AbstractBaseExample> exampleClass) {
        boolean result = false;
        final DefaultWorkflowEngineFactory workflowEngineFactory = new DefaultWorkflowEngineFactory();
        final DefaultWorkflowFactory workflowFactory = new DefaultWorkflowFactory();

        try {
            logger.warn("");
            logger.warn("- The example {} is running...", exampleClass.getSimpleName());
            final AbstractBaseExample example = exampleClass
                    .getConstructor(WorkflowEngineFactory.class, WorkflowFactory.class)
                    .newInstance(workflowEngineFactory, workflowFactory);
            example.setHttpLogging(true);
            if (exampleClass == LineCountExample.class && FIX_LINE_COUNT_SERVERS.contains(serverUrl))
                ((LineCountExample) example).setFixExpectedOutput(true);
            try {
                result = example.runExample(serverUrl);
            } catch (final GalaxyResponseException e) {
                logger.error("Galaxy response exception while running an example.", e);
            }
            logger.warn("- The example {} ran " + (result ? "" : "un") + "successfully.", exampleClass.getSimpleName());
        } catch (final ReflectiveOperationException e) {
            logger.error("Exception while running an example.", e);
        }

        return result;
    }

    /**
     * Print the summary of the checks.
     *
     * @param summary the summary with one line for each Galaxy server.
     */
    private void printSummary(final List<String> summary) {
        logger.warn("");
        logger.warn("Summary:");
        for (final String line : summary)
            logger.warn("- " + line);
    }
}
