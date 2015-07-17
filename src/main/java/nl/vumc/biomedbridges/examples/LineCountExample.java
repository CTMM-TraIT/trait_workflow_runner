/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.examples;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import nl.vumc.biomedbridges.core.Constants;
import nl.vumc.biomedbridges.core.DefaultWorkflowFactory;
import nl.vumc.biomedbridges.core.FileUtils;
import nl.vumc.biomedbridges.core.Workflow;
import nl.vumc.biomedbridges.core.WorkflowFactory;
import nl.vumc.biomedbridges.core.WorkflowType;
import nl.vumc.biomedbridges.galaxy.configuration.GalaxyConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains a simple example of the workflow running functionality: the line, word, and character count
 * workflow returns these statistics for the input file.
 *
 * You can run this example using the following Maven command:
 * mvn compile exec:java -Dexec.mainClass="nl.vumc.biomedbridges.examples.LineCountExample"
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class LineCountExample extends AbstractBaseExample {
    /**
     * The name of the Galaxy history.
     */
    protected static final String HISTORY_NAME = "Line, word, and character count history";

    /**
     * The name of the output dataset.
     */
    protected static final String OUTPUT_NAME = "Line/Word/Character count on data 1";

    /**
     * The header line for the expected output.
     */
    protected static final String HEADER_LINE = "#lines\twords";

    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(LineCountExample.class);

    /**
     * Construct the line count example.
     *
     * @param workflowFactory the workflow factory to use.
     */
    public LineCountExample(final WorkflowFactory workflowFactory) {
        super(null, workflowFactory, logger);
    }

    /**
     * Main method.
     *
     * @param arguments unused command-line arguments.
     * @throws MalformedURLException when the book URL is invalid.
     */
    // CHECKSTYLE_OFF: UncommentedMain
    public static void main(final String[] arguments) throws MalformedURLException {
        final LineCountExample example = new LineCountExample(new DefaultWorkflowFactory());
        // Use a book classic to do some counting: The Adventures of Sherlock Holmes, by Arthur Conan Doyle.
        final URL bookUrl = new URL("https://www.gutenberg.org/ebooks/1661.txt.utf-8");
        final File bookFile = FileUtils.createTemporaryFileFromURL(bookUrl);
        example.run(Constants.THE_HYVE_GALAXY_URL, Constants.LINE_COUNT_WORKFLOW, bookFile, false);
    }
    // CHECKSTYLE_ON: UncommentedMain

    /**
     * Run this example workflow and return the result.
     *
     * @param galaxyInstanceUrl the URL of the Galaxy instance to use.
     * @param workflowName      the name of the workflow.
     * @param bookFile          the file to count in.
     * @param useInternalCounts whether to use the internalCounts (true) or getExpectedLines (false) method.
     * @return whether the workflow ran successfully.
     */
    public boolean run(final String galaxyInstanceUrl, final String workflowName, final File bookFile,
                       final boolean useInternalCounts) {
        initializeExample(logger, "LineCountExample.runExample");

        final GalaxyConfiguration galaxyConfiguration = new GalaxyConfiguration().setDebug(httpLogging);
        galaxyConfiguration.buildConfiguration(galaxyInstanceUrl, null, HISTORY_NAME);

        final Workflow workflow = workflowFactory.getWorkflow(WorkflowType.GALAXY, galaxyConfiguration,
                                                              workflowName);

        if (bookFile != null) {
            try {
                workflow.addInput("Input Dataset", bookFile);
                workflow.setParameter(2, "options", Arrays.asList("lines", "words"));

                workflow.run();

                final List<String> expectedLines = useInternalCounts ? internalCounts(bookFile) : getExpectedLines();
                checkWorkflowSingleOutput(workflow, OUTPUT_NAME, expectedLines);
            } catch (final InterruptedException | IOException e) {
                logger.error("Exception while running workflow {}.", workflow.getName(), e);
            }
        }

        System.out.println();
        System.out.println("Thread dump:");
        System.out.println(getThreadDump());

        return finishExample(workflow);
    }

    /**
     * Retrieve information about the Java threads.
     *
     * @return a string with information about the Java threads.
     */
    private static String getThreadDump() {
        final StringBuilder threadDump = new StringBuilder();
        final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        final int maxThreadDepth = 100;
        for (final ThreadInfo threadInfo : threadMXBean.getThreadInfo(threadMXBean.getAllThreadIds(), maxThreadDepth)) {
            threadDump.append('"');
            threadDump.append(threadInfo.getThreadName());
            threadDump.append("\" \n   java.lang.Thread.State: ");
            threadDump.append(threadInfo.getThreadState());
            for (final StackTraceElement stackTraceElement : threadInfo.getStackTrace()) {
                threadDump.append("\n        at ");
                threadDump.append(stackTraceElement);
            }
            threadDump.append("\n\n");
        }
        return threadDump.toString();
    }

    /**
     * Get the expected output lines.
     *
     * @return the expected output lines.
     */
    protected List<String> getExpectedLines() {
        return Arrays.asList(HEADER_LINE, "13052\t107533");
    }

    /**
     * Simple internal implementation of wc to determine the expected output of the workflow.
     *
     * @param inputFile the file to count in.
     * @return the expected output lines.
     * @throws IOException when reading from the file fails.
     */
    protected static List<String> internalCounts(final File inputFile) throws IOException {
        final List<String> lines = Files.readAllLines(inputFile.toPath(), Charset.forName("UTF-8"));
        long wordCount = 0;

        for (final String line : lines)
            wordCount += line.split("\\s+").length;

        return Arrays.asList(HEADER_LINE, String.format("%d\t%d", lines.size(), wordCount));
    }
}
