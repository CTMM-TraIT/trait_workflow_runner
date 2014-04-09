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
import nl.vumc.biomedbridges.v2.examples.RemoveTopAndLeftExample;
import nl.vumc.biomedbridges.v2.galaxy.configuration.GalaxyConfiguration;

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
     * The default name of the history to run the workflow in.
     */
    private static final String DEFAULT_HISTORY_NAME = "Workflow Runner History";

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
    private static final int WAIT_AFTER_UPLOAD_MILLISECONDS = 2000;

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
     * The Galaxy server URL.
     */
    private static String galaxyInstanceUrl = GalaxyConfiguration.getGalaxyInstanceUrl();

    /**
     * The Galaxy API key.
     */
    private static String apiKey = GalaxyConfiguration.getGalaxyApiKey();

    /**
     * The name of the history to run the workflow in.
     */
    private static String historyName = DEFAULT_HISTORY_NAME;

    /**
     * The Galaxy server instance that will run the workflows.
     */
    private GalaxyInstance galaxyInstance;

    /**
     * The workflows client to interact with the workflows.
     */
    private WorkflowsClient workflowsClient;

    /**
     * The histories client for accessing Galaxy histories.
     */
    private HistoriesClient historiesClient;

    /**
     * The outputs of the executed workflow.
     */
    private WorkflowOutputs workflowOutputs;

    @Override
    public void configure(final String configurationData) {
        final String instancePrefix = GalaxyConfiguration.GALAXY_INSTANCE_PROPERTY_KEY + "=";
        final String apiKeyPrefix = GalaxyConfiguration.API_KEY_PROPERTY_KEY + "=";
        final String historyNamePrefix = GalaxyConfiguration.HISTORY_NAME_PROPERTY_KEY + "=";
        boolean instanceFound = false;
        boolean apiKeyFound = false;
        String message = null;
        if (configurationData.contains("|") && configurationData.contains(instancePrefix)
            && configurationData.contains(apiKeyPrefix)) {
            for (final String propertyDefinition : configurationData.split("\\|"))
                if (propertyDefinition.startsWith(instancePrefix)) {
                    galaxyInstanceUrl = propertyDefinition.substring(propertyDefinition.indexOf('=') + 1);
                    instanceFound = true;
                    logger.trace("Galaxy instance URL: " + galaxyInstanceUrl);
                } else if (propertyDefinition.startsWith(apiKeyPrefix)) {
                    apiKey = propertyDefinition.substring(propertyDefinition.indexOf('=') + 1);
                    apiKeyFound = true;
                    logger.trace("Galaxy API key: " + apiKey);
                } else if (propertyDefinition.startsWith(historyNamePrefix)) {
                    historyName = propertyDefinition.substring(propertyDefinition.indexOf('=') + 1);
                    logger.trace("Galaxy history name: " + historyName);
                }
            if (!instanceFound || !apiKeyFound)
                message = String.format("Not all expected properties were not found in configuration data %s.",
                                        configurationData);
        } else
            message = String.format("Expected properties were not found in configuration data %s.", configurationData);
        if (message != null)
            logger.error(message + " Please specify: {}" + "[Galaxy server URL]" + "|" + "{}" + "[API key]",
                         instancePrefix, apiKeyPrefix);
    }

    @Override
    public void runWorkflow(final Workflow workflow) throws InterruptedException, IOException {
        logger.info("nl.vumc.biomedbridges.v2.galaxy.GalaxyWorkflowEngine.runWorkflow");
        logger.info("");
        logger.info("Galaxy server: " + galaxyInstanceUrl);
        logger.info("Galaxy API key: " + apiKey);
        logger.info("Galaxy history name: " + historyName);
        logger.info("");

        galaxyInstance = GalaxyInstanceFactory.get(galaxyInstanceUrl, apiKey);
        workflowsClient = galaxyInstance.getWorkflowsClient();
        historiesClient = galaxyInstance.getHistoriesClient();

        logger.info("Ensure the test workflow is available.");
        ((GalaxyWorkflow) workflow).ensureWorkflowIsOnServer(workflowsClient);

        logger.info("Prepare the input files.");

        final String historyId = createNewHistory();
        final WorkflowInputs inputs = prepareWorkflow(historyId, workflow);

        int expectedOutputLength = 0;
        for (final Object input : workflow.getAllInputValues())
            if (input instanceof File)
                expectedOutputLength += ((File) input).length();

        final int scatterPlotOutputLength = 4733;
        if (workflow.getName().equals(WorkflowRunnerVersion2.TEST_WORKFLOW_NAME_2))
            expectedOutputLength = scatterPlotOutputLength;

        final boolean finished = executeWorkflow(historyId, inputs, expectedOutputLength);
        final boolean success = downloadOutputFiles(workflow, historyId);
        logger.trace("Download output files success: {}.", success);
        // todo: return result from runWorkflow: finished && success && checkResults.

        if (finished)
            checkWorkflowResults(historyId, expectedOutputLength);
        else {
            logger.info("Timeout while waiting for workflow output file(s).");
            // Freek: test the output anyway to generate some logging for analysis.
            checkWorkflowResults(historyId, expectedOutputLength);
        }
    }

    /**
     * Create a new history and return its ID.
     *
     * @return the ID of the newly created history.
     */
    private String createNewHistory() {
        return galaxyInstance.getHistoriesClient().create(new History(historyName)).getId();
    }

    /**
     * Prepare for execution of the workflow: upload the input files and create the workflow inputs object.
     *
     * @param historyId the ID of the history to use for workflow input and output.
     * @param workflow  the workflow.
     * @return the workflow inputs object.
     * @throws InterruptedException if any thread has interrupted the current thread while waiting for Galaxy.
     */
    private WorkflowInputs prepareWorkflow(final String historyId, final Workflow workflow)
            throws InterruptedException {
        logger.info("- Upload the input files.");
        for (final Object inputObject : workflow.getAllInputValues())
            if (inputObject instanceof File) {
                final File inputFile = (File) inputObject;
                final int uploadStatus = uploadInputFile(workflow, historyId, inputFile).getStatus();
                if (uploadStatus != HttpStatus.SC_OK)
                    logger.error("Uploading file {} failed with status {}.", inputFile.getAbsolutePath(), uploadStatus);
            }
        logger.info("- Waiting for upload to history to finish.");
        waitForHistoryUpload(historyId);
        logger.info("- Create the workflow inputs object.");
        final WorkflowInputs inputs = new WorkflowInputs();
        inputs.setDestination(new WorkflowInputs.ExistingHistory(historyId));
        final String galaxyWorkflowId = getGalaxyWorkflowId(workflow.getName());
        logger.trace("galaxyWorkflowId: " + galaxyWorkflowId);
        inputs.setWorkflowId(galaxyWorkflowId);
        final WorkflowDetails workflowDetails = workflowsClient.showWorkflow(galaxyWorkflowId);
        // todo: make input labels uniform; for now, we map generic labels to Galaxy labels.
        final String input1Key = "input1";
        final Map<String, String> genericToGalaxyLabelMap;
        if (workflow.getName().equals(WorkflowRunnerVersion2.TEST_WORKFLOW_NAME_1))
            genericToGalaxyLabelMap = ImmutableMap.of(input1Key, "WorkflowInput1", "input2", "WorkflowInput2");
        else
            genericToGalaxyLabelMap = ImmutableMap.of(input1Key, input1Key);
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
     * @param workflow  the workflow.
     * @param historyId the ID of the history to use for workflow input and output.
     * @param inputFile the input file to upload.
     * @return the client response from the Jersey library.
     */
    private ClientResponse uploadInputFile(final Workflow workflow, final String historyId, final File inputFile) {
        final ToolsClient.FileUploadRequest fileUploadRequest = new ToolsClient.FileUploadRequest(historyId, inputFile);
        // todo: do this based on what the Galaxy workflow needs.
        if (workflow.getName().equals(RemoveTopAndLeftExample.WORKFLOW_NAME))
            fileUploadRequest.setFileType("txt");
        else
            fileUploadRequest.setFileType("tabular");
        return galaxyInstance.getToolsClient().uploadRequest(fileUploadRequest);
    }

    /**
     * Wait for the input files upload to finish.
     *
     * @param historyId the ID of the history with the input files.
     * @throws InterruptedException if any thread has interrupted the current thread while waiting for Galaxy.
     */
    private void waitForHistoryUpload(final String historyId) throws InterruptedException {
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
        if (details != null)
            logger.debug("details.getStateIds(): " + details.getStateIds());
        final String state = details != null ? details.getState() : "timeout";
        if (!"ok".equals(state)) {
            logger.error("History no longer running, but not in 'ok' state. State is: '{}'.", state);
            if (details != null)
                logger.error("historiesClient.showHistory(historyId).getStateIds(): " + details.getStateIds());
        }
        Thread.sleep(WAIT_AFTER_UPLOAD_MILLISECONDS);
    }

    // todo: do we need to check getStateIds in waitForHistoryUpload (like below) or does isReady cover everything?

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
     * Get the ID of the Galaxy workflow.
     *
     * @param workflowName the name of the workflow.
     * @return the ID of the Galaxy workflow or null otherwise.
     */
    private String getGalaxyWorkflowId(final String workflowName) {
        com.github.jmchilton.blend4j.galaxy.beans.Workflow matchingWorkflow = null;
        //logger.trace("workflow.getName(): " + workflow.getName());
        for (final com.github.jmchilton.blend4j.galaxy.beans.Workflow workflow : workflowsClient.getWorkflows())
            if (workflow.getName().startsWith(workflowName))
                matchingWorkflow = workflow;
        return (matchingWorkflow != null) ? matchingWorkflow.getId() : null;
    }

    /**
     * Execute the workflow that was prepared with the workflows client.
     *
     * @param historyId            the ID of the history to use for workflow input and output.
     * @param workflowInputs       the blend4j workflow inputs.
     * @param expectedOutputLength the expected output file length.
     * @return whether the workflow finished successfully.
     * @throws InterruptedException if any thread has interrupted the current thread while waiting for Galaxy.
     */
    private boolean executeWorkflow(final String historyId, final WorkflowInputs workflowInputs,
                                    final int expectedOutputLength)
            throws InterruptedException {
        workflowOutputs = workflowsClient.runWorkflow(workflowInputs);
        logger.info("Running the workflow (history ID: {}).", workflowOutputs.getHistoryId());
        boolean finished = false;
        int waitCount = 0;
        while (!finished && waitCount < WORKFLOW_WAIT_MAX_WAIT_BLOCKS) {
            logger.info("- Now waiting for {} seconds...", WORKFLOW_WAIT_SECONDS);
            Thread.sleep(WORKFLOW_WAIT_SECONDS * MILLISECONDS_PER_SECOND);
            final long outputLength = getOutputLength(historyId);
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
     * @param historyId the ID of the history to use for workflow input and output.
     * @return the length of the output file (or -1 when the number of workflow output is not equal to one, or -2 when
     * writing the workflow output file fails).
     */
    private long getOutputLength(final String historyId) {
        long outputLength = -1;
        logger.trace("workflowOutputs.getOutputIds().size(): " + workflowOutputs.getOutputIds().size());
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
     * Download all output files and add them as results to the workflow object.
     *
     * @param workflow  the workflow.
     * @param historyId the ID of the history to use for workflow input and output.
     */
    private boolean downloadOutputFiles(final Workflow workflow, final String historyId) {
        boolean success = true;
        try {
            for (final String outputId : workflowOutputs.getOutputIds()) {
                // todo: use configurable output directory.
                final File outputFile = File.createTempFile("workflow-runner-" + historyId + "-" + outputId, ".txt");
                // todo: is it necessary to fill in the data type (last parameter)?
                success &= HistoryUtils.downloadDataset(galaxyInstance, historiesClient, historyId, outputId,
                                                        outputFile.getAbsolutePath(), false, null);
                // todo: use the Galaxy label of the output instead of the outputId (the ID makes no sense).
                workflow.addOutput(outputId, outputFile);
            }
        } catch (final Exception e) {
            logger.error("Error downloading a workflow output file.", e);
            success = false;
        }
        return success;
    }

    /**
     * Check the results of the workflow.
     *
     * @param historyId            the ID of the history to use for workflow input and output.
     * @param expectedOutputLength the expected output file length.
     * @throws IOException if reading the workflow results fails.
     */
    private void checkWorkflowResults(final String historyId, final int expectedOutputLength) throws IOException {
        logger.info("Check outputs.");
        for (final String outputId : workflowOutputs.getOutputIds())
            logger.info("- Workflow output ID: " + outputId + ".");
        final File concatenationFile = new File(OUTPUT_FILE_PATH);
        final int outputCount = workflowOutputs.getOutputIds().size();
        if (outputCount != 1)
            logger.warn("Unexpected number of workflow outputs: {} (instead of 1).", outputCount);
        //historiesClient.downloadDataset(historyId, workflowOutputs.getOutputIds().get(0),
        //                                concatenationFilePath, false, null);
        // Freek: the last workflow output file is most likely to be the end result?
        HistoryUtils.downloadDataset(galaxyInstance, historiesClient, historyId,
                                     workflowOutputs.getOutputIds().get(outputCount - 1), OUTPUT_FILE_PATH, false, null);
        if (concatenationFile.exists())
            logger.info("- Concatenated file exists.");
        else
            logger.info("- Concatenated file does not exist!");
        // todo: this has to change; maybe only warn when the file length is zero?
        if (concatenationFile.length() != expectedOutputLength)
            logger.warn("Output file length {} not equal to expected length {}.", concatenationFile.length(),
                        expectedOutputLength);
    }
}
