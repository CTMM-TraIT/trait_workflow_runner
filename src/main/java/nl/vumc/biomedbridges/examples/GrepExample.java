/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.examples;

import java.io.IOException;
import java.util.Arrays;

import nl.vumc.biomedbridges.core.Constants;
import nl.vumc.biomedbridges.core.DefaultWorkflowEngineFactory;
import nl.vumc.biomedbridges.core.DefaultWorkflowFactory;
import nl.vumc.biomedbridges.core.FileUtils;
import nl.vumc.biomedbridges.core.Workflow;
import nl.vumc.biomedbridges.core.WorkflowEngineFactory;
import nl.vumc.biomedbridges.core.WorkflowFactory;
import nl.vumc.biomedbridges.core.WorkflowType;
import nl.vumc.biomedbridges.galaxy.configuration.GalaxyConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains a simple example of the workflow that searches for a pattern (grep) in the input file.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class GrepExample extends BaseExample {
    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(GrepExample.class);

    /**
     * Construct the grep example.
     *
     * @param workflowEngineFactory the workflow engine factory to use.
     * @param workflowFactory       the workflow factory to use.
     */
//    @Inject
    public GrepExample(final WorkflowEngineFactory workflowEngineFactory, final WorkflowFactory workflowFactory) {
        super(workflowEngineFactory, workflowFactory);
    }

    /**
     * Main method.
     *
     * @param arguments unused command-line arguments.
     */
    // CHECKSTYLE_OFF: UncommentedMain
    public static void main(final String[] arguments) {
        //Guice.createInjector(new DefaultGuiceModule()).getInstance(GrepExample.class).runExample();
        new GrepExample(new DefaultWorkflowEngineFactory(), new DefaultWorkflowFactory()).runExample();
    }
    // CHECKSTYLE_ON: UncommentedMain

    /**
     * Run this example workflow: search for a pattern in the input file.
     *
     * @return whether the workflow ran successfully.
     */
    public boolean runExample() {
        initializeExample(logger, "GrepExample.runExample");

        final String workflowName = "Grep";
        final GalaxyConfiguration galaxyConfiguration = new GalaxyConfiguration().setDebug(true);
        galaxyConfiguration.buildConfiguration(Constants.THE_HYVE_GALAXY_URL, null, workflowName);
        final Workflow workflow = workflowFactory.getWorkflow(WorkflowType.GALAXY, galaxyConfiguration, workflowName);

        final String matchingLine = "10\t55";
        workflow.addInput("input", FileUtils.createTemporaryFile("8\t21", "9\t34", matchingLine, "11\t89", "12\t144"));
        workflow.setParameter(2, "pattern", "5[0-9]");

        boolean result = false;
        try {
            result = workflow.run();
            if (!result)
                logger.error("Error while running workflow {}.", workflow.getName());
            result &= checkWorkflowSingleOutput(workflow, "Select on data 1", Arrays.asList(matchingLine));
        } catch (final InterruptedException | IOException e) {
            logger.error("Exception while running workflow {}.", workflow.getName(), e);
        }

        finishExample(logger);
        return result;
    }
}
