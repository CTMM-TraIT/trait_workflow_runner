/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.examples;

import com.google.inject.Inject;

import nl.vumc.biomedbridges.core.WorkflowEngineFactory;

/**
 * todo [high priority]: this example is not working yet: complete this example and add unit tests.
 *
 * This class contains a simple example of the workflow running functionality: the "remove top and left" workflow
 * removes a number of lines from the input file and removes a number of characters from the start of the remaining
 * lines.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class RemoveTopAndLeftExample extends BaseExample {
//    /**
//     * The logger for this class.
//     */
//    private static final Logger logger = LoggerFactory.getLogger(RemoveTopAndLeftExample.class);
//
//    /**
//     * The name of the Galaxy history.
//     */
//    private static final String HISTORY_NAME = "Remove Top And Left History";
//
//    /**
//     * The name of the input dataset.
//     */
//    private static final String INPUT_NAME = "Input Dataset";
//
    /**
     * Construct the remove top and left example.
     *
     * @param workflowEngineFactory the workflow engine factory to use.
     */
    @Inject
    protected RemoveTopAndLeftExample(final WorkflowEngineFactory workflowEngineFactory) {
        super(workflowEngineFactory);
    }
//
//    /**
//     * Main method.
//     *
//     * @param arguments unused command-line arguments.
//     */
//    // CHECKSTYLE_OFF: UncommentedMain
//    public static void main(final String[] arguments) {
//        // Create a Guice injector and use it to build the RemoveTopAndLeftExample object.
//        final Injector injector = Guice.createInjector(new DefaultGuiceModule());
//        final RemoveTopAndLeftExample removeTopAndLeftExample = injector.getInstance(RemoveTopAndLeftExample.class);
//
//        removeTopAndLeftExample.runExample();
//    }
//    // CHECKSTYLE_ON: UncommentedMain
//
//    /**
//     * Run this example workflow: remove a number of lines from the input file and remove a number of characters from
//     * the start of the remaining lines.
//     */
//    public void runExample() {
//        try {
//            initializeExample(logger, "RemoveTopAndLeftExample.main");
//
//            final String workflowType = WorkflowEngineFactory.GALAXY_TYPE;
//            final GalaxyConfiguration galaxyConfiguration = new GalaxyConfiguration();
//            galaxyConfiguration.buildConfiguration(Constants.GALAXY_INSTANCE_URL, galaxyConfiguration.getGalaxyApiKey(), HISTORY_NAME);
//            final WorkflowEngine workflowEngine = workflowEngineFactory.getWorkflowEngine(workflowType, galaxyConfiguration);
//            final Workflow workflow = workflowEngine.getWorkflow(Constants.WORKFLOW_REMOVE_TOP_AND_LEFT);
//
//            workflow.addInput(INPUT_NAME, FileUtils.createTemporaryFile("First line", "Second line", "Third line"));
//
//            workflowEngine.runWorkflow(workflow);
//
//            checkWorkflowOutput(workflow);
//
//            finishExample(logger);
//        } catch (final InterruptedException | IOException e) {
//            logger.error("Exception while running workflow {}.", Constants.WORKFLOW_REMOVE_TOP_AND_LEFT, e);
//        }
//    }
//
//    /**
//     * Check the output after running the workflow.
//     *
//     * @param workflow the workflow that has been executed.
//     * @throws java.io.IOException if reading an output file fails.
//     */
//    private static void checkWorkflowOutput(final Workflow workflow) throws IOException {
//        final String outputName = "output";
//        final Object output = workflow.getOutput(outputName);
//        if (output instanceof File) {
//            final File outputFile = (File) output;
//            final List<String> lines = Files.readLines(outputFile, Charsets.UTF_8);
//            final String lineSeparator = " | ";
//            logger.info("- Output file contains the following lines:");
//            logger.info("  " + Joiner.on(lineSeparator).join(lines));
//            if (!outputFile.delete())
//                logger.error("Deleting output file {} failed (after checking contents).", outputFile.getAbsolutePath());
//        } else
//            logger.error("There is no output file named \"" + outputName + "\" of type File.");
//    }
}
