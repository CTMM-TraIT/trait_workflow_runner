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
import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryContents;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryDetails;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowDetails;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputDefinition;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowOutputs;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowStepDefinition;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.sun.jersey.api.client.ClientResponse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.vumc.biomedbridges.core.Constants;
import nl.vumc.biomedbridges.core.FileUtils;
import nl.vumc.biomedbridges.core.Workflow;
import nl.vumc.biomedbridges.galaxy.configuration.GalaxyConfiguration;

import org.apache.http.HttpStatus;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for the GalaxyWorkflowEngine class.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class GalaxyWorkflowEngineTest {
    /**
     * The resources directory for the galaxy package.
     */
    private static final String GALAXY_DIRECTORY = Paths.get(
            "src", "test", "resources", "nl", "vumc", "biomedbridges", "galaxy"
    ) + File.separator;

    /**
     * Workflow output file path.
     */
    private static final String TMP_DIRECTORY = Paths.get("tmp").toString();

    /**
     * Test the configure method with nonsense configuration data.
     */
    @Test
    public void testConfigureNonsense() {
        // todo: move this test and the next two tests to another test class: GalaxyConfigurationTest.
        assertNull(new GalaxyConfiguration().determineGalaxyInstance("nonsense"));
    }

    /**
     * Test the configure method with configuration data that appears to be valid but is not.
     */
    @Test
    public void testConfigureInvalid() {
        final String configurationData = GalaxyConfiguration.PROPERTY_SEPARATOR
                                         + GalaxyConfiguration.KEY_VALUE_SEPARATOR
                                         + GalaxyConfiguration.GALAXY_INSTANCE_PROPERTY_KEY
                                         + GalaxyConfiguration.KEY_VALUE_SEPARATOR
                                         + GalaxyConfiguration.API_KEY_PROPERTY_KEY
                                         + GalaxyConfiguration.KEY_VALUE_SEPARATOR;
        assertNull(new GalaxyConfiguration().determineGalaxyInstance(configurationData));
    }

    /**
     * Test the configure method with valid configuration data.
     */
    @Test
    public void testConfigureValid() {
        final String apiKey = "some-api-key";
        final String historyName = "some-history-name";
        final String configurationData = GalaxyConfiguration.GALAXY_INSTANCE_PROPERTY_KEY
                                         + GalaxyConfiguration.KEY_VALUE_SEPARATOR + Constants.CENTRAL_GALAXY_URL
                                         + GalaxyConfiguration.PROPERTY_SEPARATOR
                                         + GalaxyConfiguration.API_KEY_PROPERTY_KEY
                                         + GalaxyConfiguration.KEY_VALUE_SEPARATOR
                                         + apiKey
                                         + GalaxyConfiguration.PROPERTY_SEPARATOR
                                         + GalaxyConfiguration.HISTORY_NAME_PROPERTY_KEY
                                         + GalaxyConfiguration.KEY_VALUE_SEPARATOR
                                         + historyName;
        final GalaxyConfiguration galaxyConfiguration = new GalaxyConfiguration();
        assertNotNull(galaxyConfiguration.determineGalaxyInstance(configurationData));
        assertEquals(apiKey, galaxyConfiguration.getGalaxyApiKey());
        assertEquals(historyName, galaxyConfiguration.getGalaxyHistoryName());
    }

    /**
     * Test the runWorkflow method with automatic downloading enabled and normal order for the output IDs.
     */
    @Test
    public void testRunWorkflowAutomaticDownload() throws Exception {
        for (int outputIdCount = 0; outputIdCount < 3; outputIdCount++)
            runWorkflowTest(true, true, outputIdCount, true);
    }

    /**
     * Test the runWorkflow method with automatic downloading disabled and reversed order for the output IDs.
     */
    @Test
    public void testRunWorkflowManualDownload() throws Exception {
        for (int outputIdCount = 0; outputIdCount < 3; outputIdCount++)
            runWorkflowTest(false, false, outputIdCount, false);
    }

    /**
     * Run the workflow test with two parameters.
     *
     * @param automaticDownload   whether the automatic download option should be enabled or disabled.
     * @param historyReady        whether uploading files and workflow running finishes within the time limits.
     * @param outputIdCount       the number of output IDs the workflow should produce.
     * @param normalOutputIdOrder whether the normal or reversed order should be used for the output IDs.
     * @throws IOException          if reading the workflow results fails.
     * @throws InterruptedException if any thread has interrupted the current thread while waiting for the workflow
     *                              engine.
     */
    private void runWorkflowTest(final boolean automaticDownload, final boolean historyReady, final int outputIdCount,
                                 final boolean normalOutputIdOrder)
            throws InterruptedException, IOException {
        final String historyId = "history-id";
        final String workflowId = "workflow-id";
        final String workflowName = "workflow-name";
        final String inputLabel = "input-label";
        final Object dummyInputFile = new File(GALAXY_DIRECTORY + "TestWorkflow.ga");
        final Collection<Object> inputValues = ImmutableList.of(dummyInputFile, dummyInputFile);
        final Map<String, List<String>> stateIds = new HashMap<>();
        stateIds.put("running", historyReady ? new ArrayList<String>() : Collections.singletonList("some-dataset-id"));
        stateIds.put("queued", new ArrayList<String>());
        stateIds.put("ok", new ArrayList<String>());
        final com.github.jmchilton.blend4j.galaxy.beans.Workflow blend4jWorkflow
                = new com.github.jmchilton.blend4j.galaxy.beans.Workflow();
        blend4jWorkflow.setName(workflowName);
        blend4jWorkflow.setId(workflowId);
        final Set<Map.Entry<String, Object>> inputEntries
                = ImmutableSet.of(Maps.immutableEntry(inputLabel, (Object) new File("non-existing-file")));
        final WorkflowInputDefinition workflowInputDefinition = new WorkflowInputDefinition();
        workflowInputDefinition.setLabel(inputLabel);
        final Map<String, WorkflowInputDefinition> inputDefinitionMap
                = ImmutableMap.of(inputLabel, workflowInputDefinition);
        final String stepId = "1";
        final Map<Object, Map<String, Object>> parameters
                = ImmutableMap.of((Object) stepId, (Map<String, Object>) ImmutableMap.of("parameter", (Object) "value"));
        final Map<String, WorkflowStepDefinition> workflowSteps = ImmutableMap.of(stepId, new WorkflowStepDefinition());
        final String outputId1 = "oid-1";
        final String outputId2 = "oid-2";

        final GalaxyWorkflow galaxyWorkflowMock = Mockito.mock(GalaxyWorkflow.class);
        final GalaxyInstance galaxyInstanceMock = Mockito.mock(GalaxyInstance.class);
        final WorkflowsClient workflowsClientMock = Mockito.mock(WorkflowsClient.class);
        final ToolsClient toolsClientMock = Mockito.mock(ToolsClient.class);
        final ClientResponse clientResponse1 = Mockito.mock(ClientResponse.class);
        final ClientResponse clientResponse2 = Mockito.mock(ClientResponse.class);
        final HistoriesClient historiesClientMock = Mockito.mock(HistoriesClient.class);
        final History historyMock = Mockito.mock(History.class);
        final HistoryDetails historyDetailsMock = Mockito.mock(HistoryDetails.class);
        final WorkflowOutputs workflowOutputsMock = Mockito.mock(WorkflowOutputs.class);
        final WorkflowDetails workflowDetailsMock = Mockito.mock(WorkflowDetails.class);
        final Dataset datasetMock1 = Mockito.mock(Dataset.class);
        final Dataset datasetMock2 = Mockito.mock(Dataset.class);
        final HistoryUtils historyUtilsMock = Mockito.mock(HistoryUtils.class);

        if (automaticDownload)
            Mockito.when(galaxyWorkflowMock.getDownloadDirectory()).thenReturn(TMP_DIRECTORY);
        Mockito.when(galaxyInstanceMock.getWorkflowsClient()).thenReturn(workflowsClientMock);
        Mockito.when(galaxyInstanceMock.getHistoriesClient()).thenReturn(historiesClientMock);
        Mockito.when(galaxyInstanceMock.getToolsClient()).thenReturn(toolsClientMock);
        Mockito.when(historiesClientMock.create(Mockito.any(History.class))).thenReturn(historyMock);
        Mockito.when(historyMock.getId()).thenReturn(historyId);
        Mockito.when(galaxyWorkflowMock.getAllInputValues()).thenReturn(inputValues);
        Mockito.when(toolsClientMock.uploadRequest(Mockito.any(ToolsClient.FileUploadRequest.class)))
                .thenReturn(clientResponse1, clientResponse2);
        Mockito.when(clientResponse1.getStatus()).thenReturn(HttpStatus.SC_OK);
        Mockito.when(clientResponse2.getStatus()).thenReturn(HttpStatus.SC_INSUFFICIENT_STORAGE);
        Mockito.when(historiesClientMock.showHistory(historyId)).thenReturn(historyDetailsMock);
        Mockito.when(historyDetailsMock.getStateIds()).thenReturn(stateIds);
        Mockito.when(workflowsClientMock.runWorkflow(Mockito.any(WorkflowInputs.class))).thenReturn(workflowOutputsMock);
        Mockito.when(workflowOutputsMock.getHistoryId()).thenReturn(historyId);
        Mockito.when(galaxyWorkflowMock.getName()).thenReturn(workflowName);
        Mockito.when(workflowsClientMock.getWorkflows()).thenReturn(ImmutableList.of(blend4jWorkflow));
        Mockito.when(workflowsClientMock.showWorkflow(Mockito.eq(workflowId))).thenReturn(workflowDetailsMock);
        Mockito.when(galaxyWorkflowMock.getAllInputEntries()).thenReturn(inputEntries);
        Mockito.when(workflowDetailsMock.getInputs()).thenReturn(inputDefinitionMap);
        Mockito.when(galaxyWorkflowMock.getParameters()).thenReturn(parameters);
        Mockito.when(workflowDetailsMock.getSteps()).thenReturn(workflowSteps);
        final List<String> workflowOutputIds = getWorkflowOutputIds(outputIdCount, normalOutputIdOrder, outputId1, outputId2);
        Mockito.when(workflowOutputsMock.getOutputIds()).thenReturn(workflowOutputIds);
        final List<HistoryContents> historyContentsList = getHistoryContentsList(workflowOutputIds);
        Mockito.when(historiesClientMock.showHistoryContents(Mockito.eq(historyId))).thenReturn(historyContentsList);
        Mockito.when(galaxyWorkflowMock.getAutomaticDownload()).thenReturn(automaticDownload);
        if (automaticDownload) {
            Mockito.when(historiesClientMock.showDataset(Mockito.eq(historyId), Mockito.eq(outputId1)))
                    .thenReturn(datasetMock1);
            Mockito.when(datasetMock1.getDataTypeExt()).thenReturn(GalaxyWorkflowEngine.FILE_TYPE_TABULAR);
            Mockito.when(historiesClientMock.showDataset(Mockito.eq(historyId), Mockito.eq(outputId2)))
                    .thenReturn(datasetMock2);
            Mockito.when(datasetMock2.getDataTypeExt()).thenReturn(GalaxyWorkflowEngine.FILE_TYPE_TEXT);
        }

//        final Answer<Boolean> downloadDatasetAnswer = invocationOnMock -> {
//            final int datasetIdArgumentIndex = 3;
//            final File outputFile = new File(GalaxyWorkflowEngine.OUTPUT_FILE_PATH);
//            if (outputId2.equals(invocationOnMock.getArguments()[datasetIdArgumentIndex])) {
//                if (outputFile.exists())
//                    assertTrue(outputFile.delete());
//            } else {
//                if (!outputFile.exists())
//                    FileUtils.createFile(outputFile.getAbsolutePath(), "GalaxyWorkflowEngineTest.testRunWorkflow");
//            }
//            return true;
//        };
        final Answer<Boolean> downloadDatasetAnswer = new Answer<Boolean>() {
            @Override
            public Boolean answer(final InvocationOnMock invocationOnMock) throws Throwable {
                final int datasetIdArgumentIndex = 3;
                final File outputFile = new File(GalaxyWorkflowEngine.OUTPUT_FILE_PATH);
                if (outputId2.equals(invocationOnMock.getArguments()[datasetIdArgumentIndex])) {
                    if (outputFile.exists())
                        assertTrue(outputFile.delete());
                } else {
                    if (!outputFile.exists())
                        FileUtils.createFile(outputFile.getAbsolutePath(), "GalaxyWorkflowEngineTest.testRunWorkflow");
                }
                return true;
            }
        };
        Mockito.when(historyUtilsMock.downloadDataset(Mockito.eq(galaxyInstanceMock), Mockito.eq(historiesClientMock),
                                                      Mockito.eq(historyId), Mockito.anyString(),
                                                      Mockito.eq(GalaxyWorkflowEngine.OUTPUT_FILE_PATH)))
                .thenAnswer(downloadDatasetAnswer);

        final GalaxyWorkflowEngine galaxyWorkflowEngine = new GalaxyWorkflowEngine(galaxyInstanceMock, historyId,
                                                                                   historyUtilsMock);

        // Set all timers to zero to make the test as quick as possible.
        galaxyWorkflowEngine.setWaitTimers(0, 0, 0);

        // Downloading fails, so we expect the result to be false if automaticDownload is true and true otherwise.
        final boolean expectedResult = historyReady && (!automaticDownload || outputIdCount == 0);
        assertEquals(expectedResult, galaxyWorkflowEngine.runWorkflow(galaxyWorkflowMock));
    }

    /**
     * Get the workflow output IDs for a specific test scenario.
     *
     * @param outputIdCount       the number of output IDs to return.
     * @param normalOutputIdOrder whether to use normal or reversed order.
     * @param outputId1           the first output ID.
     * @param outputId2           the second output ID.
     * @return the workflow output IDs.
     */
    private List<String> getWorkflowOutputIds(final int outputIdCount, final boolean normalOutputIdOrder,
                                              final String outputId1, final String outputId2) {
        final List<String> workflowOutputIds;
        if (outputIdCount == 0)
            workflowOutputIds = new ArrayList<>();
        else if (outputIdCount == 1)
            workflowOutputIds = ImmutableList.of(normalOutputIdOrder ? outputId1 : outputId2);
        else {
            if (normalOutputIdOrder)
                workflowOutputIds = ImmutableList.of(outputId1, outputId2);
            else
                workflowOutputIds = ImmutableList.of(outputId2, outputId1);
        }
        return workflowOutputIds;
    }

    /**
     * Get the history contents list that corresponds to the workflow output IDs.
     *
     * @param workflowOutputIds the workflow output IDs.
     * @return the history contents list.
     */
    private List<HistoryContents> getHistoryContentsList(final List<String> workflowOutputIds) {
        final List<HistoryContents> historyContentsList = new ArrayList<>();
        for (final String workflowOutputId : workflowOutputIds) {
            final HistoryContents historyContents = new HistoryContents();
            historyContents.setHid(workflowOutputId.hashCode());
            historyContents.setName("name-for-" + workflowOutputId);
            historyContentsList.add(historyContents);
        }
        return historyContentsList;
    }

    /**
     * Test the downloadOutputFile method with invalid input.
     *
     * @throws IOException if a local file could not be created.
     */
    @Test
    public void testDownloadOutputFile() throws IOException, NoSuchFieldException, IllegalAccessException {
        final String historyId = "history-id";
        final String outputId = "output-id";

        final GalaxyInstance galaxyInstanceMock = Mockito.mock(GalaxyInstance.class);
        final HistoriesClient historiesClientMock = Mockito.mock(HistoriesClient.class);
        final Dataset datasetMock = Mockito.mock(Dataset.class);
        final HistoryUtils historyUtilsMock = Mockito.mock(HistoryUtils.class);

        Mockito.when(galaxyInstanceMock.getHistoriesClient()).thenReturn(historiesClientMock);
        Mockito.when(historiesClientMock.showDataset(Mockito.eq(historyId), Mockito.eq(outputId))).thenReturn(datasetMock);

        final GalaxyWorkflowEngine galaxyWorkflowEngine = new GalaxyWorkflowEngine(galaxyInstanceMock, historyId,
                                                                                   historyUtilsMock);
        final Workflow workflow = galaxyWorkflowEngine.getWorkflow("workflow-name");

        assertFalse("Downloading an output file with invalid input should fail.",
                    galaxyWorkflowEngine.downloadOutputFile(workflow, outputId));
    }

    /**
     * Test the getOutputIdForOutputName method.
     */
    @Test
    public void testGetOutputIdForOutputName() {
        final GalaxyWorkflowEngine galaxyWorkflowEngine = new GalaxyWorkflowEngine(null, null, null);
        assertNull(galaxyWorkflowEngine.getOutputIdForOutputName(null));
    }

    /**
     * Test the runWorkflow method with a null Galaxy instance.
     */
    @Test
    public void testRunWorkflowGalaxyNull() throws IOException, InterruptedException {
        final GalaxyWorkflowEngine galaxyWorkflowEngine = new GalaxyWorkflowEngine(null, null, null);
        assertFalse(galaxyWorkflowEngine.runWorkflow(null));
    }
}
