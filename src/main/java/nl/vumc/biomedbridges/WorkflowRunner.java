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
import nl.vumc.biomedbridges.configuration.Configuration;
import org.apache.http.HttpStatus;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs.InputSourceType;
import static com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs.WorkflowInput;

/**
 * This class contains a simple test of the Galaxy API for workflows. The code is based on the WorkflowsTest class from
 * the blend4j library.
 * <p/>
 * todo 1: check status output files using history
 * todo 2: check the Maven build.xml file
 * todo 3: use a logging library
 * todo 4: Javadocs, unit tests, Checkstyle, FindBugs, etc.
 *
 * (todo minor: Google(intellij do not force braces) -> try in small, different project first)
 *
 * Interesting links (BioBlend is a Python library for the Galaxy API):
 * - BioBlend presentation: 7:00 - http://vimeo.com/74403037
 * - BioBlend documentation: https://github.com/afgane/bioblend
 * - Galaxy API training: https://wiki.galaxyproject.org/Events/GCC2013/TrainingDay/API
 * - Galaxy API documentation: https://wiki.galaxyproject.org/Learn/API
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class WorkflowRunner {
    private static final String GALAXY_INSTANCE_URL = Configuration.getGalaxyInstanceUrl();
    private static final String GALAXY_API_KEY = Configuration.getGalaxyApiKey();

    /**
     * The name of the test workflow.
     */
    private static final String TEST_WORKFLOW_NAME = "TestWorkflowConcatenate";

    /**
     * The json design of the test workflow.
     */
    private static final String TEST_WORKFLOW_JSON = getWorkflowJson(TEST_WORKFLOW_NAME + ".ga");

    private static final int UPLOAD_MAX_WAIT_BLOCKS = 10;
    private static final int UPLOAD_WAIT_SECONDS = 6;

    private static final int WORKFLOW_WAIT_MAX_WAIT_BLOCKS = 10;
    private static final int WORKFLOW_WAIT_SECONDS = 3;

    private static final String TEST_FILE_LINE_1 = "Hello Galaxy!!!";
    private static final String TEST_FILE_LINE_2 = "Do you wanna play?";

    private static final SimpleDateFormat LOG_TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");

    /**
     * Main method.
     *
     * @param arguments unused command-line arguments.
     */
    public static void main(final String[] arguments) {
        try {
            final long startTime = System.currentTimeMillis();
            final Map<String, Object> workflowInputs = new HashMap<>();
            workflowInputs.put("WorkflowInput1", getTestFile(TEST_FILE_LINE_1));
            workflowInputs.put("WorkflowInput2", getTestFile(TEST_FILE_LINE_2));
            new WorkflowRunner().runWorkflow(GALAXY_INSTANCE_URL, GALAXY_API_KEY, TEST_WORKFLOW_NAME,
                                             TEST_WORKFLOW_JSON, workflowInputs);
            final double durationSeconds = (System.currentTimeMillis() - startTime) / 1000.0;
            log();
            log("Running the workflow took " + durationSeconds + " seconds.");
        } catch (final Exception e) {
            e.printStackTrace();
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
     * @throws InterruptedException
     * @throws IOException
     */
    private void runWorkflow(final String galaxyInstanceUrl, final String galaxyApiKey, final String workflowName,
                             final String workflowJson, final Map<String, Object> workflowInputs)
            throws InterruptedException, IOException {
        log("nl.vumc.biomedbridges.WorkflowRunner.runWorkflow");
        log();
        log("Galaxy server: " + galaxyInstanceUrl);
        log("Galaxy API key: " + galaxyApiKey);
        log();

        final GalaxyInstance galaxyInstance = GalaxyInstanceFactory.get(galaxyInstanceUrl, galaxyApiKey);
        final WorkflowsClient workflowsClient = galaxyInstance.getWorkflowsClient();

        log("Ensure the test workflow is available.");
        ensureHasWorkflow(workflowsClient, workflowName, workflowJson);

        log("Prepare the input files.");
        final HistoriesClient historiesClient = galaxyInstance.getHistoriesClient();
        final String historyId = getTestHistoryId(galaxyInstance);
        final WorkflowInputs inputs = prepareConcatenationWorkflow(galaxyInstance, workflowsClient, historyId,
                                                                   historiesClient, workflowName, workflowInputs);

        final int expectedOutputLength = TEST_FILE_LINE_1.length() + TEST_FILE_LINE_2.length() + 2;
        final WorkflowOutputs workflowOutputs = workflowsClient.runWorkflow(inputs);
        log("Running the workflow (history ID: " + workflowOutputs.getHistoryId() + ").");
        boolean finished = false;
        int waitCount = 0;
        while (!finished && waitCount < WORKFLOW_WAIT_MAX_WAIT_BLOCKS) {
            log("- Now waiting for " + WORKFLOW_WAIT_SECONDS + " seconds...");
            Thread.sleep(WORKFLOW_WAIT_SECONDS * 1000);
            final long outputLength = getOutputLength(galaxyInstance, workflowOutputs, historiesClient, historyId);
            log("- Output length: " + outputLength + " (expect: " + expectedOutputLength + ").");
            finished = outputLength == expectedOutputLength;
            waitCount++;
        }
        // todo: check status output files using the history instead of the code above?!?

        if (finished) {
            log("Check outputs.");
            for (final String outputId : workflowOutputs.getOutputIds())
                log("- Workflow output ID: " + outputId + ".");
            final String concatenationFilePath = "WorkflowRunner-runWorkflow.txt";
            final File concatenationFile = new File(concatenationFilePath);
            try {
                assert workflowOutputs.getOutputIds().size() == 1;
//                historiesClient.downloadDataset(historyId, workflowOutputs.getOutputIds().get(0),
//                                                concatenationFilePath, false, null);
                HistoryUtils.downloadDataset(galaxyInstance, historiesClient, historyId,
                                             workflowOutputs.getOutputIds().get(0), concatenationFilePath, false, null);
                if (concatenationFile.exists())
                    log("- Concatenated file exists.");
                else
                    log("- Concatenated file does not exist!");
                assert concatenationFile.length() == expectedOutputLength;
                final List<String> lines = Files.readLines(concatenationFile, Charsets.UTF_8);
                if (Arrays.asList(TEST_FILE_LINE_1, TEST_FILE_LINE_2).equals(lines))
                    log("- Concatenated file contains the lines we expected!!!");
                else
                    log("- Concatenated file does not contain the lines we expected (lines: " + lines + ")!");
            } finally {
                assert concatenationFile.delete();
            }
        } else
            log("Timeout while waiting for workflow output file(s).");
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

    private String getTestHistoryId(final GalaxyInstance instance) {
        final History testHistory = new History("blend4j Test History");
        final History newHistory = instance.getHistoriesClient().create(testHistory);
        return newHistory.getId();
    }

    //* @param workflowName the name of the workflow.
    private WorkflowInputs prepareConcatenationWorkflow(final GalaxyInstance instance, final WorkflowsClient client,
                                                        final String historyId, final HistoriesClient historiesClient,
                                                        final String workflowName,
                                                        final Map<String, Object> workflowInputs)
            throws InterruptedException {
        log("- Upload the input files.");
        for (final Object inputObject : workflowInputs.values())
            if (inputObject instanceof File)
                uploadInputFile(instance, historyId, (File) inputObject);
        log("- Waiting for upload to history to finish.");
        waitForHistoryUpload(instance.getHistoriesClient(), historyId);
        log("- Create the workflow inputs object.");
        final WorkflowInputs inputs = new WorkflowInputs();
        inputs.setDestination(new WorkflowInputs.ExistingHistory(historyId));
        final String testWorkflowId = getTestWorkflowId(client, workflowName);
        inputs.setWorkflowId(testWorkflowId);
        final WorkflowDetails workflowDetails = client.showWorkflow(testWorkflowId);
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

    private static File getTestFile(final String line) {
        try {
            final File tempFile = File.createTempFile("workflow-runner", ".txt");
            try (final FileWriter writer = new FileWriter(tempFile)) {
                writer.write(line);
            }
            return tempFile;
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ClientResponse uploadInputFile(final GalaxyInstance galaxyInstance, final String historyId,
                                           final File testFile) {
        final ToolsClient.FileUploadRequest request = new ToolsClient.FileUploadRequest(historyId, testFile);
        final ClientResponse clientResponse = galaxyInstance.getToolsClient().uploadRequest(request);
        assert clientResponse.getStatus() == HttpStatus.SC_OK;
        return clientResponse;
    }

    private void waitForHistoryUpload(final HistoriesClient client, final String historyId)
            throws InterruptedException {
        HistoryDetails details = null;
        boolean finished = false;
        int waitCount = 0;
        while (!finished && waitCount < UPLOAD_MAX_WAIT_BLOCKS) {
            log("  + Now waiting for " + UPLOAD_WAIT_SECONDS + " seconds...");
            Thread.sleep(UPLOAD_WAIT_SECONDS * 1000);
            details = client.showHistory(historyId);
            log("  + History is " + (details.isReady() ? "ready." : "not ready yet."));
            finished = details.isReady();
        }
        final String state = details != null ? details.getState() : "timeout";
        if (!state.equals("ok"))
            throw new RuntimeException("History no longer running, but not in 'ok' state. State is: " + state);
        Thread.sleep(200L);
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

    private long getOutputLength(final GalaxyInstance galaxyInstance, final WorkflowOutputs workflowOutputs,
                                 final HistoriesClient historiesClient, final String historyId) {
        long outputLength = -1;
        if (workflowOutputs.getOutputIds().size() == 1) {
            final String concatenationFilePath = "WorkflowRunner-runWorkflow.txt";
            final File concatenationFile = new File(concatenationFilePath);
            try {
//                historiesClient.downloadDataset(historyId, workflowOutputs.getOutputIds().get(0),
//                                                concatenationFilePath, false, null);
                HistoryUtils.downloadDataset(galaxyInstance, historiesClient, historyId,
                                             workflowOutputs.getOutputIds().get(0), concatenationFilePath, false, null);
                if (concatenationFile.exists())
                    outputLength = concatenationFile.length();
                else
                    outputLength = -2;
            } finally {
                assert concatenationFile.delete();
            }
        }
        return outputLength;
    }

    private static void log() {
        log("");
    }

    private static void log(final String line) {
        System.out.println("[" + LOG_TIME_FORMAT.format(new Date()) + "] " + line);
    }

    private static String getWorkflowJson(final String workflowFileName) {
        try {
            return Resources.asCharSource(WorkflowRunner.class.getResource(workflowFileName), Charsets.UTF_8).read();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
