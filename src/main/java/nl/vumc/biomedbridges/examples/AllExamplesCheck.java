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

/**
 * This class checks all of (working) examples on a number of servers and reports the results.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class AllExamplesCheck {
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

    // todo: restore SpellCheckingInspection later.
    @SuppressWarnings("SpellCheckingInspection")
    private void checkAllExamples() {
//        System.out.println("console-appender filter: " + LogManager.getRootLogger().getAppender("console-appender").getFilter());

        final LevelRangeFilter logAppenderFilter = new LevelRangeFilter();
        logAppenderFilter.setAcceptOnMatch(true);
        logAppenderFilter.setLevelMin(Level.WARN);
        logAppenderFilter.setLevelMax(Level.FATAL);

        LogManager.getRootLogger().getAppender("console-appender").addFilter(logAppenderFilter);
        System.out.println("console-appender filter: " + LogManager.getRootLogger().getAppender("console-appender").getFilter());

        final List<Class<? extends BaseExample>> exampleClasses = Arrays.asList(
                ConcatenateExample.class, GrepExample.class, HistogramExample.class, LineCountExample.class//,
                //RandomLinesExample.class, RemoveTopAndLeftExample.class, RnaSeqDgeExample.class
        );

//      todo: self signed certificate for Vancis servers? Error when trying to use the Galaxy API:
//      - PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid
//        certification path to requested target
//      - http://stackoverflow.com/questions/6908948/java-sun-security-provider-certpath-suncertpathbuilderexception-unable-to-find
//      + "The problem appears when your server has self signed certificate. To workaround it you can add this certificate to
//        the list of trusted certificates of your JVM."

        final List<String> serverUrls = Arrays.asList(/*Constants.CENTRAL_GALAXY_URL, Constants.VANCIS_PRO_GALAXY_URL,
                                                      Constants.VANCIS_ACC_GALAXY_URL,*/ Constants.THE_HYVE_GALAXY_URL);

        for (final String serverUrl : serverUrls) {
            boolean serverOnline = false;
            try {
                final StringBuilder content = new StringBuilder();
                final InputStream inputStream = new URL(serverUrl).openStream();
                int nextByte;
                while ((nextByte = inputStream.read()) != -1)
                    content.append((char) nextByte);
                System.out.println("content.length() = " + content.length());
                serverOnline = content.length() > 64;
//                final Object content = new URL(serverUrl).getContent();
//                System.out.println("content = " + content);
//                if (content instanceof String) {
//                    System.out.println("Content length: " + ((String) content).length());
//                    serverOnline = ((String) content).length() > 64;
//                }
            } catch (final IOException e) {
                // Empty.
            }

            if (serverOnline || serverUrl.contains(".ctmm-trait.nl/")) {
                System.out.println("Galaxy server " + serverUrl + " is " + (serverOnline ? "" : "hopefully ") + "online.");
                System.out.println("Running examples:");
                for (final Class<? extends BaseExample> exampleClass : exampleClasses) {
                    final DefaultWorkflowEngineFactory workflowEngineFactory = new DefaultWorkflowEngineFactory();
                    final DefaultWorkflowFactory workflowFactory = new DefaultWorkflowFactory();
                    try {
                        System.out.println("- The example " + exampleClass.getName() + " is running...");
                        final BaseExample example = exampleClass
                                .getConstructor(WorkflowEngineFactory.class, WorkflowFactory.class)
                                .newInstance(workflowEngineFactory, workflowFactory);
                        example.setHttpLogging(false);
//                        LogManager.getRootLogger().getAppender("console-appender").addFilter(logAppenderFilter);
                        final boolean result = example.runExample(serverUrl);
                        System.out.println("- The example " + exampleClass.getName() + " ran " + (result ? "" : "un")
                                           + "successfully.");
                        System.out.println();
//                        System.out.println("console-appender filter: "
//                                           + LogManager.getRootLogger().getAppender("console-appender").getFilter());
                    } catch (final ReflectiveOperationException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                System.out.println("Galaxy server " + serverUrl + " is not available.");
            }
            System.out.println();
            System.out.println();
        }
    }
}
