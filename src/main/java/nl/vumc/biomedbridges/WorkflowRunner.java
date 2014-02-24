/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.biomedbridges;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.GalaxyInstanceFactory;
import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.ToolsClient;
import com.github.jmchilton.blend4j.galaxy.WorkflowsClient;
import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryDetails;
import com.github.jmchilton.blend4j.galaxy.beans.Workflow;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowDetails;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowOutputs;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.sun.jersey.api.client.ClientResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.vumc.biomedbridges.configuration.Configuration;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs.InputSourceType;
import static com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs.WorkflowInput;

/**
 * This class contains a simple test of the Galaxy API for workflows. The code is based on the WorkflowsTest class from
 * the blend4j library.
 * <p/>
 * todo 1: check status output files using history
 * done 2: check the Maven build.xml file
 * todo 3: use a logging library
 * todo 4: Javadocs, unit tests, Checkstyle, FindBugs, etc.
 * <p/>
 * (todo minor: Google(intellij do not force braces) -> try in small, different project first)
 * <p/>
 * Interesting links (BioBlend is a Python library for the Galaxy API):
 * - BioBlend presentation: 7:00 - http://vimeo.com/74403037
 * - BioBlend documentation: https://github.com/afgane/bioblend
 * - Galaxy API training: https://wiki.galaxyproject.org/Events/GCC2013/TrainingDay/API
 * - Galaxy API documentation: https://wiki.galaxyproject.org/Learn/API
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class WorkflowRunner {
    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(WorkflowRunner.class);

    /**
     * The Galaxy server URL.
     */
    private static final String GALAXY_INSTANCE_URL = Configuration.getGalaxyInstanceUrl();

    /**
     * The Galaxy API key.
     */
    private static final String GALAXY_API_KEY = Configuration.getGalaxyApiKey();

    /**
     * The name of the test workflow.
     */
    private static final String TEST_WORKFLOW_NAME = "TestWorkflowConcatenate";

    /**
     * The json design of the test workflow.
     */
    private static final String TEST_WORKFLOW_JSON = readWorkflowJson(TEST_WORKFLOW_NAME + ".ga");

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
    private static final int UPLOAD_MAX_WAIT_BLOCKS = 10;

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
     * Line 1 for the test file.
     */
    private static final String TEST_FILE_LINE_1 = "Hello Galaxy!!!";

    /**
     * Line 2 for the test file.
     */
    private static final String TEST_FILE_LINE_2 = "Do you wanna play?";

    /**
     * Workflow output file path.
     */
    private static final String OUTPUT_FILE_PATH = "WorkflowRunner-runWorkflow.txt";

    /**
     * The outputs of the executed workflow.
     */
    private WorkflowOutputs workflowOutputs;

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
            final long startTime = System.currentTimeMillis();
            final Map<String, Object> workflowInputs = new HashMap<>();
            workflowInputs.put("WorkflowInput1", getTestFile(TEST_FILE_LINE_1));
            workflowInputs.put("WorkflowInput2", getTestFile(TEST_FILE_LINE_2));
            new WorkflowRunner().runWorkflow(GALAXY_INSTANCE_URL, GALAXY_API_KEY, TEST_WORKFLOW_NAME,
                                             TEST_WORKFLOW_JSON, workflowInputs);
            final double durationSeconds = (System.currentTimeMillis() - startTime) / (float) MILLISECONDS_PER_SECOND;
            logger.info("");
            logger.info("Running the workflow took " + durationSeconds + " seconds.");
        } catch (final InterruptedException | IOException e) {
            logger.error("Exception while running workflow {}.", TEST_WORKFLOW_NAME, e);
        }
    }
    // CHECKSTYLE_OFF: UncommentedMain

    /**
     * Create a test file with a single line.
     *
     * @param line the line to write to the test file.
     * @return the test file.
     */
    private static File getTestFile(final String line) {
        try {
            final File tempFile = File.createTempFile("workflow-runner", ".txt");
            try (final Writer writer = new OutputStreamWriter(new FileOutputStream(tempFile), StandardCharsets.UTF_8)) {
                writer.write(line);
            }
            return tempFile;
        } catch (final IOException e) {
            logger.error("Exception while creating a test input file.", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Run the specified workflow on the Galaxy server.
     *
     * @param galaxyInstanceUrl the URL of the Galaxy server to run the workflow on.
     * @param galaxyApiKey      the API key to run the workflow with.
     * @param workflowName      the name of the workflow to check.
     * @param workflowJson      the json design of the workflow.
     * @param workflowInputs    the map from input names to objects (currently only files are supported).
     * @throws InterruptedException if any thread has interrupted the current thread while waiting for Galaxy.
     * @throws IOException          if reading the workflow results fails.
     */
    private void runWorkflow(final String galaxyInstanceUrl, final String galaxyApiKey, final String workflowName,
                             final String workflowJson, final Map<String, Object> workflowInputs)
            throws InterruptedException, IOException {
        logger.info("nl.vumc.biomedbridges.WorkflowRunner.runWorkflow");
        logger.info("");
        logger.info("Galaxy server: " + galaxyInstanceUrl);
        logger.info("Galaxy API key: " + galaxyApiKey);
        logger.info("");

        final GalaxyInstance galaxyInstance = GalaxyInstanceFactory.get(galaxyInstanceUrl, galaxyApiKey);
        final WorkflowsClient workflowsClient = galaxyInstance.getWorkflowsClient();

        logger.info("Ensure the test workflow is available.");
        ensureHasWorkflow(workflowsClient, workflowName, workflowJson);

        logger.info("Prepare the input files.");
        final HistoriesClient historiesClient = galaxyInstance.getHistoriesClient();
        final String historyId = getTestHistoryId(galaxyInstance);
        final WorkflowInputs inputs = prepareWorkflow(galaxyInstance, workflowsClient, historyId, historiesClient,
                                                      workflowName, workflowInputs);

        final int expectedOutputLength = TEST_FILE_LINE_1.length() + TEST_FILE_LINE_2.length() + 2;
        final boolean finished = executeWorkflow(galaxyInstance, workflowsClient, historiesClient, historyId, inputs,
                                                 expectedOutputLength);

        if (finished)
            checkWorkflowResults(galaxyInstance, historiesClient, historyId, expectedOutputLength);
        else
            logger.info("Timeout while waiting for workflow output file(s).");
    }

    /**
     * Check whether the test workflow is present. If it is not found, it will be created.
     *
     * @param workflowsClient the workflows client used to iterate all workflows.
     * @param workflowName    the name of the workflow to check.
     * @param workflowJson    the json design of the workflow.
     */
    private void ensureHasWorkflow(final WorkflowsClient workflowsClient, final String workflowName,
                                   final String workflowJson) {
        boolean found = false;
        for (final Workflow workflow : workflowsClient.getWorkflows())
            if (workflow.getName().equals(workflowName)) {
                found = true;
                break;
            }
        if (!found)
            workflowsClient.importWorkflow(workflowJson);
    }

    /**
     * Create a new history and return its ID.
     *
     * @param galaxyInstance the Galaxy instance to create the history in.
     * @return the ID of the newly created history.
     */
    private String getTestHistoryId(final GalaxyInstance galaxyInstance) {
        return galaxyInstance.getHistoriesClient().create(new History(HISTORY_NAME)).getId();
    }

    /**
     * Prepare for execution of the workflow: upload the input files and create the workflow inputs object.
     *
     * @param galaxyInstance  the Galaxy instance to run the workflow in.
     * @param workflowsClient the workflows client to interact with the workflow.
     * @param historyId       the ID of the history to use for workflow input and output.
     * @param historiesClient the histories client for accessing Galaxy histories.
     * @param workflowName    the name of the workflow.
     * @param workflowInputs  the map from input names to objects (currently only files are supported).
     * @return the workflow inputs object.
     * @throws InterruptedException if any thread has interrupted the current thread while waiting for Galaxy.
     */
    private WorkflowInputs prepareWorkflow(final GalaxyInstance galaxyInstance, final WorkflowsClient workflowsClient,
                                           final String historyId, final HistoriesClient historiesClient,
                                           final String workflowName, final Map<String, Object> workflowInputs)
            throws InterruptedException {
        logger.info("- Upload the input files.");
        for (final Object inputObject : workflowInputs.values())
            if (inputObject instanceof File)
                if (uploadInputFile(galaxyInstance, historyId, (File) inputObject).getStatus() != HttpStatus.SC_OK)
                    logger.error("Uploading file {} failed.", ((File) inputObject).getAbsolutePath());
        logger.info("- Waiting for upload to history to finish.");
        waitForHistoryUpload(galaxyInstance.getHistoriesClient(), historyId);
        logger.info("- Create the workflow inputs object.");
        final WorkflowInputs inputs = new WorkflowInputs();
        inputs.setDestination(new WorkflowInputs.ExistingHistory(historyId));
        final String testWorkflowId = getTestWorkflowId(workflowsClient, workflowName);
        inputs.setWorkflowId(testWorkflowId);
        final WorkflowDetails workflowDetails = workflowsClient.showWorkflow(testWorkflowId);
        for (final Map.Entry<String, Object> inputEntry : workflowInputs.entrySet()) {
            final String fileName = ((File) inputEntry.getValue()).getName();
//        final String inputId = historiesClient.getDatasetIdByName(fileName, historyId);
            final String inputId = HistoryUtils.getDatasetIdByName(fileName, historiesClient, historyId);
//        inputs.setInputByLabel("WorkflowInput1", workflowDetails, new WorkflowInput(input1Id, InputSourceType.HDA));
            final WorkflowInput workflowInput = new WorkflowInput(inputId, InputSourceType.HDA);
            WorkflowUtils.setInputByLabel(inputEntry.getKey(), workflowDetails, inputs, workflowInput);
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
        return galaxyInstance.getToolsClient().uploadRequest(new ToolsClient.FileUploadRequest(historyId, inputFile));
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
            throw new RuntimeException("History no longer running, but not in 'ok' state. State is: " + state);
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
        Workflow matchingWorkflow = null;
        for (final Workflow workflow : client.getWorkflows())
            if (workflow.getName().startsWith(workflowName))
                matchingWorkflow = workflow;
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
     * @param galaxyInstance       the Galaxy instance to upload the file to.
     * @param historiesClient      the histories client for accessing Galaxy histories.
     * @param historyId            the ID of the history to use for workflow input and output.
     * @param expectedOutputLength the expected output file length.
     * @throws IOException if reading the workflow results fails.
     */
    private void checkWorkflowResults(final GalaxyInstance galaxyInstance, final HistoriesClient historiesClient,
                                      final String historyId, final int expectedOutputLength) throws IOException {
        logger.info("Check outputs.");
        for (final String outputId : workflowOutputs.getOutputIds())
            logger.info("- Workflow output ID: " + outputId + ".");
        final File concatenationFile = new File(OUTPUT_FILE_PATH);
        try {
            final int outputCount = workflowOutputs.getOutputIds().size();
            if (outputCount != 1)
                logger.warn("Unexpected number of workflow outputs: {} (instead of 1).", outputCount);
//                historiesClient.downloadDataset(historyId, workflowOutputs.getOutputIds().get(0),
//                                                concatenationFilePath, false, null);
            HistoryUtils.downloadDataset(galaxyInstance, historiesClient, historyId,
                                         workflowOutputs.getOutputIds().get(0), OUTPUT_FILE_PATH, false, null);
            if (concatenationFile.exists())
                logger.info("- Concatenated file exists.");
            else
                logger.info("- Concatenated file does not exist!");
            if (concatenationFile.length() != expectedOutputLength)
                logger.warn("Output file length {} not equal to expected length {}.", concatenationFile.length(),
                            expectedOutputLength);
            final List<String> lines = Files.readLines(concatenationFile, Charsets.UTF_8);
            if (Arrays.asList(TEST_FILE_LINE_1, TEST_FILE_LINE_2).equals(lines))
                logger.info("- Concatenated file contains the lines we expected!!!");
            else
                logger.warn("- Concatenated file does not contain the lines we expected (lines: " + lines + ")!");
        } finally {
            if (!concatenationFile.delete())
                logger.error("Deleting output file {} failed (after checking contents).", OUTPUT_FILE_PATH);
        }
    }

    /**
     * Read the json design of a workflow from a file in the classpath.
     *
     * @param workflowFileName the workflow filename.
     * @return the json design of the workflow.
     */
    private static String readWorkflowJson(final String workflowFileName) {
        try {
            return Resources.asCharSource(WorkflowRunner.class.getResource(workflowFileName), Charsets.UTF_8).read();
        } catch (final IOException e) {
            logger.error("Exception while retrieving json design in workflow file {}.", workflowFileName, e);
            throw new RuntimeException(e);
        }
    }
}
