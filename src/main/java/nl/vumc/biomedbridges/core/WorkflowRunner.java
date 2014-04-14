/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.biomedbridges.core;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains a simple test of the workflow running functionality.
 *
 * todo: improve error messages, for example when incorrect names for input files are used.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class WorkflowRunner {
    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(WorkflowRunner.class);

    /**
     * The number of milliseconds in a second.
     */
    private static final int MILLISECONDS_PER_SECOND = 1000;

    /**
     * Line for test file 1.
     */
    private static final String LINE_TEST_FILE_1 = "Hello workflow engine!!!";

    /**
     * Line for test file 2.
     */
    private static final String LINE_TEST_FILE_2 = "Do you wanna play?";

    /**
     * Hidden constructor. The main method below will create a workflow runner.
     */
    private WorkflowRunner() {
    }

    /**
     * Main method.
     *
     * @param arguments unused command-line arguments.
     */
    // CHECKSTYLE_OFF: UncommentedMain
    public static void main(final String[] arguments) {
        try {
            DOMConfigurator.configure(WorkflowRunner.class.getClassLoader().getResource("log4j.xml"));
            logger.info("========================================");
            logger.info("WorkflowRunner.main has started.");

            final long startTime = System.currentTimeMillis();
            //final String workflowType = WorkflowEngineFactory.DEMONSTRATION_TYPE;
            final String workflowType = WorkflowEngineFactory.GALAXY_TYPE;
            final WorkflowEngine workflowEngine = WorkflowEngineFactory.getWorkflowEngine(workflowType);
            final Workflow workflow = workflowEngine.getWorkflow(Constants.TEST_WORKFLOW_NAME);
            final String input1Key = "input1";
            if (Constants.TEST_WORKFLOW_NAME.equals(Constants.TEST_WORKFLOW_CONCATENATE)) {
                workflow.addInput(input1Key, FileUtils.createInputFile(LINE_TEST_FILE_1));
                workflow.addInput("input2", FileUtils.createInputFile(LINE_TEST_FILE_2));
            } else {
                final URL scatterplotInputURL = WorkflowRunner.class.getResource("ScatterplotInput.txt");
                workflow.addInput(input1Key, new File(scatterplotInputURL.toURI()));
            }
            workflowEngine.runWorkflow(workflow);
            checkWorkflowOutput(workflow);
            final double durationSeconds = (System.currentTimeMillis() - startTime) / (float) MILLISECONDS_PER_SECOND;
            logger.info("");
            logger.info(String.format("Running the workflow took %1.2f seconds.", durationSeconds));
        } catch (final InterruptedException | IOException | URISyntaxException e) {
            logger.error("Exception while running workflow {}.", Constants.TEST_WORKFLOW_NAME, e);
        }
    }
    // CHECKSTYLE_ON: UncommentedMain

    /**
     * Check the output after running the workflow.
     *
     * @param workflow the workflow that has been executed.
     * @throws IOException if reading an output file fails.
     */
    private static void checkWorkflowOutput(final Workflow workflow) throws IOException {
        // todo: look at all outputs (since GalaxyWorkflowEngine.downloadOutputFiles now downloads all output files).
        final Object output = workflow.getOutput("output");
        if (output instanceof File) {
            final File outputFile = (File) output;
            if (workflow.getName().equals(Constants.TEST_WORKFLOW_SCATTERPLOT)) {
                logger.debug("Create pdf file for workflow {}.", Constants.TEST_WORKFLOW_SCATTERPLOT);
                Files.copy(outputFile, new File("/tmp/HackathonHappiness.pdf"));
            }
            final List<String> lines = Files.readLines(outputFile, Charsets.UTF_8);
            final String lineSeparator = " | ";
            if (Arrays.asList(LINE_TEST_FILE_1, LINE_TEST_FILE_2).equals(lines)) {
                logger.info("- Concatenated file contains the lines we expected!!!");
                logger.info("  actual: " + Joiner.on(lineSeparator).join(lines));
            } else {
                logger.error("- Concatenated file does not contain the lines we expected!");
                logger.error("  expected: " + LINE_TEST_FILE_1 + lineSeparator + LINE_TEST_FILE_2);
                logger.error("  actual:   " + Joiner.on(lineSeparator).join(lines));
            }
            if (!outputFile.delete())
                logger.error("Deleting output file {} failed (after checking contents).", outputFile.getAbsolutePath());
        } else
            logger.error("There is no output parameter named \"output\" of type File.");
    }
}
