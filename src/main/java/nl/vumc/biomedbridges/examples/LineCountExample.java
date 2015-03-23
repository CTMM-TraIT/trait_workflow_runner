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
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(LineCountExample.class);

    /**
     * todo: on some Galaxy servers, the character count is slightly different; for the moment, this flag tries to fix it.
     */
    private boolean fixExpectedOutput;

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
        Guice.createInjector(new DefaultGuiceModule()).getInstance(LineCountExample.class)
                .runExample(Constants.VANCIS_PRO_GALAXY_URL);
    }
    // CHECKSTYLE_ON: UncommentedMain

    /**
     * Set the fix flag for the expected output.
     *
     * @param fixExpectedOutput the fix flag for the expected output.
     */
    public void setFixExpectedOutput(final boolean fixExpectedOutput) {
        this.fixExpectedOutput = fixExpectedOutput;
    }

    @Override
    public boolean runExample(final String galaxyInstanceUrl) {
        initializeExample(logger, "LineCountExample.runExample");

        final GalaxyConfiguration galaxyConfiguration = new GalaxyConfiguration().setDebug(httpLogging);
        galaxyConfiguration.buildConfiguration(galaxyInstanceUrl, null, HISTORY_NAME);

        final Workflow workflow = workflowFactory.getWorkflow(WorkflowType.GALAXY, galaxyConfiguration,
                                                              Constants.LINE_COUNT_WORKFLOW);

        // Use a book classic to do some counting: The Adventures of Sherlock Holmes, by Arthur Conan Doyle.
        final String bookLink = "https://www.gutenberg.org/ebooks/1661.txt.utf-8";

        try {
            workflow.addInput("Input Dataset", FileUtils.createTemporaryFileFromURL(new URL(bookLink)));

            workflow.run();
            checkWorkflowSingleOutput(workflow, OUTPUT_NAME, getExpectedLines());
        } catch (final InterruptedException | IOException e) {
            logger.error("Exception while running workflow {}.", workflow.getName(), e);
        }

        return finishExample(workflow);
    }

    /**
     * Get the expected output lines.
     *
     * @return the expected output lines.
     */
    private List<String> getExpectedLines() {
        return Arrays.asList("#lines\twords\tcharacters", "13052\t107533\t5949" + (fixExpectedOutput ? "16" : "33"));
    }
}
