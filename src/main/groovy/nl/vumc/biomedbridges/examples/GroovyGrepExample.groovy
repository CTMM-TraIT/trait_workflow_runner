/**
 * Copyright 2015 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.examples

import nl.vumc.biomedbridges.core.Constants
import nl.vumc.biomedbridges.core.DefaultWorkflowFactory
import nl.vumc.biomedbridges.core.FileUtils
import nl.vumc.biomedbridges.core.WorkflowFactory
import nl.vumc.biomedbridges.core.WorkflowType
import org.slf4j.LoggerFactory

/**
 * This class contains a simple example of the workflow that searches for a pattern (grep) in the input file.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
class GroovyGrepExample {
    /**
     * The logger for this class.
     */
    static final logger = LoggerFactory.getLogger(GroovyGrepExample.class)

    /**
     * The input file to search in.
     */
    static final INPUT_FILE = FileUtils.createTemporaryFile("8\t21", "9\t34", "10\t55", "11\t89", "12\t144")

    /**
     * The regular expression to search for.
     */
    static final PATTERN = "5[0-9]"

    /**
     * The workflow factory to use.
     */
    final WorkflowFactory workflowFactory

    /**
     * The start time of this example (in milliseconds).
     */
    def startTime

    /**
     * Main method.
     *
     * @param arguments unused command-line arguments.
     */
    static void main(final String[] arguments) {
        final groovyGrepExample = new GroovyGrepExample(new DefaultWorkflowFactory())
        groovyGrepExample.run(Constants.THE_HYVE_GALAXY_URL, Constants.GREP_WORKFLOW, INPUT_FILE, PATTERN)
    }

    /**
     * Construct the Groovy grep example.
     *
     * @param workflowFactory the workflow factory to use.
     */
    public GroovyGrepExample(final WorkflowFactory workflowFactory) {
        this.workflowFactory = workflowFactory
    }

    /**
     * Run this example workflow and return the result.
     *
     * @param galaxyInstanceUrl the URL of the Galaxy instance to use.
     * @param workflowName the name of the workflow to run.
     * @param inputFile the file to search.
     * @param pattern the regular expression to search for.
     * @return whether the workflow ran successfully.
     * @throws IOException          if reading the workflow results fails.
     * @throws InterruptedException if any thread has interrupted the current thread while waiting for the workflow
     *                              engine.
     */
    boolean run(final String galaxyInstanceUrl, final String workflowName, final File inputFile, final String pattern)
            throws IOException, InterruptedException {
        initializeExample()
        final workflow = workflowFactory.getWorkflow(WorkflowType.GALAXY, galaxyInstanceUrl, workflowName)

        workflow.addInput("input", inputFile)
        // todo: step number can vary from server to server; a more robust way to specify parameters would be nice.
        workflow.setParameter(1, "pattern", pattern)

        workflow.run()

        AbstractBaseExample.checkWorkflowSingleOutput(workflow, "matching_lines", internalGrep(inputFile, pattern))

        finishExample()
        workflow.getResult()
    }

    /**
     * Initialize running an example by configuring the logging and storing the start time.
     *
     * @param logger the logger to use.
     */
    void initializeExample() {
        logger.info("========================================")
        logger.info(getClass().getSimpleName() + " has started.")

        startTime = System.currentTimeMillis()
    }

    /**
     * Simple internal implementation of grep to determine the expected output of the workflow.
     *
     * @param inputFile the file to search.
     * @param pattern the regular expression to search for.
     * @return the matching lines.
     * @throws IOException when reading from the file fails.
     */
    protected static List<String> internalGrep(final File inputFile, final String pattern) throws IOException {
        // There must be a way groovier way to do this...
        final matchingWriter = new StringWriter()
        inputFile.filterLine(matchingWriter) { it =~ pattern }
        final matchingLines = new ArrayList<String>()
        matchingWriter.toString().eachLine { matchingLines << it }
        matchingLines
    }

    /**
     * Finish running an example by logging the duration.
     */
    void finishExample() {
        final durationSeconds = (System.currentTimeMillis() - startTime) / (float) Constants.MILLISECONDS_PER_SECOND
        logger.info("")
        logger.info("Running the workflow took ${durationSeconds.round(2)} seconds.")
    }
}
