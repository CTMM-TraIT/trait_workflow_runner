/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.galaxy;

import com.github.jmchilton.blend4j.galaxy.WorkflowsClient;
import com.google.common.collect.ImmutableMap;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import nl.vumc.biomedbridges.core.Constants;
import nl.vumc.biomedbridges.core.FileUtils;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for the GalaxyWorkflow class.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class GalaxyWorkflowTest {
    /**
     * Test the constructor, the json parsing, and the getInputsMetadata & getOutputsMetadata methods.
     */
    @Test
    public void testConstructorAndJsonParsing() {
        final GalaxyWorkflow concatenateWorkflow = new GalaxyWorkflow(Constants.CONCATENATE_WORKFLOW, null, new JSONParser());
        final List<Map<String, String>> expectedInputs = new ArrayList<>();
        expectedInputs.add(ImmutableMap.of("description", "", "name", "WorkflowInput1"));
        expectedInputs.add(ImmutableMap.of("description", "", "name", "WorkflowInput2"));
        assertEquals(expectedInputs, concatenateWorkflow.getInputsMetadata());
        final List<Map<String, String>> expectedOutputs = new ArrayList<>();
        expectedOutputs.add(ImmutableMap.of("name", "out_file1", "type", "input"));
        assertEquals(expectedOutputs, concatenateWorkflow.getOutputsMetadata());
    }

    /**
     * Test the ensureWorkflowIsOnServer method when the workflow is already on the Galaxy server.
     */
    @Test
    public void testEnsureWorkflowIsOnServerAlreadyThere() {
        final GalaxyWorkflow concatenateWorkflow = new GalaxyWorkflow(Constants.CONCATENATE_WORKFLOW, null, new JSONParser());
        final WorkflowsClient workflowsClientMock = Mockito.mock(WorkflowsClient.class);
        final com.github.jmchilton.blend4j.galaxy.beans.Workflow blend4jWorkflowMock
            = Mockito.mock(com.github.jmchilton.blend4j.galaxy.beans.Workflow.class);
        Mockito.when(workflowsClientMock.getWorkflows()).thenReturn(Collections.singletonList(blend4jWorkflowMock));
        Mockito.when(blend4jWorkflowMock.getName()).thenReturn(Constants.CONCATENATE_WORKFLOW);
        assertTrue(concatenateWorkflow.ensureWorkflowIsOnServer(workflowsClientMock));
    }

    /**
     * Test the ensureWorkflowIsOnServer method when the workflow is not on the Galaxy server and is imported.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testEnsureWorkflowIsOnServerWithImport() {
        final GalaxyWorkflow concatenateWorkflow = new GalaxyWorkflow(Constants.CONCATENATE_WORKFLOW, null, new JSONParser());
        final WorkflowsClient workflowsClientMock = Mockito.mock(WorkflowsClient.class);
        final com.github.jmchilton.blend4j.galaxy.beans.Workflow blend4jWorkflowMock1
            = getBlend4jWorkflowMock("dummy");
        final com.github.jmchilton.blend4j.galaxy.beans.Workflow blend4jWorkflowMock2
            = getBlend4jWorkflowMock(Constants.CONCATENATE_WORKFLOW);
        final List<com.github.jmchilton.blend4j.galaxy.beans.Workflow> workflowList1 = new ArrayList<>();
        final List<com.github.jmchilton.blend4j.galaxy.beans.Workflow> workflowList2
            = Arrays.asList(blend4jWorkflowMock1, blend4jWorkflowMock2);
        Mockito.when(workflowsClientMock.getWorkflows()).thenReturn(workflowList1, workflowList2);
        assertTrue(concatenateWorkflow.ensureWorkflowIsOnServer(workflowsClientMock));
    }

    private com.github.jmchilton.blend4j.galaxy.beans.Workflow getBlend4jWorkflowMock(final String name) {
        final com.github.jmchilton.blend4j.galaxy.beans.Workflow blend4jWorkflowMock
            = Mockito.mock(com.github.jmchilton.blend4j.galaxy.beans.Workflow.class);
        Mockito.when(blend4jWorkflowMock.getName()).thenReturn(name);
        return blend4jWorkflowMock;
    }

    /**
     * Test the run method.
     */
    @Test
    public void testRun() throws IOException {
        final GalaxyWorkflowEngine workflowEngineMock = Mockito.mock(GalaxyWorkflowEngine.class);
        final GalaxyWorkflow workflow = new GalaxyWorkflow(Constants.CONCATENATE_WORKFLOW, workflowEngineMock,
                                                           new JSONParser());
        workflow.addInput("WorkflowInput1", FileUtils.createTemporaryFile("file 1 - test line"));
        workflow.addInput("WorkflowInput2", FileUtils.createTemporaryFile("file 2 - test line"));

        try {
            Mockito.when(workflowEngineMock.runWorkflow(Mockito.eq(workflow))).thenReturn(true);

            assertTrue(workflow.run());
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test the getOutput method.
     */
    @Test
    public void testGetOutput() throws IOException {
        final GalaxyWorkflowEngine workflowEngineMock = Mockito.mock(GalaxyWorkflowEngine.class);
        final GalaxyWorkflow workflow = new GalaxyWorkflow(Constants.CONCATENATE_WORKFLOW, workflowEngineMock,
                                                           new JSONParser());
        final String outputName = "test";
        final String outputId = "9876543210";
        final BigInteger dummyOutput = new BigInteger("12345678901234567890");
        final File outputFile = new File("does not exist.txt");

        assertNull(workflow.getOutput(outputName));

        // Test whether downloading is handled correctly.
        workflow.setAutomaticDownload(false);
        Mockito.when(workflowEngineMock.getOutputIdForOutputName(Mockito.eq(outputName))).thenReturn(outputId);
        final Answer<Boolean> downloadOutputFileAnswer = invocationOnMock -> {
            workflow.addOutput(outputName, outputFile);
            return true;
        };
        Mockito.when(workflowEngineMock.downloadOutputFile(Mockito.eq(workflow), Mockito.eq(outputId)))
            .thenAnswer(downloadOutputFileAnswer);
        assertEquals(outputFile, workflow.getOutput(outputName));

        workflow.addOutput(outputName, dummyOutput);
        assertEquals(dummyOutput, workflow.getOutput(outputName));

        workflow.addOutput(outputName, outputFile);
        assertEquals(outputFile, workflow.getOutput(outputName));
    }

    /**
     * Test the constructor when the JSONParser.parse method throws a parse exception (which is caught by the parseJson
     * method).
     */
    @Test
    public void testConstructorWithParseException() throws ParseException {
        final JSONParser jsonParserMock = Mockito.mock(JSONParser.class);
        Mockito.when(jsonParserMock.parse(Mockito.anyString())).thenThrow(new ParseException(6));
        new GalaxyWorkflow(Constants.CONCATENATE_WORKFLOW, null, jsonParserMock);
    }
}
