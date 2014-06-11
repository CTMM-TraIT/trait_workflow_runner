/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.examples;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import java.io.File;
import java.io.IOException;
import java.util.List;

import nl.vumc.biomedbridges.core.Constants;
import nl.vumc.biomedbridges.core.DefaultGuiceModule;
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
     * The name of the output dataset.
     */
    public static final String OUTPUT_NAME = "Select random lines on data 2";

    /**
     * The initial number of lines the input file gets reduced to.
     */
    protected static final int INITIAL_LINE_COUNT = 6;

    /**
     * The definitive number of lines the intermediate file gets reduced to.
     */
    protected static final int DEFINITIVE_LINE_COUNT = 3;

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
     * The name of the line count parameter that can be specified for step 2 and 3.
     */
    private static final String LINE_COUNT_PARAMETER_NAME = "num_lines";

    /**
     * The definitive line count the input file is limited to.
     */
    private int definitiveLineCount;

    /**
     * Construct the random lines example.
     *
     * @param workflowEngineFactory the workflow engine factory to use.
     */
    @Inject
    protected RandomLinesExample(final WorkflowEngineFactory workflowEngineFactory) {
        super(workflowEngineFactory);
    }

    /**
     * Main method.
     *
     * @param arguments unused command-line arguments.
     */
    // CHECKSTYLE_OFF: UncommentedMain
    public static void main(final String[] arguments) {
        // Create a Guice injector and use it to build the RandomLinesExample object.
        final Injector injector = Guice.createInjector(new DefaultGuiceModule());
        final RandomLinesExample randomLinesExample = injector.getInstance(RandomLinesExample.class);

        randomLinesExample.runExample(WorkflowEngineFactory.GALAXY_TYPE, INITIAL_LINE_COUNT, DEFINITIVE_LINE_COUNT);
    }
    // CHECKSTYLE_ON: UncommentedMain

    /**
     * Run this example workflow: randomly select a number of lines from an input file twice.
     *
     * @param workflowType the workflow (engine) type to use.
     * @param initialLineCount the initial line count the input file is limited to.
     * @param definitiveLineCount the definitive line count the input file is limited to.
     * @return the output lines if the workflow ran successfully or else null.
     */
    public List<String> runExample(final String workflowType, final int initialLineCount, final int definitiveLineCount) {
        List<String> outputLines = null;
        initializeExample(logger, "RandomLinesExample.runExample");

        this.definitiveLineCount = definitiveLineCount;
        final String apiKey = GalaxyConfiguration.getGalaxyApiKey();
        // Note: the configuration will be ignored for the demonstration workflow type.
        final String configuration = GalaxyConfiguration.buildConfiguration(GALAXY_INSTANCE_URL, apiKey, HISTORY_NAME);
        final WorkflowEngine workflowEngine = workflowEngineFactory.getWorkflowEngine(workflowType, configuration);
        final Workflow workflow = workflowEngine.getWorkflow(Constants.WORKFLOW_RANDOM_LINES_TWICE);

        workflow.addInput(INPUT_NAME, FileUtils.createTemporaryFile("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"));
        final int stepIdFirstFilter = 2;
        final int stepIdSecondFilter = 3;
        workflow.setParameter(stepIdFirstFilter, LINE_COUNT_PARAMETER_NAME, initialLineCount);
        workflow.setParameter(stepIdSecondFilter, LINE_COUNT_PARAMETER_NAME, definitiveLineCount);

        boolean result = true;
        try {
            result = workflowEngine.runWorkflow(workflow);
            if (!result)
                logger.error("Error while running workflow {}.", workflow.getName());
            outputLines = checkWorkflowOutput(workflow);
            result &= outputLines != null;
        } catch (final InterruptedException | IOException e) {
            logger.error("Exception while running workflow {}.", workflow.getName(), e);
        }
        finishExample(logger);
        return result ? outputLines : null;
    }

    /**
     * Check the output after running the workflow.
     *
     * @param workflow the workflow that has been executed.
     * @return the output lines if the workflow output is correct or else null.
     * @throws IOException if reading an output file fails.
     */
    private List<String> checkWorkflowOutput(final Workflow workflow) throws IOException {
        List<String> result = null;
        final Object output = workflow.getOutputMap().get(OUTPUT_NAME);
        if (output instanceof File) {
            final File outputFile = (File) output;
            logger.trace("Reading output file {}.", outputFile.getAbsolutePath());
            final List<String> lines = Files.readLines(outputFile, Charsets.UTF_8);
            if (lines.size() == definitiveLineCount) {
                result = lines;
                logger.trace("The number of lines ({}) is as expected.", lines.size());
            } else
                logger.error("The number of lines ({}) is not as expected ({}).", lines.size(), definitiveLineCount);
            for (final String line : lines)
                logger.trace(line);
            if (!outputFile.delete()) {
                result = null;
                logger.error("Deleting output file {} failed (after checking contents).", outputFile.getAbsolutePath());
            }
        } else
            logger.error("There is no output parameter {} of type file.", OUTPUT_NAME);
        return result;
    }
}
