/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
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
     * Test the constructor, the json parsing, and the getInputs & getOutputs methods.
     */
    @Test
    public void testConstructorAndJsonParsing() {
        final GalaxyWorkflow concatenateWorkflow = new GalaxyWorkflow(Constants.TEST_WORKFLOW_CONCATENATE);
        final List<Map<String, String>> expectedInputs = new ArrayList<>();
        expectedInputs.add(ImmutableMap.of("description", "", "name", "WorkflowInput1"));
        expectedInputs.add(ImmutableMap.of("description", "", "name", "WorkflowInput2"));
        assertEquals(expectedInputs, concatenateWorkflow.getInputs());
        final List<Map<String, String>> expectedOutputs = new ArrayList<>();
        expectedOutputs.add(ImmutableMap.of("name", "out_file1", "type", "input"));
        assertEquals(expectedOutputs, concatenateWorkflow.getOutputs());
    }

    /**
     * Test the ensureWorkflowIsOnServer method.
     */
    @Test
    public void testEnsureWorkflowIsOnServer() {
        final GalaxyWorkflow concatenateWorkflow = new GalaxyWorkflow(Constants.TEST_WORKFLOW_CONCATENATE);
        final WorkflowsClient workflowsClientMock = Mockito.mock(WorkflowsClient.class);
        final com.github.jmchilton.blend4j.galaxy.beans.Workflow blend4jWorkflowMock
                = Mockito.mock(com.github.jmchilton.blend4j.galaxy.beans.Workflow.class);
        Mockito.when(workflowsClientMock.getWorkflows()).thenReturn(Arrays.asList(blend4jWorkflowMock));
        Mockito.when(blend4jWorkflowMock.getName()).thenReturn(Constants.TEST_WORKFLOW_CONCATENATE);
        assertTrue(concatenateWorkflow.ensureWorkflowIsOnServer(workflowsClientMock));
    }
}
