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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains shared functionality for the workflow running examples.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public abstract class AbstractBaseExample {
    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(AbstractBaseExample.class);

    /**
     * The workflow engine factory to use.
     */
    protected final WorkflowEngineFactory workflowEngineFactory;

    /**
     * The workflow factory to use.
     */
    protected final WorkflowFactory workflowFactory;

    /**
     * Whether the HTTP messages should be logged or not.
     */
    protected boolean httpLogging = true;

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
    public AbstractBaseExample(final WorkflowFactory workflowFactory) {
        this.workflowEngineFactory = null;
        this.workflowFactory = workflowFactory;
    }

    /**
     * Construct a base example object.
     *
     * @param workflowEngineFactory the workflow engine factory to use.
     * @param workflowFactory       the workflow factory to use.
     */
    public AbstractBaseExample(final WorkflowEngineFactory workflowEngineFactory, final WorkflowFactory workflowFactory) {
        this.workflowEngineFactory = workflowEngineFactory;
        this.workflowFactory = workflowFactory;
    }

    /**
     * Construct a base example object.
     *
     * @param workflowEngineFactory the workflow engine factory to use.
     * @param workflowFactory       the workflow factory to use.
     * @param logger                the logger to use for initialization.
     */
    public AbstractBaseExample(final WorkflowEngineFactory workflowEngineFactory, final WorkflowFactory workflowFactory,
                               final Logger logger) {
        this.workflowEngineFactory = workflowEngineFactory;
        this.workflowFactory = workflowFactory;

        if (logger != null)
            initializeExample(logger, null);
    }

    /**
     * Construct a base example object.
     *
     * @param workflowEngineFactory the workflow engine factory to use.
     */
    // todo: remove this old constructor later.
    //@Deprecated
    public AbstractBaseExample(final WorkflowEngineFactory workflowEngineFactory) {
        this.workflowEngineFactory = workflowEngineFactory;
        this.workflowFactory = null;
    }

    /**
     * Select whether the HTTP messages should be logged or not.
     *
     * @param httpLogging whether the HTTP messages should be logged or not.
     */
    public void setHttpLogging(final boolean httpLogging) {
        this.httpLogging = httpLogging;
    }

    /**
     * Initialize running an example by configuring the logging and storing the start time.
     *
     * @param logger the logger to use.
     * @param name   the name of the example.
     */
    public void initializeExample(final Logger logger, final String name) {
        logger.info("========================================");
        logger.info(name != null ? name : getClass().getSimpleName() + " has started.");

        startTime = System.currentTimeMillis();
    }

    /**
     * Run this example workflow and return the result. Should be implemented by sub classes.
     *
     * @param galaxyInstanceUrl the URL of the Galaxy instance to use.
     * @return whether the workflow ran successfully.
     */
    public boolean runExample(final String galaxyInstanceUrl) {
        return false;
    }

    /**
     * Check the single output after running the workflow.
     *
     * @param workflow      the workflow that has been executed.
     * @param outputName    the name of the single output to check.
     * @param expectedLines the lines that are expected in the output file.
     * @throws IOException if reading the output file fails.
     */
    public void checkWorkflowSingleOutput(final Workflow workflow, final String outputName,
                                          final List<String> expectedLines) throws IOException {
        final boolean finalResult = checkWorkflowSingleOutput(workflow, workflow.getResult(), outputName, expectedLines);
        workflow.setResult(finalResult);
    }

    /**
     * Check the single output after running the workflow.
     *
     * @param workflow      the workflow that has been executed.
     * @param runResult     the result from running the workflow.
     * @param outputName    the name of the single output to check.
     * @param expectedLines the lines that are expected in the output file.
     * @return whether the workflow output is correct (and running the workflow returned true).
     * @throws IOException if reading the output file fails.
     */
    public boolean checkWorkflowSingleOutput(final Workflow workflow, final boolean runResult, final String outputName,
                                             final List<String> expectedLines) throws IOException {
        boolean result = false;
        if (!runResult)
            logger.error("Error while running workflow {}.", workflow.getName());
        final Object output = workflow.getOutput(outputName);
        if (output instanceof File) {
            final File outputFile = (File) output;
            final List<String> actualLines = Files.readLines(outputFile, Charsets.UTF_8);
            final String lineSeparator = " | ";
            final String partialMessage = "the line" + (expectedLines.size() > 1 ? "s" : "") + " we expected!";
            if (expectedLines.equals(actualLines)) {
                result = true;
                logger.info("- The output file contains " + partialMessage + "!!");
                logger.info("  actual: " + Joiner.on(lineSeparator).join(actualLines));
            } else {
                logger.error("- The output file does not contain " + partialMessage);
                logger.error("  expected: " + Joiner.on(lineSeparator).join(expectedLines));
                logger.error("  actual:   " + Joiner.on(lineSeparator).join(actualLines));
            }
            final boolean deleteResult = outputFile.delete();
            result &= deleteResult;
            if (!deleteResult)
                logger.error("Deleting output file {} failed (after checking contents).", outputFile.getAbsolutePath());
        } else
            logger.error("There is no output file named {}.", outputName);
        return runResult && result;
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
     * Finish running an example by logging the duration. This method returns the workflow result as well.
     *
     * @param workflow the workflow that ran.
     * @return the result from running the workflow.
     */
    public boolean finishExample(final Workflow workflow) {
        finishExample(logger);
        return workflow.getResult();
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
