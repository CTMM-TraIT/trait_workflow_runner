/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.examples;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import java.io.File;
import java.io.IOException;
import java.util.List;

import nl.vumc.biomedbridges.core.Constants;
import nl.vumc.biomedbridges.core.DefaultGuiceModule;
import nl.vumc.biomedbridges.core.Workflow;
import nl.vumc.biomedbridges.core.WorkflowEngine;
import nl.vumc.biomedbridges.core.WorkflowEngineFactory;
import nl.vumc.biomedbridges.galaxy.configuration.GalaxyConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This example calls a workflow todo ...
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class RnaSeqDgeExample extends BaseExample {
    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(RnaSeqDgeExample.class);

    /**
     * The name of the Galaxy history.
     */
    private static final String HISTORY_NAME = Constants.WORKFLOW_RNA_SEQ_DGE + " History";

    /**
     * Construct the random lines example.
     *
     * @param workflowEngineFactory the workflow engine factory to use.
     */
    @Inject
    protected RnaSeqDgeExample(final WorkflowEngineFactory workflowEngineFactory) {
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
        final RnaSeqDgeExample rnaSeqDgeExample = injector.getInstance(RnaSeqDgeExample.class);

        final String directory = "C:\\Freek\\VUmc\\BioMedBridges\\Galaxy-API\\RNA-Seq EdgeR DGE pipeline EMC\\";
        rnaSeqDgeExample.runExample(WorkflowEngineFactory.GALAXY_TYPE, directory + "MCF7_featureCounts_concatenated.txt",
                                    directory + "design_matrix.txt", "Control-E2");
    }
    // CHECKSTYLE_ON: UncommentedMain

    /**
     * Run this example workflow: todo...
     *
     * @param workflowType the workflow (engine) type to use.
     * @param expressionMatrixPathName the expression matrix input file.
     * @param designMatrixPathName the design matrix input file.
     * @param contrast the contrast parameter.
     * @return whether the workflow ran successfully and the output seems to be ok.
     */
    public boolean runExample(final String workflowType, final String expressionMatrixPathName,
                              final String designMatrixPathName, final String contrast) {
        initializeExample(logger, "RnaSeqDgeExample.runExample");

        final GalaxyConfiguration galaxyConfiguration = new GalaxyConfiguration();
        final String apiKey = galaxyConfiguration.getGalaxyApiKey();
        galaxyConfiguration.buildConfiguration(Constants.TRAIT_GALAXY_INSTANCE_URL, apiKey, HISTORY_NAME);
        final WorkflowEngine workflowEngine = workflowEngineFactory.getWorkflowEngine(workflowType, galaxyConfiguration);
        final Workflow workflow = workflowEngine.getWorkflow(Constants.WORKFLOW_RNA_SEQ_DGE);

        workflow.addInput("Expression Matrix", new File(expressionMatrixPathName));
        workflow.addInput("Design Matrix", new File(designMatrixPathName));
        final int stepNumberEdgeRDGE = 1;
        // todo: setParameter should log an error or throw an exception if the step number and parameter name do not match.
        workflow.setParameter(stepNumberEdgeRDGE, "contrast", contrast);

        boolean result = true;
        try {
            result = workflowEngine.runWorkflow(workflow);
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
     * @return whether the output seems to be ok.
     * @throws IOException if reading an output file fails.
     */
    private boolean checkWorkflowOutput(final Workflow workflow) throws IOException {
        return workflow.getOutputMap().size() == 7;
    }
}
