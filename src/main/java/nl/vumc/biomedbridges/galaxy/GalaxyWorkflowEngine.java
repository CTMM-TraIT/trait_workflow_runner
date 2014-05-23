/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.biomedbridges.galaxy;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.GalaxyInstanceFactory;
import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.ToolsClient;
import com.github.jmchilton.blend4j.galaxy.WorkflowsClient;
import com.github.jmchilton.blend4j.galaxy.beans.Dataset;
import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryDetails;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowDetails;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowOutputs;
import com.sun.jersey.api.client.ClientResponse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import nl.vumc.biomedbridges.core.Workflow;
import nl.vumc.biomedbridges.core.WorkflowEngine;
import nl.vumc.biomedbridges.examples.RemoveTopAndLeftExample;
import nl.vumc.biomedbridges.galaxy.configuration.GalaxyConfiguration;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs.WorkflowInput;

/**
 * The workflow engine implementation for Galaxy.
 *
 * todo: make galaxyInstanceUrl, apiKey, and historyName non static to enable using multiple Galaxy servers?
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
    private static final int WORKFLOW_WAIT_MAX_WAIT_BLOCKS = 20;

    /**
     * The number of seconds to wait for the workflow to finish (for each wait cycle).
     */
    private static final int WORKFLOW_WAIT_SECONDS = 3;

    /**
     * Workflow output file path.
     */
    private static final String OUTPUT_FILE_PATH = "WorkflowRunner-runWorkflow.txt";

    /**
     * History state ok.
     */
    private static final String STATE_OK = "ok";

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
    public boolean configure() {
        return configure(null);
    }

    @Override
    public boolean configure(final String configurationData) {
        final String instancePrefix = GalaxyConfiguration.GALAXY_INSTANCE_PROPERTY_KEY
                                      + GalaxyConfiguration.KEY_VALUE_SEPARATOR;
        final String apiKeyPrefix = GalaxyConfiguration.API_KEY_PROPERTY_KEY + GalaxyConfiguration.KEY_VALUE_SEPARATOR;
        final String historyNamePrefix = GalaxyConfiguration.HISTORY_NAME_PROPERTY_KEY
                                         + GalaxyConfiguration.KEY_VALUE_SEPARATOR;
        String message = null;
        if (configurationData != null)
            if (configurationData.contains(GalaxyConfiguration.PROPERTY_SEPARATOR)
                && configurationData.contains(instancePrefix)
                && configurationData.contains(apiKeyPrefix))
                message = processConfigurationProperties(configurationData, instancePrefix, apiKeyPrefix, historyNamePrefix);
            else
                message = String.format("Expected properties were not found in configuration data %s.", configurationData);
        final boolean success = message == null;
        if (success) {
            galaxyInstance = GalaxyInstanceFactory.get(galaxyInstanceUrl, apiKey);
            workflowsClient = galaxyInstance.getWorkflowsClient();
            historiesClient = galaxyInstance.getHistoriesClient();
        } else {
            logger.error(message + " Please specify: {}[Galaxy server URL]{}{}[API key]", instancePrefix,
                         GalaxyConfiguration.PROPERTY_SEPARATOR, apiKeyPrefix);
            galaxyInstance = null;
        }
        return success;
    }

    /**
     * Process all configuration properties.
     *
     * @param configurationData the configuration data.
     * @param instancePrefix    the Galaxy instance property prefix.
     * @param apiKeyPrefix      the api key property prefix.
     * @param historyNamePrefix the history name property prefix.
     * @return the logging message or null if there is nothing to log.
     */
    private String processConfigurationProperties(final String configurationData, final String instancePrefix,
                                                  final String apiKeyPrefix, final String historyNamePrefix) {
        String message = null;
        boolean instanceFound = false;
        boolean apiKeyFound = false;
        for (final String propertyDefinition : configurationData.split("\\|"))
            if (propertyDefinition.startsWith(instancePrefix)) {
                galaxyInstanceUrl = propertyDefinition.substring(propertyDefinition.indexOf('=') + 1);
                instanceFound = true;
                logger.trace("Read property Galaxy instance URL: {}.", galaxyInstanceUrl);
            } else if (propertyDefinition.startsWith(apiKeyPrefix)) {
                apiKey = propertyDefinition.substring(propertyDefinition.indexOf('=') + 1);
                apiKeyFound = true;
                logger.trace("Read property Galaxy API key: {}.", apiKey);
            } else if (propertyDefinition.startsWith(historyNamePrefix)) {
                historyName = propertyDefinition.substring(propertyDefinition.indexOf('=') + 1);
                logger.trace("Read property Galaxy history name: {}.", historyName);
            }
        if (!instanceFound || !apiKeyFound)
            message = String.format("Not all expected properties (Galaxy instance and API key) were found in"
                                    + " configuration data %s.", configurationData);
        return message;
    }

    @Override
    public Workflow getWorkflow(final String workflowName) {
        return new GalaxyWorkflow(workflowName);
    }

    @Override
    public boolean runWorkflow(final Workflow workflow) throws InterruptedException, IOException {
        boolean result = false;
        if (galaxyInstance != null) {
            logStartRunWorkflow();

            ((GalaxyWorkflow) workflow).ensureWorkflowIsOnServer(workflowsClient);

            logger.info("Prepare the input files.");
            final String historyId = createNewHistory();
            uploadInputFiles(historyId, workflow);
            final WorkflowInputs inputs = createInputsObject(historyId, workflow);

            final boolean workflowFinished = executeWorkflow(historyId, inputs);
            final boolean downloadsSuccessful = downloadOutputFiles(workflow, historyId);
            logger.trace("Download output files downloadsSuccessful: {}.", downloadsSuccessful);

            final boolean checkResults;
            if (workflowFinished)
                checkResults = checkWorkflowResults(historyId);
            else {
                logger.info("Timeout while waiting for workflow output file(s).");
                // Freek: test the output anyway to generate some logging for analysis.
                checkResults = checkWorkflowResults(historyId);
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
        logger.info("Galaxy instance URL: {}.", galaxyInstanceUrl);
        logger.info("Galaxy API key: {}.", apiKey);
        logger.info("Galaxy history name: {}.", historyName);
        logger.info("");

        logger.info("Ensure the workflow is available.");
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
     * Upload the input files and wait for it to finish.
     *
     * @param historyId the ID of the history to use for workflow input and output.
     * @param workflow  the workflow.
     * @throws InterruptedException if any thread has interrupted the current thread while waiting for Galaxy.
     */
    private void uploadInputFiles(final String historyId, final Workflow workflow) throws InterruptedException {
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
     * @param historyId the ID of the history to use for workflow input and output.
     * @param workflow  the workflow.
     * @return the workflow inputs object.
     */
    private WorkflowInputs createInputsObject(final String historyId, final Workflow workflow) {
        logger.info("- Create the workflow inputs object.");
        final WorkflowInputs inputs = new WorkflowInputs();
        inputs.setDestination(new WorkflowInputs.ExistingHistory(historyId));
        final String galaxyWorkflowId = getGalaxyWorkflowId(workflow.getName());
        logger.trace("galaxyWorkflowId: {}.", galaxyWorkflowId);
        inputs.setWorkflowId(galaxyWorkflowId);
        final WorkflowDetails workflowDetails = workflowsClient.showWorkflow(galaxyWorkflowId);
        for (final Map.Entry<String, Object> inputEntry : workflow.getAllInputEntries()) {
            final String fileName = ((File) inputEntry.getValue()).getName();
            final String inputId = new HistoryUtils().getDatasetIdByName(fileName, historiesClient, historyId);
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
     * @param historyId      the ID of the history to use for workflow input and output.
     * @param workflowInputs the blend4j workflow inputs.
     * @return whether the workflow finished successfully.
     * @throws InterruptedException if any thread has interrupted the current thread while waiting for Galaxy.
     */
    private boolean executeWorkflow(final String historyId, final WorkflowInputs workflowInputs)
            throws InterruptedException {
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
     * Download all output files and add them as results to the workflow object.
     *
     * @param workflow  the workflow.
     * @param historyId the ID of the history to use for workflow input and output.
     * @return whether all output files were downloaded successfully.
     */
    private boolean downloadOutputFiles(final Workflow workflow, final String historyId) {
        boolean success = true;
        try {
            for (final String outputId : workflowOutputs.getOutputIds()) {
                final Dataset dataset = historiesClient.showDataset(historyId, outputId);
                final String outputName = dataset.getName() != null ? dataset.getName() : outputId;
                // todo: make downloading optional (only some files might be needed) and use configurable output directory.
                final String prefix = String.format("workflow-runner-%s-%s-", historyId, outputName);
                final File outputFile = File.createTempFile(prefix, ".txt");
                logger.trace("Downloading output {} to local file {}.", outputName, outputFile.getAbsolutePath());
                // todo: is it necessary to fill in the data type (last parameter)?
                success &= new HistoryUtils().downloadDataset(galaxyInstance, historiesClient, historyId, outputId,
                                                              outputFile.getAbsolutePath(), false, null);
                workflow.addOutput(outputName, outputFile);
            }
        } catch (final IllegalArgumentException | IOException | SecurityException e) {
            logger.error("Error downloading a workflow output file.", e);
            success = false;
        }
        return success;
    }

    /**
     * Check the results of the workflow.
     *
     * @param historyId the ID of the history to use for workflow input and output.
     * @return whether the workflow results appear to be valid.
     * @throws IOException if reading the workflow results fails.
     */
    private boolean checkWorkflowResults(final String historyId) throws IOException {
        boolean valid = true;
        logger.info("Check outputs.");
        for (final String outputId : workflowOutputs.getOutputIds())
            logger.info("- Workflow output ID: " + outputId + ".");
        final int outputCount = workflowOutputs.getOutputIds().size();
        if (outputCount == 0)
            logger.warn("No workflow output found.");
        else if (outputCount > 1)
            logger.warn("More than one workflow outputs found ({}).", outputCount);
        if (outputCount > 0) {
            // Freek: the last workflow output file is most likely to be the end result?
            final String outputDatasetId = workflowOutputs.getOutputIds().get(outputCount - 1);
            new HistoryUtils().downloadDataset(galaxyInstance, historiesClient, historyId, outputDatasetId,
                                               OUTPUT_FILE_PATH, false, null);
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
