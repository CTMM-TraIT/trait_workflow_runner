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
import nl.vumc.biomedbridges.core.WorkflowEngineFactory;
import nl.vumc.biomedbridges.core.WorkflowFactory;
import nl.vumc.biomedbridges.core.WorkflowType;
import nl.vumc.biomedbridges.galaxy.configuration.GalaxyConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * todo: complete this example and add unit tests.
 *
 * This example calls a RNA-Seq workflow todo ...
 *
 * The tool for this workflow can be found in this tool shed:
 * https://testtoolshed.g2.bx.psu.edu/view/yhoogstrate/edger_with_design_matrix
 *
 * You can run this example using the following Maven command:
 * mvn compile exec:java -Dexec.mainClass="nl.vumc.biomedbridges.examples.RnaSeqDgeExample"
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class RnaSeqDgeExample extends AbstractBaseExample {
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
     * The file path to the expression matrix.
     */
    private static final String EXPRESSION_MATRIX_PATH_NAME = EXAMPLES_DIRECTORY + "MCF7_featureCounts_concatenated.txt";

    /**
     * The file path to the design matrix.
     */
    private static final String DESIGN_MATRIX_PATH_NAME = EXAMPLES_DIRECTORY + "design_matrix.txt";

    /**
     * The name of the Galaxy history.
     */
    private static final String HISTORY_NAME = Constants.WORKFLOW_RNA_SEQ_DGE + " History";

    /**
     * Construct the RNA-Seq example.
     *
     * @param workflowEngineFactory the workflow engine factory to use.
     * @param workflowFactory       the workflow factory to use.
     */
    @Inject
    public RnaSeqDgeExample(final WorkflowEngineFactory workflowEngineFactory, final WorkflowFactory workflowFactory) {
        super(workflowEngineFactory, workflowFactory);
    }

    /**
     * Main method.
     *
     * @param arguments unused command-line arguments.
     */
    // CHECKSTYLE_OFF: UncommentedMain
    public static void main(final String[] arguments) {
        Guice.createInjector(new DefaultGuiceModule()).getInstance(RnaSeqDgeExample.class)
                .runExample(Constants.VANCIS_PRO_GALAXY_URL);
//                .runExample(Constants.THE_HYVE_GALAXY_URL);
    }
    // CHECKSTYLE_ON: UncommentedMain

    @Override
    public boolean runExample(final String galaxyInstanceUrl) {
        initializeExample(logger, "RnaSeqDgeExample.runExample");

        final String contrast = "Control-E2";
        final double fdr = 0.05;

        final GalaxyConfiguration configuration = new GalaxyConfiguration().setDebug(httpLogging);
        configuration.buildConfiguration(galaxyInstanceUrl, null, HISTORY_NAME);
        final Workflow workflow = workflowFactory.getWorkflow(WorkflowType.GALAXY, configuration, Constants.WORKFLOW_RNA_SEQ_DGE);

        workflow.addInput("expression_matrix", new File(EXPRESSION_MATRIX_PATH_NAME));
        workflow.addInput("design_matrix", new File(DESIGN_MATRIX_PATH_NAME));
        final int stepNumberEdgeRDGE = 1;
        // todo: setParameter should log an error or throw an exception if the step number and parameter name do not match.
        workflow.setParameter(stepNumberEdgeRDGE, "contrast", contrast);
        workflow.setParameter(stepNumberEdgeRDGE, "fdr", fdr);
        // todo: the Vancis production server seems to have a problem converting pdf files to png. Is GraphicsMagick installed?
        // Converting PDF figures to PNG
        // /opt/tmp/331/command.sh: line 35: gm: command not found
        // mv: cannot stat ‘/opt/tmp/331/outputs/dataset_380.dat.png’: No such file or directory
        // /opt/tmp/331/command.sh: line 35: gm: command not found
        // mv: cannot stat ‘/opt/tmp/
//        workflow.setParameter(stepNumberEdgeRDGE, "output_format_images", "png");
        workflow.setParameter(stepNumberEdgeRDGE, "output_format_images", "pdf");
        final String selectedOutputs = "[\"make_output_MDSplot\"," +
                                       " \"make_output_PValue_distribution_plot\"," +
                                       " \"make_output_heatmap_plot\"]";
        workflow.setParameter(stepNumberEdgeRDGE, "outputs", selectedOutputs);

        boolean result = false;
        try {
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
     * @return whether the output seems to be ok.
     * @throws IOException if reading an output file fails.
     */
    private boolean checkWorkflowOutput(final Workflow workflow) throws IOException {
        final int expectedOutputCount = 7;
        return workflow.getOutputMap().size() == expectedOutputCount;
    }
}
