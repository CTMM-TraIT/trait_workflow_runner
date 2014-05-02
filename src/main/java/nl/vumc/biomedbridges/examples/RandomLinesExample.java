/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.biomedbridges.examples;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.util.List;

import nl.vumc.biomedbridges.core.Constants;
import nl.vumc.biomedbridges.core.FileUtils;
import nl.vumc.biomedbridges.core.Workflow;
import nl.vumc.biomedbridges.core.WorkflowEngine;
import nl.vumc.biomedbridges.core.WorkflowEngineFactory;
import nl.vumc.biomedbridges.galaxy.configuration.GalaxyConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This example calls a workflow which consists of two calls to the random lines tool, which randomly selects a number
 * of lines from an input file.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class RandomLinesExample extends BaseExample {
    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(RandomLinesExample.class);

    /**
     * The name of the Galaxy history.
     */
    private static final String HISTORY_NAME = "Random Lines History";

    /**
     * The name of the input dataset.
     */
    private static final String INPUT_NAME = "Input Dataset";

    /**
     * The name of the output dataset.
     */
    private static final String OUTPUT_NAME = "Select random lines on data 2";

    /**
     * The name of the line count parameter that can be specified for step 2 and 3.
     */
    private static final String LINE_COUNT_PARAMETER_NAME = "num_lines";

    /**
     * The initial number of lines the input file gets reduced to.
     */
    private static final int INITIAL_LINE_COUNT = 6;

    /**
     * The definitive number of lines the intermediate file gets reduced to.
     */
    private static final int DEFINITIVE_LINE_COUNT = 3;

    /**
     * Hidden constructor. The main method below will run this example.
     */
    private RandomLinesExample() {
    }

    /**
     * Main method.
     *
     * @param arguments unused command-line arguments.
     */
    // CHECKSTYLE_OFF: UncommentedMain
    public static void main(final String[] arguments) {
        new RandomLinesExample().runExample();
    }
    // CHECKSTYLE_ON: UncommentedMain

    /**
     * Run this example workflow: randomly select a number of lines from an input file twice.
     */
    public void runExample() {
        initializeExample(logger, "RandomLinesExample.runExample");

        final String workflowType = WorkflowEngineFactory.GALAXY_TYPE;
        final String apiKey = GalaxyConfiguration.getGalaxyApiKey();
        final String configuration = GalaxyConfiguration.buildConfiguration(GALAXY_INSTANCE_URL, apiKey, HISTORY_NAME);
        final WorkflowEngine workflowEngine = WorkflowEngineFactory.getWorkflowEngine(workflowType, configuration);
        final Workflow workflow = workflowEngine.getWorkflow(Constants.WORKFLOW_RANDOM_LINES_TWICE);

        workflow.addInput(INPUT_NAME, FileUtils.createInputFile("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"));
        final int stepIdFirstFilter = 2;
        final int stepIdSecondFilter = 3;
        workflow.setParameter(stepIdFirstFilter, LINE_COUNT_PARAMETER_NAME, INITIAL_LINE_COUNT);
        workflow.setParameter(stepIdSecondFilter, LINE_COUNT_PARAMETER_NAME, DEFINITIVE_LINE_COUNT);

        try {
            if (!workflowEngine.runWorkflow(workflow))
                logger.error("Error while running workflow {}.", Constants.WORKFLOW_RANDOM_LINES_TWICE);
            checkWorkflowOutput(workflow);
        } catch (final InterruptedException | IOException e) {
            logger.error("Exception while running workflow {}.", Constants.WORKFLOW_RANDOM_LINES_TWICE, e);
        }

        finishExample(logger);
    }

    /**
     * Check the output after running the workflow.
     *
     * @param workflow the workflow that has been executed.
     * @throws IOException if reading an output file fails.
     */
    private static void checkWorkflowOutput(final Workflow workflow) throws IOException {
        final Object output = workflow.getOutputMap().get(OUTPUT_NAME);
        if (output instanceof File) {
            final File outputFile = (File) output;
            logger.trace("Reading output file {}.", outputFile.getAbsolutePath());
            final List<String> lines = Files.readLines(outputFile, Charsets.UTF_8);
            if (lines.size() == DEFINITIVE_LINE_COUNT)
                logger.trace("The number of lines ({}) is as expected.", lines.size());
            else
                logger.error("The number of lines ({}) is not as expected ({}).", lines.size(), DEFINITIVE_LINE_COUNT);
            for (final String line : lines)
                logger.trace(line);
            if (!outputFile.delete())
                logger.error("Deleting output file {} failed (after checking contents).", outputFile.getAbsolutePath());
        } else
            logger.error("There is no output parameter {} of type file.", OUTPUT_NAME);
    }
}
