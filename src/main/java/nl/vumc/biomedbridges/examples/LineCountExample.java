/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.examples;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.io.Files;
import com.google.inject.Guice;
import com.google.inject.Inject;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import nl.vumc.biomedbridges.core.Constants;
import nl.vumc.biomedbridges.core.DefaultGuiceModule;
import nl.vumc.biomedbridges.core.FileUtils;
import nl.vumc.biomedbridges.core.Workflow;
import nl.vumc.biomedbridges.core.WorkflowEngineFactory;
import nl.vumc.biomedbridges.core.WorkflowFactory;
import nl.vumc.biomedbridges.core.WorkflowType;
import nl.vumc.biomedbridges.galaxy.configuration.GalaxyConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains a simple example of the workflow running functionality: the line, word, and character count
 * workflow returns these statistics for the input file.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class LineCountExample extends BaseExample {
    /**
     * The name of the Galaxy history.
     */
    protected static final String HISTORY_NAME = "Line, word, and character count history";

    /**
     * The name of the output dataset.
     */
    protected static final String OUTPUT_NAME = "Line/Word/Character count on data 1";

    /**
     * Expected line 1 for the output file.
     */
    protected static final String LINE_OUTPUT_FILE_1 = "#lines\twords\tcharacters";

    /**
     * Expected line 2 for the output file.
     */
    protected static final String LINE_OUTPUT_FILE_2 = "13052\t107533\t594916";

    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(LineCountExample.class);

    /**
     * Construct the line count example.
     *
     * @param workflowEngineFactory the workflow engine factory to use.
     * @param workflowFactory       the workflow factory to use.
     */
    @Inject
    public LineCountExample(final WorkflowEngineFactory workflowEngineFactory, final WorkflowFactory workflowFactory) {
        super(workflowEngineFactory, workflowFactory);
    }

    /**
     * Main method.
     *
     * @param arguments unused command-line arguments.
     */
    // CHECKSTYLE_OFF: UncommentedMain
    public static void main(final String[] arguments) {
        Guice.createInjector(new DefaultGuiceModule()).getInstance(LineCountExample.class).runExample();
    }
    // CHECKSTYLE_ON: UncommentedMain

    /**
     * Run this example workflow: count lines, words, and characters in the input file.
     *
     * @return whether the workflow ran successfully.
     */
    public boolean runExample() {
        initializeExample(logger, "LineCountExample.runExample");

        final GalaxyConfiguration galaxyConfiguration = new GalaxyConfiguration().setDebug(true);
        galaxyConfiguration.buildConfiguration(Constants.VANCIS_GALAXY_URL, null, HISTORY_NAME);

        final Workflow workflow = workflowFactory.getWorkflow(WorkflowType.GALAXY, galaxyConfiguration,
                                                              Constants.LINE_COUNT_WORKFLOW);

        boolean result = false;
        final String bookLink = "https://www.gutenberg.org/ebooks/1661.txt.utf-8";

        try {
            workflow.addInput("Input Dataset", FileUtils.createTemporaryFileFromURL(new URL(bookLink)));

            result = workflow.run();
            if (!result)
                logger.error("Error while running workflow {}.", workflow.getName());
            result &= checkWorkflowOutput(workflow);
        } catch (final InterruptedException | IOException e) {
            logger.error("Exception while running workflow {}.", workflow.getName(), e);
        }

        finishExample(logger);
        return result;
    }

    /**
     * Check the output after running the workflow.
     *
     * @param workflow the workflow that has been executed.
     * @return whether the workflow output is correct.
     */
    private static boolean checkWorkflowOutput(final Workflow workflow) throws IOException {
        boolean result = false;
        final Object output = workflow.getOutput(OUTPUT_NAME);
        if (output instanceof File) {
            final File outputFile = (File) output;
            final List<String> lines = Files.readLines(outputFile, Charsets.UTF_8);
            final String lineSeparator = " | ";
            if (Arrays.asList(LINE_OUTPUT_FILE_1, LINE_OUTPUT_FILE_2).equals(lines)) {
                result = true;
                logger.info("- The output file contains the lines we expected!!!");
                logger.info("  actual: " + Joiner.on(lineSeparator).join(lines));
            } else {
                logger.error("- The output file does not contain the lines we expected!");
                logger.error("  expected: " + LINE_OUTPUT_FILE_1 + lineSeparator + LINE_OUTPUT_FILE_2);
                logger.error("  actual:   " + Joiner.on(lineSeparator).join(lines));
            }
            final boolean deleteResult = outputFile.delete();
            result &= deleteResult;
            if (!deleteResult)
                logger.error("Deleting output file {} failed (after checking contents).", outputFile.getAbsolutePath());
        } else
            logger.error("There is no output file named {}.", OUTPUT_NAME);
        return result;
    }
}
