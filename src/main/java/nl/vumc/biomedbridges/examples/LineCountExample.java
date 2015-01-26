/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.examples;

import com.google.inject.Guice;
import com.google.inject.Inject;

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
     * Expected lines for the output file.
     */
    private static final List<String> EXPECTED_OUTPUT_LINES = Arrays.asList("#lines\twords\tcharacters",
                                                                            "13052\t107533\t594916");

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
        galaxyConfiguration.buildConfiguration(Constants.VANCIS_PRO_GALAXY_URL, null, HISTORY_NAME);

        final Workflow workflow = workflowFactory.getWorkflow(WorkflowType.GALAXY, galaxyConfiguration,
                                                              Constants.LINE_COUNT_WORKFLOW);

        boolean result = false;
        // Use a book classic to do some counting: The Adventures of Sherlock Holmes, by Arthur Conan Doyle.
        final String bookLink = "https://www.gutenberg.org/ebooks/1661.txt.utf-8";

        try {
            workflow.addInput("Input Dataset", FileUtils.createTemporaryFileFromURL(new URL(bookLink)));

            result = workflow.run();
            if (!result)
                logger.error("Error while running workflow {}.", workflow.getName());
            result &= checkWorkflowSingleOutput(workflow, OUTPUT_NAME, EXPECTED_OUTPUT_LINES);
        } catch (final InterruptedException | IOException e) {
            logger.error("Exception while running workflow {}.", workflow.getName(), e);
        }

        finishExample(logger);
        return result;
    }
}
