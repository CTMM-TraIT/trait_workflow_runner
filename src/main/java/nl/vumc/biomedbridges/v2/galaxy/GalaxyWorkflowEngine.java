/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.biomedbridges.v2.galaxy;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.GalaxyInstanceFactory;
import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.ToolsClient;
import com.github.jmchilton.blend4j.galaxy.WorkflowsClient;
import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryDetails;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowDetails;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowOutputs;
import com.google.common.collect.ImmutableMap;
import com.sun.jersey.api.client.ClientResponse;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import nl.vumc.biomedbridges.v2.core.Workflow;
import nl.vumc.biomedbridges.v2.core.WorkflowEngine;
import nl.vumc.biomedbridges.v2.core.WorkflowRunnerVersion2;
import nl.vumc.biomedbridges.v2.galaxy.configuration.Configuration;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs.WorkflowInput;

/**
 * The workflow engine implementation for Galaxy.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class GalaxyWorkflowEngine implements WorkflowEngine {
    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(GalaxyWorkflowEngine.class);

    /**
     * The Galaxy server URL.
     */
    private static final String GALAXY_INSTANCE_URL = Configuration.getGalaxyInstanceUrl();

    /**
     * The Galaxy API key.
     */
    private static final String GALAXY_API_KEY = Configuration.getGalaxyApiKey();

    /**
     * The name of the history to run the workflow in.
     */
    private static final String HISTORY_NAME = "Workflow Runner History";

    /**
     * The number of milliseconds in a second.
     */
    private static final int MILLISECONDS_PER_SECOND = 1000;

    /**
     * The maximum number of times to wait for the upload to finish.
     */
    private static final int UPLOAD_MAX_WAIT_BLOCKS = 28;

    /**
     * The number of seconds to wait for the upload to finish (for each wait cycle).
     */
    private static final int UPLOAD_WAIT_SECONDS = 6;

    /**
     * The number of milliseconds to wait after the upload has finished.
     */
    private static final int WAIT_AFTER_UPLOAD_MILLISECONDS = 200;

    /**
     * The maximum number of times to wait for the workflow to finish.
     */
    private static final int WORKFLOW_WAIT_MAX_WAIT_BLOCKS = 10;

    /**
     * The number of seconds to wait for the workflow to finish (for each wait cycle).
     */
    private static final int WORKFLOW_WAIT_SECONDS = 3;

    /**
     * Workflow output file path.
     */
    private static final String OUTPUT_FILE_PATH = "WorkflowRunner-runWorkflow.txt";

    /**
     * The outputs of the executed workflow.
     */
    private WorkflowOutputs workflowOutputs;

    @Override
    public void runWorkflow(final Workflow workflow) throws InterruptedException, IOException {
        logger.info("nl.vumc.biomedbridges.v2.galaxy.GalaxyWorkflowEngine.runWorkflow");
        logger.info("");
        logger.info("Galaxy server: " + GALAXY_INSTANCE_URL);
        logger.info("Galaxy API key: " + GALAXY_API_KEY);
        logger.info("");

        final GalaxyInstance galaxyInstance = GalaxyInstanceFactory.get(GALAXY_INSTANCE_URL, GALAXY_API_KEY);
        final WorkflowsClient workflowsClient = galaxyInstance.getWorkflowsClient();

        logger.info("Ensure the test workflow is available.");
        ((GalaxyWorkflow) workflow).ensureWorkflowIsOnServer(workflowsClient);

        logger.info("Prepare the input files.");
        final HistoriesClient historiesClient = galaxyInstance.getHistoriesClient();
//        if (testHistoryStatus("c27fd950e2f21bbd", historiesClient))
//            return;

        final String historyId = getNewHistoryId(galaxyInstance);
        final WorkflowInputs inputs = prepareWorkflow(galaxyInstance, workflowsClient, historyId, historiesClient,
                                                      workflow);

        int expectedOutputLength = 0;
        for (final Object input : workflow.getAllInputValues())
            if (input instanceof File)
                expectedOutputLength += ((File) input).length();
        expectedOutputLength += 2 * (workflow.getAllInputValues().size() - 1);

        if (workflow.getName().equals(WorkflowRunnerVersion2.TEST_WORKFLOW_NAME_2))
            expectedOutputLength = 4733;

        final boolean finished = executeWorkflow(galaxyInstance, workflowsClient, historiesClient, historyId, inputs,
                                                 expectedOutputLength);

        if (finished)
            checkWorkflowResults(workflow, galaxyInstance, historiesClient, historyId, expectedOutputLength);
        else
            logger.info("Timeout while waiting for workflow output file(s).");
    }

    // Freek: quick test for retrieving the status of files in the history.
//    private boolean testHistoryStatus(final String historyId, final HistoriesClient historiesClient) {
//        System.out.println();
//        for (final History history : historiesClient.getHistories())
//            if (history.getName().equals(historyId))
//                System.out.println("History " + historyId + " found.");
//        final HistoryDetails historyDetails = historiesClient.showHistory(historyId);
//        System.out.println("historyDetails: " + historyDetails);
//        System.out.println("historyDetails.getState(): " + historyDetails.getState());
//        System.out.println("historyDetails.getStateIds(): " + historyDetails.getStateIds());
//        System.out.println();
//        return true;
//    }

    /**
     * Create a new history and return its ID.
     *
     * @param galaxyInstance the Galaxy instance to create the history in.
     * @return the ID of the newly created history.
     */
    private String getNewHistoryId(final GalaxyInstance galaxyInstance) {
        return galaxyInstance.getHistoriesClient().create(new History(HISTORY_NAME)).getId();
    }

    /**
     * Prepare for execution of the workflow: upload the input files and create the workflow inputs object.
     *
     * @param galaxyInstance  the Galaxy instance to run the workflow in.
     * @param workflowsClient the workflows client to interact with the workflow.
     * @param historyId       the ID of the history to use for workflow input and output.
     * @param historiesClient the histories client for accessing Galaxy histories.
     * @param workflow        the workflow.
     * @return the workflow inputs object.
     * @throws InterruptedException if any thread has interrupted the current thread while waiting for Galaxy.
     */
    private WorkflowInputs prepareWorkflow(final GalaxyInstance galaxyInstance, final WorkflowsClient workflowsClient,
                                           final String historyId, final HistoriesClient historiesClient,
                                           final Workflow workflow)
            throws InterruptedException {
        logger.info("- Upload the input files.");
        for (final Object inputObject : workflow.getAllInputValues()) {
            if (inputObject instanceof File) {
                final File inputFile = (File) inputObject;
                final int uploadStatus = uploadInputFile(galaxyInstance, historyId, inputFile).getStatus();
                if (uploadStatus != HttpStatus.SC_OK) {
                    logger.error("Uploading file {} failed with status {}.", inputFile.getAbsolutePath(), uploadStatus);
                }
            }
        }
        logger.info("- Waiting for upload to history to finish.");
        waitForHistoryUpload(galaxyInstance.getHistoriesClient(), historyId);
        logger.info("- Create the workflow inputs object.");
        final WorkflowInputs inputs = new WorkflowInputs();
        inputs.setDestination(new WorkflowInputs.ExistingHistory(historyId));
        final String testWorkflowId = getTestWorkflowId(workflowsClient, workflow.getName());
        inputs.setWorkflowId(testWorkflowId);
        final WorkflowDetails workflowDetails = workflowsClient.showWorkflow(testWorkflowId);
        // todo: make input labels uniform; for now, we map generic labels to Galaxy labels.
//        final Map<String, String> genericToGalaxyLabelMap = ImmutableMap.of("input1", "WorkflowInput1",
//                                                                            "input2", "WorkflowInput2");
        final Map<String, String> genericToGalaxyLabelMap = ImmutableMap.of("input1", "input1");
        for (final Map.Entry<String, Object> inputEntry : workflow.getAllInputEntries()) {
            final String fileName = ((File) inputEntry.getValue()).getName();
//        final String inputId = historiesClient.getDatasetIdByName(fileName, historyId);
            final String inputId = HistoryUtils.getDatasetIdByName(fileName, historiesClient, historyId);
//        inputs.setInputByLabel("WorkflowInput1", workflowDetails, new WorkflowInput(input1Id, InputSourceType.HDA));
            final WorkflowInput workflowInput = new WorkflowInput(inputId, WorkflowInputs.InputSourceType.HDA);
            final String label = genericToGalaxyLabelMap.get(inputEntry.getKey());
            WorkflowUtils.setInputByLabel(label, workflowDetails, inputs, workflowInput);
        }
        return inputs;
    }

    /**
     * Upload an input file to a Galaxy server.
     *
     * @param galaxyInstance the Galaxy instance to upload the file to.
     * @param historyId      the ID of the history to use for workflow input and output.
     * @param inputFile      the input file to upload.
     * @return the client response from the Jersey library.
     */
    private ClientResponse uploadInputFile(final GalaxyInstance galaxyInstance, final String historyId,
                                           final File inputFile) {
        final ToolsClient.FileUploadRequest fileUploadRequest = new ToolsClient.FileUploadRequest(historyId, inputFile);
        fileUploadRequest.setFileType("tabular");
        return galaxyInstance.getToolsClient().uploadRequest(fileUploadRequest);
    }

    /**
     * Wait for the input files upload to finish.
     *
     * @param historiesClient the histories client for accessing Galaxy histories.
     * @param historyId       the ID of the history with the input files.
     * @throws InterruptedException if any thread has interrupted the current thread while waiting for Galaxy.
     */
    private void waitForHistoryUpload(final HistoriesClient historiesClient, final String historyId)
            throws InterruptedException {
        HistoryDetails details = null;
        boolean finished = false;
        int waitCount = 0;
        while (!finished && waitCount < UPLOAD_MAX_WAIT_BLOCKS) {
            logger.info("  + Now waiting for {} seconds...", UPLOAD_WAIT_SECONDS);
            Thread.sleep(UPLOAD_WAIT_SECONDS * MILLISECONDS_PER_SECOND);
            details = historiesClient.showHistory(historyId);
            logger.info("  + History is " + (details.isReady() ? "ready." : "not ready yet."));
            finished = details.isReady();
            waitCount++;
        }
        final String state = details != null ? details.getState() : "timeout";
        if (!"ok".equals(state))
        //throw new RuntimeException("History no longer running, but not in 'ok' state. State is: " + state);
        {
            logger.error("History no longer running, but not in 'ok' state. State is: " + state);
        }
        Thread.sleep(WAIT_AFTER_UPLOAD_MILLISECONDS);
    }

    /**
     * Get the ID of the test workflow.
     *
     * @param client       the workflows client used to iterate all workflows.
     * @param workflowName the name of the workflow.
     * @return the ID of the test workflow or null otherwise.
     */
    private String getTestWorkflowId(final WorkflowsClient client, final String workflowName) {
        com.github.jmchilton.blend4j.galaxy.beans.Workflow matchingWorkflow = null;
        for (final com.github.jmchilton.blend4j.galaxy.beans.Workflow workflow : client.getWorkflows()) {
            if (workflow.getName().startsWith(workflowName)) {
                matchingWorkflow = workflow;
            }
        }
        return (matchingWorkflow != null) ? matchingWorkflow.getId() : null;
    }

    /**
     * Execute the workflow that was prepared with the workflows client.
     *
     * @param galaxyInstance       the Galaxy instance to upload the file to.
     * @param workflowsClient      the workflows client to interact with the workflow.
     * @param historiesClient      the histories client for accessing Galaxy histories.
     * @param historyId            the ID of the history to use for workflow input and output.
     * @param workflowInputs       the blend4j workflow inputs.
     * @param expectedOutputLength the expected output file length.
     * @return whether the workflow finished successfully.
     * @throws InterruptedException if any thread has interrupted the current thread while waiting for Galaxy.
     */
    private boolean executeWorkflow(final GalaxyInstance galaxyInstance, final WorkflowsClient workflowsClient,
                                    final HistoriesClient historiesClient, final String historyId,
                                    final WorkflowInputs workflowInputs, final int expectedOutputLength)
            throws InterruptedException {
        workflowOutputs = workflowsClient.runWorkflow(workflowInputs);
        logger.info("Running the workflow (history ID: {}).", workflowOutputs.getHistoryId());
        boolean finished = false;
        int waitCount = 0;
        while (!finished && waitCount < WORKFLOW_WAIT_MAX_WAIT_BLOCKS) {
            logger.info("- Now waiting for {} seconds...", WORKFLOW_WAIT_SECONDS);
            Thread.sleep(WORKFLOW_WAIT_SECONDS * MILLISECONDS_PER_SECOND);
            final long outputLength = getOutputLength(galaxyInstance, workflowOutputs, historiesClient, historyId);
            logger.info("- Output length: {} (expect: {}).", outputLength, expectedOutputLength);
            finished = outputLength == expectedOutputLength;
            // todo: check status output files using the history instead of the code above?!?
            waitCount++;
        }
        return finished;
    }

    /**
     * Determine the length of the output file.
     *
     * @param galaxyInstance  the Galaxy instance to upload the file to.
     * @param workflowOutputs the blend4j workflow outputs.
     * @param historiesClient the histories client for accessing Galaxy histories.
     * @param historyId       the ID of the history to use for workflow input and output.
     * @return the length of the output file (or -1 when the number of workflow output is not equal to one, or -2 when
     * writing the workflow output file fails).
     */
    private long getOutputLength(final GalaxyInstance galaxyInstance, final WorkflowOutputs workflowOutputs,
                                 final HistoriesClient historiesClient, final String historyId) {
        long outputLength = -1;
        if (workflowOutputs.getOutputIds().size() == 1) {
            final File concatenationFile = new File(OUTPUT_FILE_PATH);
            try {
//                historiesClient.downloadDataset(historyId, workflowOutputs.getOutputIds().get(0),
//                                                concatenationFilePath, false, null);
                HistoryUtils.downloadDataset(galaxyInstance, historiesClient, historyId,
                                             workflowOutputs.getOutputIds().get(0), OUTPUT_FILE_PATH, false, null);
                if (concatenationFile.exists())
                    outputLength = concatenationFile.length();
                else
                    outputLength = -2;
            } finally {
                if (!concatenationFile.delete())
                    logger.error("Deleting output file {} failed (after determining length).", OUTPUT_FILE_PATH);
            }
        }
        return outputLength;
    }

    /**
     * Check the results of the workflow.
     *
     * @param workflow             the workflow.
     * @param galaxyInstance       the Galaxy instance to upload the file to.
     * @param historiesClient      the histories client for accessing Galaxy histories.
     * @param historyId            the ID of the history to use for workflow input and output.
     * @param expectedOutputLength the expected output file length.
     * @throws IOException if reading the workflow results fails.
     */
    private void checkWorkflowResults(final Workflow workflow, final GalaxyInstance galaxyInstance,
                                      final HistoriesClient historiesClient, final String historyId,
                                      final int expectedOutputLength) throws IOException {
        logger.info("Check outputs.");
        for (final String outputId : workflowOutputs.getOutputIds())
            logger.info("- Workflow output ID: " + outputId + ".");
        final File concatenationFile = new File(OUTPUT_FILE_PATH);
        final int outputCount = workflowOutputs.getOutputIds().size();
        if (outputCount != 1)
            logger.warn("Unexpected number of workflow outputs: {} (instead of 1).", outputCount);
        //historiesClient.downloadDataset(historyId, workflowOutputs.getOutputIds().get(0),
        //                                concatenationFilePath, false, null);
        HistoryUtils.downloadDataset(galaxyInstance, historiesClient, historyId,
                                     workflowOutputs.getOutputIds().get(0), OUTPUT_FILE_PATH, false, null);
        if (concatenationFile.exists()) {
            logger.info("- Concatenated file exists.");
            // todo: make this generic.
            workflow.addOutput("output", concatenationFile);
        } else
            logger.info("- Concatenated file does not exist!");
        // todo: this has to change; maybe only warn when the file length is zero?
        if (concatenationFile.length() != expectedOutputLength)
            logger.warn("Output file length {} not equal to expected length {}.", concatenationFile.length(),
                        expectedOutputLength);
        // todo: this is now done in WorkflowRunnerVersion2. Make sure this is no longer needed and then remove it.
//        final List<String> lines = Files.readLines(concatenationFile, Charsets.UTF_8);
//        if (Arrays.asList(TEST_FILE_LINE_1, TEST_FILE_LINE_2).equals(lines)) {
//            logger.info("- Concatenated file contains the lines we expected!!!");
//        } else {
//            logger.warn("- Concatenated file does not contain the lines we expected (lines: " + lines + ")!");
//        }
    }
}
