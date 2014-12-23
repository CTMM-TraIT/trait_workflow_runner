/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.examples;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.util.List;

import nl.vumc.biomedbridges.core.Constants;
import nl.vumc.biomedbridges.core.Workflow;
import nl.vumc.biomedbridges.core.WorkflowEngineFactory;
import nl.vumc.biomedbridges.core.WorkflowFactory;

import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains shared functionality for the workflow running examples.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class BaseExample {
    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(LineCountExample.class);

    /**
     * The workflow engine factory to use.
     */
    protected final WorkflowEngineFactory workflowEngineFactory;

    /**
     * The workflow factory to use.
     */
    protected final WorkflowFactory workflowFactory;

    /**
     * The start time of this example (in milliseconds).
     */
    private long startTime;

    /**
     * The runtime duration of this workflow (in seconds).
     */
    private double durationSeconds;

    /**
     * Construct a base example object.
     *
     * @param workflowFactory the workflow factory to use.
     */
    public BaseExample(final WorkflowFactory workflowFactory) {
        this.workflowEngineFactory = null;
        this.workflowFactory = workflowFactory;
    }

    /**
     * Construct a base example object.
     *
     * @param workflowEngineFactory the workflow engine factory to use.
     * @param workflowFactory       the workflow factory to use.
     */
    public BaseExample(final WorkflowEngineFactory workflowEngineFactory, final WorkflowFactory workflowFactory) {
        this.workflowEngineFactory = workflowEngineFactory;
        this.workflowFactory = workflowFactory;
    }

    /**
     * Construct a base example object.
     *
     * @param workflowEngineFactory the workflow engine factory to use.
     */
    // todo: remove this old constructor later.
    //@Deprecated
    public BaseExample(final WorkflowEngineFactory workflowEngineFactory) {
        this.workflowEngineFactory = workflowEngineFactory;
        this.workflowFactory = null;
    }

    /**
     * Initialize running an example by configuring the logging and storing the start time.
     *
     * @param logger the logger to use.
     * @param name   the name of the example.
     */
    public void initializeExample(final Logger logger, final String name) {
        DOMConfigurator.configure(BaseExample.class.getClassLoader().getResource("log4j.xml"));
        logger.info("========================================");
        logger.info(name + " has started.");

        startTime = System.currentTimeMillis();
    }

    /**
     * Check the single output after running the workflow.
     *
     * @param workflow      the workflow that has been executed.
     * @param outputName    the name of the single output to check.
     * @param expectedLines the lines that are expected in the output file.
     * @return whether the workflow output is correct.
     * @throws IOException if reading the output file fails.
     */
    public boolean checkWorkflowSingleOutput(final Workflow workflow, final String outputName,
                                             final List<String> expectedLines) throws IOException {
        boolean result = false;
        final Object output = workflow.getOutput(outputName);
        if (output instanceof File) {
            final File outputFile = (File) output;
            final List<String> actualLines = Files.readLines(outputFile, Charsets.UTF_8);
            final String lineSeparator = " | ";
            if (expectedLines.equals(actualLines)) {
                result = true;
                logger.info("- The output file contains the lines we expected!!!");
                logger.info("  actual: " + Joiner.on(lineSeparator).join(actualLines));
            } else {
                logger.error("- The output file does not contain the lines we expected!");
                logger.error("  expected: " + Joiner.on(lineSeparator).join(expectedLines));
                logger.error("  actual:   " + Joiner.on(lineSeparator).join(actualLines));
            }
            final boolean deleteResult = outputFile.delete();
            result &= deleteResult;
            if (!deleteResult)
                logger.error("Deleting output file {} failed (after checking contents).", outputFile.getAbsolutePath());
        } else
            logger.error("There is no output file named {}.", outputName);
        return result;
    }

    /**
     * Finish running an example by logging the duration.
     *
     * @param logger the logger to use.
     */
    public void finishExample(final Logger logger) {
        durationSeconds = (System.currentTimeMillis() - startTime) / (float) Constants.MILLISECONDS_PER_SECOND;
        logger.info("");
        logger.info(String.format("Running the workflow took %1.2f seconds.", durationSeconds));
    }

    /**
     * Get the runtime duration of this workflow (in seconds).
     *
     * @return the runtime duration of this workflow (in seconds).
     */
    public double getDurationSeconds() {
        return durationSeconds;
    }
}
