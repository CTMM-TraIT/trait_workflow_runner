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
        try {
            initializeExample(logger, "HistogramExample.runExample");

            //final String workflowType = WorkflowEngineFactory.DEMONSTRATION_TYPE;
            final String workflowType = WorkflowEngineFactory.GALAXY_TYPE;
            final WorkflowEngine workflowEngine = WorkflowEngineFactory.getWorkflowEngine(workflowType);
            final Workflow workflow = workflowEngine.getWorkflow(Constants.TEST_WORKFLOW_HISTOGRAM);

            workflow.addInput("input", FileUtils.createInputFile("8\t21", "9\t34", "10\t55", "11\t89", "12\t144"));
            workflowEngine.runWorkflow(workflow);
            checkWorkflowOutput(workflow);

            finishExample(logger);
        } catch (final InterruptedException | IOException e) {
            logger.error("Exception while running workflow {}.", Constants.TEST_WORKFLOW_HISTOGRAM, e);
        }
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
            final String pdfHeader = "%PDF-1.4";
            final int lineCountLowLimit = 50;
            final int lineCountHighLimit = 70;
            if (pdfHeader.equals(lines.get(0)))
                if (lineCountLowLimit < lines.size() && lines.size() < lineCountHighLimit)
                    logger.info("- Histogram pdf output file appears to be ok!!!");
                else
                    logger.error("- Histogram pdf output file does not contain the amount of lines we expected!");
            else
                logger.error("- Histogram pdf output file does not start the expected pdf header {} !", pdfHeader);
            if (!outputFile.delete())
                logger.error("Deleting output file {} failed (after checking contents).", outputFile.getAbsolutePath());
        } else
            logger.error("There is no single output parameter of type File.");
    }
}
