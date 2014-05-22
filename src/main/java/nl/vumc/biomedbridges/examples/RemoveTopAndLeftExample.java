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
import java.util.List;

import nl.vumc.biomedbridges.core.DefaultWorkflowEngineFactory;
import nl.vumc.biomedbridges.core.FileUtils;
import nl.vumc.biomedbridges.core.Workflow;
import nl.vumc.biomedbridges.core.WorkflowEngine;
import nl.vumc.biomedbridges.core.WorkflowEngineFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains a simple example of the workflow running functionality: the "remove top and left" workflow
 * removes a number of lines from the input file and removes a number of characters from the start of the remaining
 * lines.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class RemoveTopAndLeftExample extends BaseExample {
    /**
     * The name of the "remove top and left" workflow.
     */
    public static final String WORKFLOW_NAME = "TestWorkflowRemoveTopAndLeft";
//    public static final String WORKFLOW_NAME = "RemoveLeftAndTop";

    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(RemoveTopAndLeftExample.class);

    /**
     * Hidden constructor. The main method below will run this example.
     */
    private RemoveTopAndLeftExample() {
    }

    /**
     * Main method.
     *
     * @param arguments unused command-line arguments.
     */
    // CHECKSTYLE_OFF: UncommentedMain
    public static void main(final String[] arguments) {
        new RemoveTopAndLeftExample().runExample();
    }
    // CHECKSTYLE_ON: UncommentedMain

    /**
     * Run this example workflow: remove a number of lines from the input file and remove a number of characters from
     * the start of the remaining lines.
     */
    public void runExample() {
        try {
            initializeExample(logger, "RemoveTopAndLeftExample.main");

            //final String workflowType = WorkflowEngineFactory.DEMONSTRATION_TYPE;
            final String workflowType = WorkflowEngineFactory.GALAXY_TYPE;
            final WorkflowEngine workflowEngine = new DefaultWorkflowEngineFactory().getWorkflowEngine(workflowType);
            final Workflow workflow = workflowEngine.getWorkflow(WORKFLOW_NAME);

            workflow.addInput("input", FileUtils.createTemporaryFile("First line", "Second line", "Third line"));

            workflowEngine.runWorkflow(workflow);

            checkWorkflowOutput(workflow);

            finishExample(logger);
        } catch (final InterruptedException | IOException e) {
            logger.error("Exception while running workflow {}.", WORKFLOW_NAME, e);
        }
    }

    /**
     * Check the output after running the workflow.
     *
     * @param workflow the workflow that has been executed.
     * @throws java.io.IOException if reading an output file fails.
     */
    private static void checkWorkflowOutput(final Workflow workflow) throws IOException {
        final String outputName = "output";
        final Object output = workflow.getOutput(outputName);
        if (output instanceof File) {
            final File outputFile = (File) output;
            final List<String> lines = Files.readLines(outputFile, Charsets.UTF_8);
            final String lineSeparator = " | ";
            logger.info("- Output file contains the following lines:");
            logger.info("  " + Joiner.on(lineSeparator).join(lines));
            if (!outputFile.delete())
                logger.error("Deleting output file {} failed (after checking contents).", outputFile.getAbsolutePath());
        } else
            logger.error("There is no output file named \"" + outputName + "\" of type File.");
    }
}
