/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.biomedbridges.examples;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import nl.vumc.biomedbridges.core.FileUtils;
import nl.vumc.biomedbridges.core.Workflow;
import nl.vumc.biomedbridges.core.WorkflowEngine;
import nl.vumc.biomedbridges.core.WorkflowEngineFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains a simple example of the workflow running functionality: the histogram workflow creates a pdf file
 * with a histogram from an input file.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class HistogramExample extends BaseExample {
    /**
     * The name of the first test workflow.
     */
    public static final String TEST_WORKFLOW_NAME = "TestWorkflowConcatenate";

    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(HistogramExample.class);

    /**
     * Line for test file 1.
     */
    private static final String LINE_TEST_FILE_1 = "Hello workflow engine!!!";

    /**
     * Line for test file 2.
     */
    private static final String LINE_TEST_FILE_2 = "Do you wanna play?";

    /**
     * Hidden constructor. The main method below will run this example.
     */
    private HistogramExample() {
    }

    /**
     * Main method.
     *
     * @param arguments unused command-line arguments.
     */
    // CHECKSTYLE_OFF: UncommentedMain
    public static void main(final String[] arguments) {
        new HistogramExample().runExample();
    }
    // CHECKSTYLE_ON: UncommentedMain

    /**
     * Run this example workflow: combine two input files into one output file.
     */
    public void runExample() {
        try {
            initializeExample(logger, "ConcatenateExample.runExample");

            //final String workflowType = WorkflowEngineFactory.DEMONSTRATION_TYPE;
            final String workflowType = WorkflowEngineFactory.GALAXY_TYPE;
            final WorkflowEngine workflowEngine = WorkflowEngineFactory.getWorkflowEngine(workflowType);
            final Workflow workflow = workflowEngine.getWorkflow(TEST_WORKFLOW_NAME);

            workflow.addInput("input1", FileUtils.createInputFile(LINE_TEST_FILE_1));
            workflow.addInput("input2", FileUtils.createInputFile(LINE_TEST_FILE_2));
            workflowEngine.runWorkflow(workflow);
            checkWorkflowOutput(workflow);

            finishExample(logger);
        } catch (final InterruptedException | IOException e) {
            logger.error("Exception while running workflow {}.", TEST_WORKFLOW_NAME, e);
        }
    }

    /**
     * Check the output after running the workflow.
     *
     * @param workflow the workflow that has been executed.
     * @throws java.io.IOException if reading an output file fails.
     */
    private static void checkWorkflowOutput(final Workflow workflow) throws IOException {
        final Object output = workflow.getOutput("output");
        if (output instanceof File) {
            final File outputFile = (File) output;
            final List<String> lines = Files.readLines(outputFile, Charsets.UTF_8);
            final String lineSeparator = " | ";
            if (Arrays.asList(LINE_TEST_FILE_1, LINE_TEST_FILE_2).equals(lines)) {
                logger.info("- Concatenated file contains the lines we expected!!!");
                logger.info("  actual: " + Joiner.on(lineSeparator).join(lines));
            } else {
                logger.error("- Concatenated file does not contain the lines we expected!");
                logger.error("  expected: " + LINE_TEST_FILE_1 + lineSeparator + LINE_TEST_FILE_2);
                logger.error("  actual:   " + Joiner.on(lineSeparator).join(lines));
            }
            if (!outputFile.delete())
                logger.error("Deleting output file {} failed (after checking contents).", outputFile.getAbsolutePath());
        } else
            logger.error("There is no output parameter named \"output\" of type File.");
    }
}
