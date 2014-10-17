/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.galaxy;

import com.github.jmchilton.blend4j.galaxy.WorkflowsClient;
import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import nl.vumc.biomedbridges.core.Constants;

import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
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
        final GalaxyWorkflow concatenateWorkflow = new GalaxyWorkflow(null, Constants.CONCATENATE_WORKFLOW);
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
        final GalaxyWorkflow concatenateWorkflow = new GalaxyWorkflow(null, Constants.CONCATENATE_WORKFLOW);
        final WorkflowsClient workflowsClientMock = Mockito.mock(WorkflowsClient.class);
        final com.github.jmchilton.blend4j.galaxy.beans.Workflow blend4jWorkflowMock
                = Mockito.mock(com.github.jmchilton.blend4j.galaxy.beans.Workflow.class);
        Mockito.when(workflowsClientMock.getWorkflows()).thenReturn(Arrays.asList(blend4jWorkflowMock));
        Mockito.when(blend4jWorkflowMock.getName()).thenReturn(Constants.CONCATENATE_WORKFLOW);
        assertTrue(concatenateWorkflow.ensureWorkflowIsOnServer(workflowsClientMock));
    }

    /**
     * Test the ensureWorkflowIsOnServer method when the workflow is not on the Galaxy server and is imported.
     */
    @Test
    public void testEnsureWorkflowIsOnServerWithImport() {
        final GalaxyWorkflow concatenateWorkflow = new GalaxyWorkflow(null, Constants.CONCATENATE_WORKFLOW);
        final WorkflowsClient workflowsClientMock = Mockito.mock(WorkflowsClient.class);
        final com.github.jmchilton.blend4j.galaxy.beans.Workflow blend4jWorkflowMock1
                = getBlend4jWorkflowMock("dummy");
        final com.github.jmchilton.blend4j.galaxy.beans.Workflow blend4jWorkflowMock2
                = getBlend4jWorkflowMock(Constants.CONCATENATE_WORKFLOW);
        final List<com.github.jmchilton.blend4j.galaxy.beans.Workflow> workflowList1 = new ArrayList<>();
        final List<com.github.jmchilton.blend4j.galaxy.beans.Workflow> workflowList2
                = Arrays.asList(blend4jWorkflowMock1, blend4jWorkflowMock2);
        //noinspection unchecked
        Mockito.when(workflowsClientMock.getWorkflows()).thenReturn(workflowList1, workflowList2);
        assertTrue(concatenateWorkflow.ensureWorkflowIsOnServer(workflowsClientMock));
    }

    private com.github.jmchilton.blend4j.galaxy.beans.Workflow getBlend4jWorkflowMock(final String name) {
        final com.github.jmchilton.blend4j.galaxy.beans.Workflow blend4jWorkflowMock
                = Mockito.mock(com.github.jmchilton.blend4j.galaxy.beans.Workflow.class);
        Mockito.when(blend4jWorkflowMock.getName()).thenReturn(name);
        return blend4jWorkflowMock;
    }
}
