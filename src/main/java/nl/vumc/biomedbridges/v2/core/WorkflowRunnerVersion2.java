/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.biomedbridges.v2.core;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.io.Files;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains a simple test of the workflow running functionality.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class WorkflowRunnerVersion2 {
    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(WorkflowRunnerVersion2.class);

    /**
     * The number of milliseconds in a second.
     */
    private static final int MILLISECONDS_PER_SECOND = 1000;

    /**
     * The name of the test workflow.
     */
    public static final String TEST_WORKFLOW_NAME_1 = "TestWorkflowConcatenate";
    public static final String TEST_WORKFLOW_NAME_2 = "TestWorkflowScatterplot";
    public static final String TEST_WORKFLOW_NAME = TEST_WORKFLOW_NAME_2;

    /**
     * Line for test file 1.
     */
    private static final String LINE_TEST_FILE_1 = "Hello workflow engine!!!";

    /**
     * Line for test file 2.
     */
    private static final String LINE_TEST_FILE_2 = "Do you wanna play?";

    /**
     * Hidden constructor. The main method below will create a workflow runner.
     */
    private WorkflowRunnerVersion2() {
    }

    /**
     * Main method.
     *
     * @param arguments unused command-line arguments.
     */
    // CHECKSTYLE_OFF: UncommentedMain
    public static void main(final String[] arguments) {
        try {
            DOMConfigurator.configure(WorkflowRunnerVersion2.class.getClassLoader().getResource("log4j.xml"));
            logger.info("========================================");
            logger.info("WorkflowRunnerVersion2.main has started.");

            final long startTime = System.currentTimeMillis();
            //final String workflowType = WorkflowFactory.DEMONSTRATION_TYPE;
            final String workflowType = WorkflowFactory.GALAXY_TYPE;
            final WorkflowEngine workflowEngine = WorkflowFactory.getWorkflowEngine(workflowType);
            final Workflow workflow = WorkflowFactory.getWorkflow(workflowType, TEST_WORKFLOW_NAME);
            if (TEST_WORKFLOW_NAME.equals(TEST_WORKFLOW_NAME_1)) {
                workflow.addInput("input1", createInputFile(LINE_TEST_FILE_1));
                workflow.addInput("input2", createInputFile(LINE_TEST_FILE_2));
            } else {
                final URL scatterplotInputURL = WorkflowRunnerVersion2.class.getResource("ScatterplotInput.txt");
                workflow.addInput("input1", new File(scatterplotInputURL.toURI()));
            }
            workflowEngine.runWorkflow(workflow);
            checkWorkflowOutput(workflow);
            final double durationSeconds = (System.currentTimeMillis() - startTime) / (float) MILLISECONDS_PER_SECOND;
            logger.info("");
            logger.info(String.format("Running the workflow took %1.2f seconds.", durationSeconds));
        } catch (final InterruptedException | IOException | URISyntaxException e) {
            logger.error("Exception while running workflow {}.", TEST_WORKFLOW_NAME, e);
        }
    }
    // CHECKSTYLE_OFF: UncommentedMain

    /**
     * Create a test file with a single line.
     *
     * @param line the line to write to the test file.
     * @return the test file.
     */
    private static File createInputFile(final String line) {
        try {
            final File tempFile = File.createTempFile("workflow-runner", ".txt");
            try (final Writer writer = new OutputStreamWriter(new FileOutputStream(tempFile), StandardCharsets.UTF_8)) {
                writer.write(line);
            }
            return tempFile;
        } catch (final IOException e) {
            logger.error("Exception while creating a test input file.", e);
            throw new RuntimeException(e);
        }
    }

    private static void checkWorkflowOutput(final Workflow workflow) throws IOException {
        final Object output = workflow.getOutput("output");
        if (output instanceof File) {
            final File outputFile = (File) output;
            final List<String> lines = Files.readLines(outputFile, Charsets.UTF_8);
            if (Arrays.asList(LINE_TEST_FILE_1, LINE_TEST_FILE_2).equals(lines)) {
                logger.info("- Concatenated file contains the lines we expected!!!");
                logger.info("  actual: " + Joiner.on(" | ").join(lines));
            } else {
                logger.error("- Concatenated file does not contain the lines we expected!");
                logger.error("  expected: " + LINE_TEST_FILE_1 + " | " + LINE_TEST_FILE_2);
                logger.error("  actual:   " + Joiner.on(" | ").join(lines));
            }
            if (!outputFile.delete())
                logger.error("Deleting output file {} failed (after checking contents).", outputFile.getAbsolutePath());
        } else
            logger.error("There is no output parameter named \"output\" of type File.");
    }
}