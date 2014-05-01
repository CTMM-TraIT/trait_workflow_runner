/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.biomedbridges.examples;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import nl.vumc.biomedbridges.core.Constants;
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
     * Hidden constructor. The main method below will run this example.
     */
    private HistogramExample() {
    }

    /**
     * Main method.
     *
     * @param arguments unused command-line arguments.
     */
    // CHECKSTYLE_OFF: UncommentedMain
    public static void main(final String[] arguments) {
        new HistogramExample().runExample();
    }
    // CHECKSTYLE_ON: UncommentedMain

    /**
     * Run this example workflow: combine two input files into one output file.
     */
    public void runExample() {
        initializeExample(logger, "HistogramExample.runExample");

        final String workflowType = WorkflowEngineFactory.GALAXY_TYPE;
        final String apiKey = GalaxyConfiguration.getGalaxyApiKey();
        final String configuration = GalaxyConfiguration.buildConfiguration(GALAXY_INSTANCE_URL, apiKey, HISTORY_NAME);
        final WorkflowEngine workflowEngine = WorkflowEngineFactory.getWorkflowEngine(workflowType, configuration);
        final Workflow workflow = workflowEngine.getWorkflow(Constants.WORKFLOW_HISTOGRAM);

        workflow.addInput("input", FileUtils.createInputFile("8\t21", "9\t34", "10\t55", "11\t89", "12\t144"));
        workflow.setParameter(2, "title", "A histogram example");
        workflow.setParameter(2, "numerical_column", 1);
        workflow.setParameter(2, "breaks", 6);
        workflow.setParameter(2, "xlab", "Number");
        workflow.setParameter(2, "density", true);
        workflow.setParameter(2, "frequency", false);

        try {
            workflowEngine.runWorkflow(workflow);
            checkWorkflowOutput(workflow);
        } catch (final InterruptedException | IOException e) {
            logger.error("Exception while running workflow {}.", Constants.WORKFLOW_HISTOGRAM, e);
        }

        finishExample(logger);
    }

    /**
     * Check the output after running the workflow.
     *
     * @param workflow the workflow that has been executed.
     * @throws java.io.IOException if reading an output file fails.
     */
    private static void checkWorkflowOutput(final Workflow workflow) throws IOException {
        final Map<String, Object> outputMap = workflow.getOutputMap();
        final Object output = outputMap.isEmpty() ? null : outputMap.values().iterator().next();
        if (output instanceof File) {
            final File outputFile = (File) output;
            final List<String> lines = Files.readLines(outputFile, Charsets.UTF_8);
            checkPdfContents(lines);
            if (!outputFile.delete())
                logger.error("Deleting output file {} failed (after checking contents).", outputFile.getAbsolutePath());
        } else
            logger.error("There is no single output parameter of type File.");
    }

    /**
     * Check the contents of the pdf file with the histogram.
     *
     * @param lines the lines from the pdf file.
     */
    private static void checkPdfContents(final List<String> lines) {
        final int lineCountLowLimit = 400;
        final int lineCountHighLimit = 550;
        final String pdfHeader = "%PDF-1.4";
        if (lineCountLowLimit <= lines.size() && lines.size() <= lineCountHighLimit) {
            if (pdfHeader.equals(lines.get(0)))
                logger.info("- Histogram pdf file appears to be ok!!!");
            else
                logger.error("- Histogram pdf file does not start with the expected pdf header {} !", pdfHeader);
        } else
            logger.error("- Histogram pdf file does not contain the number of lines we expected ({} actual lines, "
                         + "which is not in expected range [{}..{}])!", lines.size(), lineCountLowLimit,
                         lineCountHighLimit);
    }
}
