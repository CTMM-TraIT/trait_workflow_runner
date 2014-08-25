/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.galaxy;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.ToolsClient;
import com.github.jmchilton.blend4j.galaxy.WorkflowsClient;
import com.github.jmchilton.blend4j.galaxy.beans.Dataset;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryContents;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryDetails;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowDetails;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowOutputs;
import com.sun.jersey.api.client.ClientResponse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.vumc.biomedbridges.core.Constants;
import nl.vumc.biomedbridges.core.FileUtils;
import nl.vumc.biomedbridges.core.Workflow;
import nl.vumc.biomedbridges.core.WorkflowEngine;

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
     * File type tabular.
     */
    public static final String FILE_TYPE_TABULAR = "tabular";

    /**
     * File type text.
     */
    public static final String FILE_TYPE_TEXT = "txt";

    /**
     * Workflow output file path.
     */
    protected static final String OUTPUT_FILE_PATH = Paths.get("tmp", "WorkflowRunner-runWorkflow.txt").toString();

    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(GalaxyWorkflowEngine.class);

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
    private static final int WORKFLOW_WAIT_MAX_WAIT_BLOCKS = 20;

    /**
     * The number of seconds to wait for the workflow to finish (for each wait cycle).
     */
    private static final int WORKFLOW_WAIT_SECONDS = 3;

    /**
     * History state ok.
     */
    private static final String STATE_OK = "ok";

    /**
     * File extension text.
     */
    private static final String EXTENSION_TEXT = FILE_TYPE_TEXT;

    /**
     * The history utils object.
     */
    private HistoryUtils historyUtils;

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
     * The ID of the history that is used for the input and output files.
     */
    private String historyId;

    /**
     * The outputs of the executed workflow.
     */
    private WorkflowOutputs workflowOutputs;

    /**
     * Mappings from output name to output ID (used when output files are not downloaded automatically).
     */
    private Map<String, String> outputNameToIdsMap;

    ///**
    // * The metadata for the workflow engine.
    // */
    //private GalaxyWorkflowEngineMetadata workflowEngineMetadata;

    /**
     * Create a Galaxy workflow engine.
     *
     * @deprecated todo: this constructor will be removed later.
     */
    @Deprecated
    public GalaxyWorkflowEngine() {
    }

    /**
     * Create a Galaxy workflow engine.
     *
     * @param galaxyInstance the Galaxy instance that is used.
     * @param historyId      the history ID.
     * @param historyUtils   the history utils object.
     */
    public GalaxyWorkflowEngine(final GalaxyInstance galaxyInstance, final String historyId,
                                final HistoryUtils historyUtils) {
        this.galaxyInstance = galaxyInstance;
        this.workflowsClient = galaxyInstance != null ? galaxyInstance.getWorkflowsClient() : null;
        this.historiesClient = galaxyInstance != null ? galaxyInstance.getHistoriesClient() : null;
        this.historyId = historyId;
        this.historyUtils = historyUtils;
    }

    // todo: remove the configure methods from the interface and the implementing classes?
    @Override
    public boolean configure() {
        return configure(null);
    }

    @Override
    public boolean configure(final String configurationData) {
        return true;
    }

    @Override
    public Workflow getWorkflow(final String workflowName) {
        return new GalaxyWorkflow(this, workflowName);
    }

    @Override
    @SuppressWarnings("SpellCheckingInspection")
    public boolean runWorkflow(final Workflow workflow) throws InterruptedException, IOException {
        boolean result = false;
        if (galaxyInstance != null) {
            logStartRunWorkflow();

            // todo: check whether the server is available and/or better error message when the server isn't available.
            /*
            <html>
                <head><title>504 Gateway Time-out</title></head>
                <body bgcolor="white">
                    <center><h1>504 Gateway Time-out</h1></center>
                    <hr>
                    <center>nginx/1.2.0</center>
                </body>
            </html>
             */
            ((GalaxyWorkflow) workflow).ensureWorkflowIsOnServer(workflowsClient);

            logger.info("Prepare the input files.");
            uploadInputFiles(workflow);
            final WorkflowInputs inputs = createInputsObject(workflow);

            final boolean workflowFinished = executeWorkflow(inputs);
            final boolean downloadsSuccessful = downloadOutputFiles(workflow);
            logger.trace("Download output files downloadsSuccessful: {}.", downloadsSuccessful);

            final boolean checkResults;
            if (workflowFinished)
                checkResults = checkWorkflowResults();
            else {
                logger.info("Timeout while waiting for workflow output file(s).");
                // Freek: test the output anyway to generate some logging for debugging/analysis.
                checkResults = checkWorkflowResults();
            }
            logger.trace("workflowFinished: " + workflowFinished);
            logger.trace("downloadsSuccessful: " + downloadsSuccessful);
            logger.trace("checkResults: " + checkResults);
            result = workflowFinished && downloadsSuccessful && checkResults;
        } else
            logger.error("Galaxy instance is not initialized properly.");
        return result;
    }

    /**
     * Log Galaxy server details when starting a workflow.
     */
    private void logStartRunWorkflow() {
        logger.info("nl.vumc.biomedbridges.galaxy.GalaxyWorkflowEngine.runWorkflow");
        logger.info("");
        logger.info("Galaxy instance URL: {}.", galaxyInstance.getGalaxyUrl());
        logger.info("Galaxy API key: {}.", galaxyInstance.getApiKey());
        logger.info("Galaxy history ID: {}.", historyId);
        logger.info("");

        logger.info("Ensure the workflow is available.");
    }

    /**
     * Upload the input files and wait for it to finish.
     *
     * @param workflow the workflow.
     * @throws InterruptedException if any thread has interrupted the current thread while waiting for Galaxy.
     */
    private void uploadInputFiles(final Workflow workflow) throws InterruptedException {
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
        if (workflow.getName().equals(Constants.WORKFLOW_REMOVE_TOP_AND_LEFT))
            fileUploadRequest.setFileType(FILE_TYPE_TEXT);
        else
            fileUploadRequest.setFileType(FILE_TYPE_TABULAR);
        return galaxyInstance.getToolsClient().uploadRequest(fileUploadRequest);
    }

    /**
     * Wait for the input files upload to finish.
     *
     * @param historyId the ID of the history with the input files.
     * @throws InterruptedException if any thread has interrupted the current thread while waiting for Galaxy.
     */
    private void waitForHistoryUpload(final String historyId) throws InterruptedException {
        boolean finished = false;
        int waitCount = 0;
        while (!finished && waitCount < UPLOAD_MAX_WAIT_BLOCKS) {
            logger.info("  + Now waiting for {} seconds...", UPLOAD_WAIT_SECONDS);
            Thread.sleep(UPLOAD_WAIT_SECONDS * MILLISECONDS_PER_SECOND);
            finished = isHistoryReady(historyId);
            waitCount++;
        }
        final HistoryDetails historyDetails = historiesClient.showHistory(historyId);
        final String state = historyDetails.getState();
        final Map<String, List<String>> stateIds = historyDetails.getStateIds();
        final String stateIdsMessage = "historyDetails.getStateIds(): " + stateIds;
        if (STATE_OK.equals(state))
            logger.debug(stateIdsMessage);
        else {
            logger.error("History upload no longer running, but not in 'ok' state. State is: '{}'.", state);
            logger.error(stateIdsMessage);
        }
        Thread.sleep(WAIT_AFTER_UPLOAD_MILLISECONDS);
    }

    /**
     * Check whether uploading/processing of all files in a history is ready.
     *
     * @param historyId the ID of the history with the input files.
     * @return whether uploading/processing of all files in a history is ready.
     */
    private boolean isHistoryReady(final String historyId) {
        final HistoryDetails historyDetails = historiesClient.showHistory(historyId);
        // If the input/output file count is known, it could be checked too:
        //                       historyDetails.getStateIds().get(STATE_OK).size() == [n]
        final boolean finished = historyDetails.getStateIds().get("running").size() == 0
                                 && historyDetails.getStateIds().get("queued").size() == 0;
        logger.debug("finished: " + finished);
        logger.debug("History state IDs: {}.", historyDetails.getStateIds());
        return finished;
    }

    /**
     * Create the workflow inputs object with the input files and parameters.
     *
     * @param workflow the workflow.
     * @return the workflow inputs object.
     */
    private WorkflowInputs createInputsObject(final Workflow workflow) {
        logger.info("- Create the workflow inputs object.");
        final WorkflowInputs inputs = new WorkflowInputs();
        inputs.setDestination(new WorkflowInputs.ExistingHistory(historyId));
        final String galaxyWorkflowId = getGalaxyWorkflowId(workflow.getName());
        logger.trace("galaxyWorkflowId: {}.", galaxyWorkflowId);
        inputs.setWorkflowId(galaxyWorkflowId);
        final WorkflowDetails workflowDetails = workflowsClient.showWorkflow(galaxyWorkflowId);
        for (final Map.Entry<String, Object> inputEntry : workflow.getAllInputEntries()) {
            final String fileName = ((File) inputEntry.getValue()).getName();
            final String inputId = historyUtils.getDatasetIdByName(fileName, historiesClient, historyId);
            final WorkflowInput workflowInput = new WorkflowInput(inputId, WorkflowInputs.InputSourceType.HDA);
            logger.trace("Add input file {} for input label {}.", fileName, inputEntry.getKey());
            WorkflowUtils.setInputByLabel(inputEntry.getKey(), workflowDetails, inputs, workflowInput);
        }
        if (workflow.getParameters() != null && workflow.getParameters().size() > 0) {
            final List<String> stepIds = new ArrayList<>(workflowDetails.getSteps().keySet());
            Collections.sort(stepIds);
            for (final Object stepNumber : workflow.getParameters().keySet())
                for (final Map.Entry<String, Object> entry : workflow.getParameters().get(stepNumber).entrySet()) {
                    final String stepId = stepIds.get(Integer.parseInt(stepNumber.toString()) - 1);
                    logger.trace("Set workflow step {} (id: {}) parameter {} to value {}.", stepNumber, stepId,
                                 entry.getKey(), entry.getValue());
                    inputs.setStepParameter(stepId, entry.getKey(), entry.getValue());
                }
        }
        return inputs;
    }

    /**
     * Get the ID of the Galaxy workflow.
     *
     * @param workflowName the name of the workflow.
     * @return the ID of the Galaxy workflow or null otherwise.
     */
    private String getGalaxyWorkflowId(final String workflowName) {
        com.github.jmchilton.blend4j.galaxy.beans.Workflow matchingWorkflow = null;
        for (final com.github.jmchilton.blend4j.galaxy.beans.Workflow workflow : workflowsClient.getWorkflows())
            if (workflow.getName().startsWith(workflowName))
                matchingWorkflow = workflow;
        return (matchingWorkflow != null) ? matchingWorkflow.getId() : null;
    }

    /**
     * Execute the workflow that was prepared with the workflows client.
     *
     * @param workflowInputs the blend4j workflow inputs.
     * @return whether the workflow finished successfully.
     * @throws InterruptedException if any thread has interrupted the current thread while waiting for Galaxy.
     */
    private boolean executeWorkflow(final WorkflowInputs workflowInputs) throws InterruptedException {
        workflowOutputs = workflowsClient.runWorkflow(workflowInputs);
        logger.info("Running the workflow (history ID: {}).", workflowOutputs.getHistoryId());
        boolean finished = false;
        int waitCount = 0;
        while (!finished && waitCount < WORKFLOW_WAIT_MAX_WAIT_BLOCKS) {
            logger.info("- Now waiting for {} seconds...", WORKFLOW_WAIT_SECONDS);
            Thread.sleep(WORKFLOW_WAIT_SECONDS * MILLISECONDS_PER_SECOND);
            finished = isHistoryReady(historyId);
            waitCount++;
        }
        if (finished)
            logger.info("Workflow seems to be finished after roughly {} seconds.", waitCount * WORKFLOW_WAIT_SECONDS);
        else
            logger.warn("Stopped waiting for the workflow to finish after {} seconds.",
                        WORKFLOW_WAIT_MAX_WAIT_BLOCKS * WORKFLOW_WAIT_SECONDS);
        final Map<String, List<String>> stateIds = historiesClient.showHistory(historyId).getStateIds();
        logger.debug("History state IDs after execute: {}.", stateIds);
        logger.debug("There are {} output file(s) ready for download.", stateIds.get(STATE_OK).size());
        return finished;
    }

    /**
     * If the workflow has automatically downloading selected: download all output files and add them as results to the
     * workflow object. Else: fill a map with output name to output ID entries to allow later download.
     *
     * @param workflow  the workflow.
     * @return whether all output files were downloaded successfully.
     */
    private boolean downloadOutputFiles(final Workflow workflow) {
        boolean success = true;
        try {
            if (workflow.getAutomaticDownload())
                for (final String outputId : workflowOutputs.getOutputIds())
                    success &= downloadOutputFile(workflow, outputId);
            else {
                outputNameToIdsMap = new HashMap<>();
                for (final HistoryContents historyContents : historiesClient.showHistoryContents(historyId))
                    outputNameToIdsMap.put(historyContents.getName(), historyContents.getId());
            }
        } catch (final IllegalArgumentException | IOException | SecurityException e) {
            logger.error("Error downloading a workflow output file.", e);
            success = false;
        }
        return success;
    }

    /**
     * Retrieve the output ID for a workflow output file using the output name.
     *
     * @param outputName the output name.
     * @return the output ID.
     */
    public String getOutputIdForOutputName(final String outputName) {
        return outputNameToIdsMap != null ? outputNameToIdsMap.get(outputName) : null;
    }

    /**
     * Download a workflow output file and add it to the output map in the workflow.
     *
     * @param workflow the workflow.
     * @param outputId the ID of the output file.
     * @return whether downloading was successful.
     * @throws IOException if a local file could not be created.
     */
    protected boolean downloadOutputFile(final Workflow workflow, final String outputId) throws IOException {
        final Dataset dataset = historiesClient.showDataset(historyId, outputId);
        final String outputName = dataset.getName() != null ? dataset.getName() : outputId;
        final String baseName = FileUtils.cleanFileName(String.format("workflow-runner-%s-%s-", historyId, outputName));
        // todo: determine the appropriate extension (like for example pdf).
        //if (workflowEngineMetadata == null)
        //    workflowEngineMetadata = new GalaxyWorkflowEngineMetadata();
        //workflowEngineMetadata.getWorkflow(workflow.getName()).getSteps().get(0).getOutputs().get(0).getName();
        // todo: use dataset data type?
        //final String dataType = dataset.getDataType();
        final String suffix;
        final String period = ".";
        suffix = period + EXTENSION_TEXT;
        //if (FILE_TYPE_TABULAR.equals(dataType))
        //    suffix = period + EXTENSION_TEXT;
        //else
        //    suffix = period + EXTENSION_TEXT;
        final File outputFile;
        if (workflow.getDownloadDirectory() != null)
            outputFile = new File(FileUtils.createUniqueFilePath(workflow.getDownloadDirectory(), baseName, suffix));
        else
            outputFile = File.createTempFile(baseName, suffix);
        logger.trace("Downloading output {} to local file {}.", outputName, outputFile.getAbsolutePath());
        // todo: is it necessary to fill in the data type (last parameter)?
        final boolean success = historyUtils.downloadDataset(galaxyInstance, historiesClient, historyId, outputId,
                                                             outputFile.getAbsolutePath(), null);
        workflow.addOutput(outputName, outputFile);
        return success;
    }

    /**
     * Check the results of the workflow.
     *
     * todo: is this still necessary? only if automatic download is on? last output file is downloaded twice?
     *
     * @return whether the workflow results appear to be valid.
     * @throws IOException if reading the workflow results fails.
     */
    private boolean checkWorkflowResults() throws IOException {
        boolean valid = true;
        logger.info("Check outputs.");
        for (final String outputId : workflowOutputs.getOutputIds())
            logger.info("- Workflow output ID: {}.", outputId);
        final int outputCount = workflowOutputs.getOutputIds().size();
        if (outputCount == 0)
            logger.warn("No workflow output found.");
        else if (outputCount > 1)
            logger.warn("More than one workflow outputs found ({}).", outputCount);
        if (outputCount > 0) {
            // Freek: the last workflow output file is most likely to be the end result?
            final String outputDatasetId = workflowOutputs.getOutputIds().get(outputCount - 1);
            historyUtils.downloadDataset(galaxyInstance, historiesClient, historyId, outputDatasetId, OUTPUT_FILE_PATH,
                                         null);
            final File outputFile = new File(OUTPUT_FILE_PATH);
            if (outputFile.exists())
                logger.info("- Output file exists.");
            else {
                valid = false;
                logger.error("- Output file does not exist!");
            }
            if (outputFile.length() == 0)
                logger.warn("- Output file is empty.");
        }
        return valid;
    }
}
