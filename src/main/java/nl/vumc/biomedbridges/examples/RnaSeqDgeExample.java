/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.examples;

import com.google.inject.Guice;
import com.google.inject.Inject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import nl.vumc.biomedbridges.core.Constants;
import nl.vumc.biomedbridges.core.DefaultGuiceModule;
import nl.vumc.biomedbridges.core.Workflow;
import nl.vumc.biomedbridges.core.WorkflowEngine;
import nl.vumc.biomedbridges.core.WorkflowEngineFactory;
import nl.vumc.biomedbridges.core.WorkflowType;
import nl.vumc.biomedbridges.galaxy.HistoryUtils;
import nl.vumc.biomedbridges.galaxy.configuration.GalaxyConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * todo: complete this example and add unit tests.
 *
 * This example calls a workflow todo ...
 *
 * https://testtoolshed.g2.bx.psu.edu/view/yhoogstrate/edger_with_design_matrix
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class RnaSeqDgeExample extends BaseExample {
    /**
     * The resources directory for the examples package.
     */
    protected static final String EXAMPLES_DIRECTORY = Paths.get(
            "src", "test", "resources", "nl", "vumc", "biomedbridges", "examples"
    ) + File.separator;

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
        final RnaSeqDgeExample example = Guice.createInjector(new DefaultGuiceModule()).getInstance(RnaSeqDgeExample.class);

        final String expressionMatrixPathName = EXAMPLES_DIRECTORY + "MCF7_featureCounts_concatenated.txt";
        final String designMatrixPathName = EXAMPLES_DIRECTORY + "design_matrix.txt";
        example.runExample(expressionMatrixPathName, designMatrixPathName);
    }
    // CHECKSTYLE_ON: UncommentedMain

    /**
     * Run this example workflow: todo...
     *
     * @param expressionMatrixPathName the expression matrix input file.
     * @param designMatrixPathName the design matrix input file.
     * @return whether the workflow ran successfully and the output seems to be ok.
     */
    public boolean runExample(final String expressionMatrixPathName, final String designMatrixPathName) {
        initializeExample(logger, "RnaSeqDgeExample.runExample");

        final WorkflowType workflowType = WorkflowType.GALAXY;
        final String contrast = "Control-E2";
        final double fdr = 0.05;

        final GalaxyConfiguration galaxyConfiguration = new GalaxyConfiguration();
        galaxyConfiguration.setDebug(true);
        final String apiKey = galaxyConfiguration.getGalaxyApiKey();
        galaxyConfiguration.buildConfiguration(Constants.VANCIS_GALAXY_URL, apiKey, HISTORY_NAME);
        final WorkflowEngine workflowEngine = workflowEngineFactory.getWorkflowEngine(workflowType, galaxyConfiguration,
                                                                                      new HistoryUtils());
        final Workflow workflow = workflowEngine.getWorkflow(Constants.WORKFLOW_RNA_SEQ_DGE);

        workflow.addInput("expression_matrix", new File(expressionMatrixPathName));
        workflow.addInput("design_matrix", new File(designMatrixPathName));
        final int stepNumberEdgeRDGE = 1;
        // todo: setParameter should log an error or throw an exception if the step number and parameter name do not match.
        workflow.setParameter(stepNumberEdgeRDGE, "contrast", contrast);
        workflow.setParameter(stepNumberEdgeRDGE, "fdr", fdr);
        workflow.setParameter(stepNumberEdgeRDGE, "output_format_images", "png");
        final String selectedOutputs = "[\"make_output_MDSplot\", \"make_output_PValue_distribution_plot\", "
                                       + "\"make_output_heatmap_plot\"]";
        workflow.setParameter(stepNumberEdgeRDGE, "outputs", selectedOutputs);

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
        final int expectedOutputCount = 7;
        return workflow.getOutputMap().size() == expectedOutputCount;
    }
}
