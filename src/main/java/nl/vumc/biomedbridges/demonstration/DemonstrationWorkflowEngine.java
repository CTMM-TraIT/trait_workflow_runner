/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.demonstration;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.io.Files;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import nl.vumc.biomedbridges.core.Constants;
import nl.vumc.biomedbridges.core.Workflow;
import nl.vumc.biomedbridges.core.WorkflowEngine;
import nl.vumc.biomedbridges.examples.RandomLinesExample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a very simple implementation of the WorkflowEngine interface.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 *
 * This class was created during the BioMedBridges annual general meeting on March 12th, 2014. The original
 * demonstration of the Workflow Runner was impossible because several Galaxy engines were down:
 *
 * https://usegalaxy.org/
 * Galaxy could not be reached
 * The filesystem which serves Galaxy datasets is currently unavailable due to a power interruption in the data center
 * at TACC. Galaxy will be offline until this filesystem is available again. It is estimated that it will return to
 * production service on Monday, March 10.
 *
 * http://galaxy.nbic.nl/workflow/import_workflow
 * Internal Server Error
 * Galaxy was unable to successfully complete your request
 * An error occurred.
 * This may be an intermittent problem due to load or other unpredictable factors, reloading the page may address the
 * problem.
 * The error has been logged to our team.
 */
public class DemonstrationWorkflowEngine implements WorkflowEngine {
    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(DemonstrationWorkflowEngine.class);

    @Override
    public boolean configure() {
        return configure(null);
    }

    @Override
    public boolean configure(final String configurationData) {
        logger.info("The demonstration workflow engine is not (yet) configurable. "
                    + "The configuration data {} is not used.", configurationData);
        return true;
    }

    @Override
    public Workflow getWorkflow(final String workflowName) {
        return new DemonstrationWorkflow(workflowName);
    }

    @Override
    public boolean runWorkflow(final Workflow workflow) {
        boolean result = true;
        final String workflowName = workflow.getName();
        try {
            logger.info("DemonstrationWorkflowEngine.runWorkflow");
            if (Constants.TEST_WORKFLOW_CONCATENATE.equals(workflowName)) {
                logger.info("Running workflow " + workflowName + "...");
                final Object input1 = workflow.getInput("WorkflowInput1");
                final Object input2 = workflow.getInput("WorkflowInput2");
                if (input1 instanceof File && input2 instanceof File) {
                    final String inputString1 = Joiner.on("").join(Files.readLines((File) input1, Charsets.UTF_8));
                    final String inputString2 = Joiner.on("").join(Files.readLines((File) input2, Charsets.UTF_8));
                    logger.info("input 1: " + inputString1);
                    logger.info("input 2: " + inputString2);
                    workflow.addOutput("output", createOutputFile(workflow, Arrays.asList(inputString1, inputString2)));
                    logger.info("output: " + inputString1 + " " + inputString2);
                } else {
                    result = false;
                    logger.error("Input parameters are not of the expected type (two input files where expected).");
                }
            } else if (Constants.WORKFLOW_RANDOM_LINES_TWICE.equals(workflowName)) {
                logger.info("Running workflow " + workflowName + "...");
                final Object input = workflow.getInput("Input Dataset");
                final int initialLineCount = (int) workflow.getParameters().get(2).get("num_lines");
                final int definitiveLineCount = (int) workflow.getParameters().get(3).get("num_lines");
                final Random randomGenerator = new Random(123456);
                if (input instanceof File) {
                    final List<String> lines = Files.readLines((File) input, Charsets.UTF_8);
                    final List<String> selectedLines1 = selectRandomLines(lines, initialLineCount, randomGenerator);
                    final List<String> selectedLines2 = selectRandomLines(selectedLines1, definitiveLineCount, randomGenerator);
                    workflow.addOutput(RandomLinesExample.OUTPUT_NAME, createOutputFile(workflow, selectedLines2));
                    logger.info("output: " + selectedLines2);
                } else {
                    result = false;
                    logger.error("Expected input file was not found.");
                }
            }
        } catch (final IOException e) {
            result = false;
            logger.error("Exception while running workflow {}.", workflowName, e);
        }
        return result;
    }

    /**
     * Select a number of random lines from the list of lines.
     *
     * @param lines the list of lines.
     * @param lineCount the number of lines to select.
     * @param randomGenerator the random generator to use.
     * @return the randomly selected lines.
     */
    private List<String> selectRandomLines(final List<String> lines, final int lineCount, final Random randomGenerator) {
        final List<String> selectedLines = new ArrayList<>();
        final List<Integer> selectedIndices = new ArrayList<>();
        while (selectedIndices.size() < lineCount) {
            final int selectedIndex = randomGenerator.nextInt(lines.size());
            if (!selectedIndices.contains(selectedIndex)) {
                selectedIndices.add(selectedIndex);
                selectedLines.add(lines.get(selectedIndex));
            }
        }
        return selectedLines;
    }

    /**
     * Create an output file with two lines.
     *
     * @param workflow the workflow where this output file is created for.
     * @param lines the lines to write to the output file.
     * @return the test file.
     */
    private static File createOutputFile(final Workflow workflow, final List<String> lines) {
        try {
            final String filenamePrefix = "workflow-runner-" + workflow.getName().toLowerCase() + "-output";
            final File tempFile = File.createTempFile(filenamePrefix, ".txt");
            try (final Writer writer = new OutputStreamWriter(new FileOutputStream(tempFile), StandardCharsets.UTF_8)) {
                for (final String line : lines) {
                    writer.write(line);
                    writer.write('\n');
                }
            }
            return tempFile;
        } catch (final IOException e) {
            logger.error("Exception while creating the output file.", e);
            throw new RuntimeException(e);
        }
    }
}
