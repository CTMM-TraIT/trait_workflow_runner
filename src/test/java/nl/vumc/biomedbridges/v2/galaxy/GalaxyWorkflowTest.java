/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.biomedbridges.v2.galaxy;

import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nl.vumc.biomedbridges.v2.core.WorkflowRunnerVersion2;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

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
        final GalaxyWorkflow concatenateWorkflow = new GalaxyWorkflow(WorkflowRunnerVersion2.TEST_WORKFLOW_NAME_1);
        final List<Map<String, String>> expectedInputs = new ArrayList<>();
        expectedInputs.add(ImmutableMap.of("description", "", "name", "WorkflowInput1"));
        expectedInputs.add(ImmutableMap.of("description", "", "name", "WorkflowInput2"));
        assertEquals(expectedInputs, concatenateWorkflow.getInputs());
        final List<Map<String, String>> expectedOutputs = new ArrayList<>();
        expectedOutputs.add(ImmutableMap.of("name", "out_file1", "type", "input"));
        assertEquals(expectedOutputs, concatenateWorkflow.getOutputs());
    }
}
