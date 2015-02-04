/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.examples;

import com.google.inject.Guice;
import com.google.inject.Inject;

import java.io.IOException;
import java.util.Arrays;

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
 * This class contains a simple example of the workflow running functionality: the concatenate workflow combines two
 * input files into one output file.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class ConcatenateExample extends AbstractBaseExample {
    /**
     * The name of the Galaxy history.
     */
    protected static final String HISTORY_NAME = "Concatenate History";

    /**
     * The name of the output dataset.
     */
    protected static final String OUTPUT_NAME = "Concatenate datasets on data 1 and data 2";

    /**
     * Line for test file 1.
     */
    protected static final String LINE_TEST_FILE_1 = "Hello workflow engine!!!";

    /**
     * Line for test file 2.
     */
    protected static final String LINE_TEST_FILE_2 = "Do you wanna play?";

    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(ConcatenateExample.class);

    /**
     * Construct the concatenate example.
     *
     * @param workflowEngineFactory the workflow engine factory to use.
     * @param workflowFactory       the workflow factory to use.
     */
    @Inject
    public ConcatenateExample(final WorkflowEngineFactory workflowEngineFactory, final WorkflowFactory workflowFactory) {
        super(workflowEngineFactory, workflowFactory);
    }

    /**
     * Main method.
     *
     * @param arguments unused command-line arguments.
     */
    // CHECKSTYLE_OFF: UncommentedMain
    public static void main(final String[] arguments) {
        Guice.createInjector(new DefaultGuiceModule()).getInstance(ConcatenateExample.class)
                .runExample(Constants.CENTRAL_GALAXY_URL);
    }
    // CHECKSTYLE_ON: UncommentedMain

    /**
     * Run this example workflow: combine two input files into one output file.
     *
     * @param galaxyInstanceUrl the URL of the Galaxy instance to use.
     * @return whether the workflow ran successfully.
     */
    @Override
    public boolean runExample(final String galaxyInstanceUrl) {
        initializeExample(logger, "ConcatenateExample.runExample");

        final GalaxyConfiguration galaxyConfiguration = new GalaxyConfiguration().setDebug(httpLogging);
        galaxyConfiguration.buildConfiguration(galaxyInstanceUrl, null, HISTORY_NAME);

        final Workflow workflow = workflowFactory.getWorkflow(WorkflowType.GALAXY, galaxyConfiguration,
                                                              Constants.CONCATENATE_WORKFLOW);

        workflow.addInput("WorkflowInput1", FileUtils.createTemporaryFile(LINE_TEST_FILE_1));
        workflow.addInput("WorkflowInput2", FileUtils.createTemporaryFile(LINE_TEST_FILE_2));

        boolean result = false;
        try {
            result = workflow.run();
            if (!result)
                logger.error("Error while running workflow {}.", workflow.getName());
            result &= checkWorkflowSingleOutput(workflow, OUTPUT_NAME, Arrays.asList(LINE_TEST_FILE_1, LINE_TEST_FILE_2));
        } catch (final InterruptedException | IOException e) {
            logger.error("Exception while running workflow {}.", workflow.getName(), e);
        }

        finishExample(logger);
        return result;
    }
}
