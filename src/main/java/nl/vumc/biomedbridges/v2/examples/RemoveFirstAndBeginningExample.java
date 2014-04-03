/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.biomedbridges.v2.examples;

import java.io.File;
import java.io.IOException;

import nl.vumc.biomedbridges.v2.core.FileUtils;
import nl.vumc.biomedbridges.v2.core.Workflow;
import nl.vumc.biomedbridges.v2.core.WorkflowEngine;
import nl.vumc.biomedbridges.v2.core.WorkflowFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains a simple example of the workflow running functionality: the "remove first and beginning" workflow
 * removes a number of lines from the input file and removes a number of characters from the start of the remaining
 * lines.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class RemoveFirstAndBeginningExample extends BaseExample {
    /**
     * The name of the first test workflow.
     */
    public static final String TEST_WORKFLOW_NAME = "TestWorkflowRemoveFirstAndBeginning";

    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(RemoveFirstAndBeginningExample.class);

    /**
     * Hidden constructor. The main method below will run this example.
     */
    private RemoveFirstAndBeginningExample() {
    }

    /**
     * Main method.
     *
     * @param arguments unused command-line arguments.
     */
    // CHECKSTYLE_OFF: UncommentedMain
    public static void main(final String[] arguments) {
        new RemoveFirstAndBeginningExample().runExample();
    }
    // CHECKSTYLE_OFF: UncommentedMain

    /**
     * Run this example workflow: remove a number of lines from the input file and remove a number of characters from
     * the start of the remaining lines.
     */
    public void runExample() {
        try {
            initializeExample(logger, "WorkflowRunnerVersion2.main");

            //final String workflowType = WorkflowFactory.DEMONSTRATION_TYPE;
            final String workflowType = WorkflowFactory.GALAXY_TYPE;
            final WorkflowEngine workflowEngine = WorkflowFactory.getWorkflowEngine(workflowType);
            final Workflow workflow = WorkflowFactory.getWorkflow(workflowType, TEST_WORKFLOW_NAME);

            workflow.addInput("input", FileUtils.createInputFile("First line", "Second line", "Third line"));

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
//            final List<String> lines = Files.readLines(outputFile, Charsets.UTF_8);
//            final String lineSeparator = " | ";
//            if (Arrays.asList(LINE_TEST_FILE_1, LINE_TEST_FILE_2).equals(lines)) {
//                logger.info("- Concatenated file contains the lines we expected!!!");
//                logger.info("  actual: " + Joiner.on(lineSeparator).join(lines));
//            } else {
//                logger.error("- Concatenated file does not contain the lines we expected!");
//                logger.error("  expected: " + LINE_TEST_FILE_1 + lineSeparator + LINE_TEST_FILE_2);
//                logger.error("  actual:   " + Joiner.on(lineSeparator).join(lines));
//            }
            if (!outputFile.delete())
                logger.error("Deleting output file {} failed (after checking contents).", outputFile.getAbsolutePath());
        } else
            logger.error("There is no output parameter named \"output\" of type File.");
    }
}
