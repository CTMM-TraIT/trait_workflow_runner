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
import java.util.Map;

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
 * This class contains a simple example of the workflow running functionality: the histogram workflow creates a pdf file
 * with a histogram from an input file.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class HistogramExample extends BaseExample {
    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(HistogramExample.class);

    /**
     * The name of the Galaxy history.
     */
    private static final String HISTORY_NAME = "Histogram History";

    /**
     * Construct the histogram example.
     *
     * @param workflowEngineFactory the workflow engine factory to use.
     */
    @Inject
    protected HistogramExample(final WorkflowEngineFactory workflowEngineFactory) {
        super(workflowEngineFactory);
    }

    /**
     * Main method.
     *
     * @param arguments unused command-line arguments.
     */
    // CHECKSTYLE_OFF: UncommentedMain
    public static void main(final String[] arguments) {
        // Create a Guice injector and use it to build the HistogramExample object.
        final Injector injector = Guice.createInjector(new DefaultGuiceModule());
        final HistogramExample histogramExample = injector.getInstance(HistogramExample.class);

        histogramExample.runExample();
    }
    // CHECKSTYLE_ON: UncommentedMain

    /**
     * Run this example workflow: combine two input files into one output file.
     *
     * @return whether the workflow ran successfully.
     */
    public boolean runExample() {
        initializeExample(logger, "HistogramExample.runExample");

        final String workflowType = WorkflowEngineFactory.GALAXY_TYPE;
        final GalaxyConfiguration galaxyConfiguration = new GalaxyConfiguration();
        galaxyConfiguration.buildConfiguration(GALAXY_INSTANCE_URL, galaxyConfiguration.getGalaxyApiKey(), HISTORY_NAME);
        final WorkflowEngine workflowEngine = workflowEngineFactory.getWorkflowEngine(workflowType, galaxyConfiguration);
        final Workflow workflow = workflowEngine.getWorkflow(Constants.WORKFLOW_HISTOGRAM);

        workflow.addInput("input", FileUtils.createTemporaryFile("8\t21", "9\t34", "10\t55", "11\t89", "12\t144"));
        final int stepId = 2;
        final int barCount = 6;
        workflow.setParameter(stepId, "title", "A histogram example");
        workflow.setParameter(stepId, "numerical_column", 1);
        workflow.setParameter(stepId, "breaks", barCount);
        workflow.setParameter(stepId, "xlab", "Number");
        workflow.setParameter(stepId, "density", true);
        workflow.setParameter(stepId, "frequency", false);

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
     * @return whether the workflow output is correct.
     * @throws IOException if reading an output file fails.
     */
    private boolean checkWorkflowOutput(final Workflow workflow) throws IOException {
        boolean result = false;
        final Map<String, Object> outputMap = workflow.getOutputMap();
        final Object output = outputMap.isEmpty() ? null : outputMap.values().iterator().next();
        if (output instanceof File) {
            final File outputFile = (File) output;
            final List<String> lines = Files.readLines(outputFile, Charsets.UTF_8);
            final boolean pdfCorrect = checkPdfContents(lines);
            if (outputFile.delete())
                result = pdfCorrect;
            else
                logger.error("Deleting output file {} failed (after checking contents).", outputFile.getAbsolutePath());
        } else
            logger.error("There is no single output parameter of type File.");
        return result;
    }

    /**
     * Check the contents of the pdf file with the histogram.
     *
     * @param lines the lines from the pdf file.
     * @return whether the contents of the pdf file appear to be correct.
     */
    private boolean checkPdfContents(final List<String> lines) {
        boolean result = false;
        final int lineCountLowLimit = 400;
        final int lineCountHighLimit = 550;
        final String pdfHeader = "%PDF-1.4";
        if (lineCountLowLimit <= lines.size() && lines.size() <= lineCountHighLimit) {
            if (pdfHeader.equals(lines.get(0))) {
                result = true;
                logger.info("- Histogram pdf file appears to be ok!!!");
            } else
                logger.error("- Histogram pdf file does not start with the expected pdf header {} !", pdfHeader);
        } else
            logger.error("- Histogram pdf file does not contain the number of lines we expected ({} actual lines, "
                         + "which is not in expected range [{}..{}])!", lines.size(), lineCountLowLimit,
                         lineCountHighLimit);
        return result;
    }
}
