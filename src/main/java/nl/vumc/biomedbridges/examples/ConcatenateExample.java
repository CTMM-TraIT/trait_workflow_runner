/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.examples;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.io.Files;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import nl.vumc.biomedbridges.core.Constants;
import nl.vumc.biomedbridges.core.DefaultGuiceModule;
import nl.vumc.biomedbridges.core.FileUtils;
import nl.vumc.biomedbridges.core.Workflow;
import nl.vumc.biomedbridges.core.WorkflowEngineFactory;
import nl.vumc.biomedbridges.core.WorkflowFactory;
import nl.vumc.biomedbridges.core.WorkflowType;
import nl.vumc.biomedbridges.galaxy.configuration.GalaxyConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains a simple example of the workflow running functionality: the concatenate workflow combines two
 * input files into one output file.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class ConcatenateExample extends BaseExample {
    /**
     * The name of the output dataset.
     */
    public static final String OUTPUT_NAME = "Concatenate datasets on data 2 and data 1";

    /**
     * The name of the Galaxy history.
     */
    protected static final String HISTORY_NAME = "Concatenate History";

    /**
     * Line for test file 1.
     */
    protected static final String LINE_TEST_FILE_1 = "Hello workflow engine!!!";

    /**
     * Line for test file 2.
     */
    protected static final String LINE_TEST_FILE_2 = "Do you wanna play?";

    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(ConcatenateExample.class);

    /**
     * Construct the concatenate example.
     *
     * @param workflowEngineFactory the workflow engine factory to use.
     * @param workflowFactory       the workflow factory to use.
     */
    @Inject
    public ConcatenateExample(final WorkflowEngineFactory workflowEngineFactory, final WorkflowFactory workflowFactory) {
        super(workflowEngineFactory, workflowFactory);
    }

    /**
     * Main method.
     *
     * @param arguments unused command-line arguments.
     */
    // CHECKSTYLE_OFF: UncommentedMain
    public static void main(final String[] arguments) {
        // Create a Guice injector and use it to build the ConcatenateExample object.
        final Injector injector = Guice.createInjector(new DefaultGuiceModule());
        final ConcatenateExample concatenateExample = injector.getInstance(ConcatenateExample.class);

        concatenateExample.runExample();
        //System.exit(0);
    }
    // CHECKSTYLE_ON: UncommentedMain

    /**
     * Run this example workflow: combine two input files into one output file.
     *
     * @return whether the workflow ran successfully.
     */
    public boolean runExample() {
        initializeExample(logger, "ConcatenateExample.runExample");

        final GalaxyConfiguration galaxyConfiguration = new GalaxyConfiguration().setDebug(true);
        galaxyConfiguration.buildConfiguration(Constants.VANCIS_GALAXY_URL, null, HISTORY_NAME);

        final Workflow workflow = workflowFactory.getWorkflow(WorkflowType.GALAXY, galaxyConfiguration,
                                                              Constants.CONCATENATE_WORKFLOW);

        workflow.addInput("WorkflowInput1", FileUtils.createTemporaryFile(LINE_TEST_FILE_1));
        workflow.addInput("WorkflowInput2", FileUtils.createTemporaryFile(LINE_TEST_FILE_2));

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
     * @return whether the workflow output is correct.
     * @throws IOException if reading an output file fails.
     */
    private static boolean checkWorkflowOutput(final Workflow workflow) throws IOException {
        boolean result = false;
        final Object output = workflow.getOutput(OUTPUT_NAME);
        if (output instanceof File) {
            final File outputFile = (File) output;
            final List<String> lines = Files.readLines(outputFile, Charsets.UTF_8);
            final String lineSeparator = " | ";
            if (Arrays.asList(LINE_TEST_FILE_1, LINE_TEST_FILE_2).equals(lines)) {
                result = true;
                logger.info("- Concatenated file contains the lines we expected!!!");
                logger.info("  actual: " + Joiner.on(lineSeparator).join(lines));
            } else {
                logger.error("- Concatenated file does not contain the lines we expected!");
                logger.error("  expected: " + LINE_TEST_FILE_1 + lineSeparator + LINE_TEST_FILE_2);
                logger.error("  actual:   " + Joiner.on(lineSeparator).join(lines));
            }
            final boolean deleteResult = outputFile.delete();
            result &= deleteResult;
            if (!deleteResult)
                logger.error("Deleting output file {} failed (after checking contents).", outputFile.getAbsolutePath());
        } else
            logger.error("There is no single output parameter of type File.");
        return result;
    }
}
