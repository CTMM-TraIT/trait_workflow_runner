/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.biomedbridges.demonstration;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.io.Files;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import nl.vumc.biomedbridges.core.Workflow;
import nl.vumc.biomedbridges.core.WorkflowEngine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a very simple implementation of the WorkflowEngine interface.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 *
 * This class was created during the BioMedBridges annual general meeting on March 12th, 2014. The original
 * demonstration of the Workflow Runner was impossible because several Galaxy engines were down:
 *
 * https://usegalaxy.org/
 * Galaxy could not be reached
 * The filesystem which serves Galaxy datasets is currently unavailable due to a power interruption in the data center
 * at TACC. Galaxy will be offline until this filesystem is available again. It is estimated that it will return to
 * production service on Monday, March 10.
 *
 * http://galaxy.nbic.nl/workflow/import_workflow
 * Internal Server Error
 * Galaxy was unable to successfully complete your request
 * An error occurred.
 * This may be an intermittent problem due to load or other unpredictable factors, reloading the page may address the
 * problem.
 * The error has been logged to our team.
 */
public class DemonstrationWorkflowEngine implements WorkflowEngine {
    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(DemonstrationWorkflowEngine.class);

    @Override
    public void configure(final String configurationData) {
        logger.info("The demonstration workflow engine is not (yet) configurable. "
                    + "The configuration data {} is not used.", configurationData);
    }

    @Override
    public Workflow getWorkflow(final String workflowName) {
        return new DemonstrationWorkflow(workflowName);
    }

    @Override
    public void runWorkflow(final Workflow workflow) {
        try {
            logger.info("DemonstrationWorkflowEngine.runWorkflow");
            if ("TestWorkflowConcatenate".equals(workflow.getName())) {
                logger.info("Running workflow " + workflow.getName() + "...");
                final Object input1 = workflow.getInput("input1");
                final Object input2 = workflow.getInput("input2");
                if (input1 instanceof File && input2 instanceof File) {
                    final String inputString1 = Joiner.on("").join(Files.readLines((File) input1, Charsets.UTF_8));
                    final String inputString2 = Joiner.on("").join(Files.readLines((File) input2, Charsets.UTF_8));
                    logger.info("input 1: " + inputString1);
                    logger.info("input 2: " + inputString2);
                    workflow.addOutput("output", createOutputFile(workflow, Arrays.asList(inputString1, inputString2)));
                    logger.info("output: " + inputString1 + " " + inputString2);
                } else
                    logger.error("Input parameters are not of the expected type (two input files where expected).");
            }
        } catch (final IOException e) {
            logger.error("Exception while running workflow {}.", workflow.getName(), e);
        }
    }

    /**
     * Create an output file with two lines.
     *
     * @param workflow the workflow where this output file is created for.
     * @param lines the lines to write to the output file.
     * @return the test file.
     */
    private static File createOutputFile(final Workflow workflow, final List<String> lines) {
        try {
            final String filenamePrefix = "workflow-runner-" + workflow.getName().toLowerCase() + "-output";
            final File tempFile = File.createTempFile(filenamePrefix, ".txt");
            try (final Writer writer = new OutputStreamWriter(new FileOutputStream(tempFile), StandardCharsets.UTF_8)) {
                for (final String line : lines) {
                    writer.write(line);
                    writer.write('\n');
                }
            }
            return tempFile;
        } catch (final IOException e) {
            logger.error("Exception while creating the output file.", e);
            throw new RuntimeException(e);
        }
    }
}
