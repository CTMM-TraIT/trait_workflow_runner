/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.galaxy.metadata;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Unit tests for the GalaxyWorkflowStep class.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class GalaxyWorkflowStepTest {
    /**
     * The resources directory for the configuration package.
     */
    private static final String GALAXY_DIRECTORY = Paths.get(
            "src", "test", "resources", "nl", "vumc", "biomedbridges", "galaxy"
    ) + File.separator;

    /**
     * The workflow step to test.
     */
    private GalaxyWorkflowStep workflowStep;

    /**
     * Common initialization for all unit tests.
     *
     * @throws ParseException when parsing fails.
     * @throws IOException when reading from file fails.
     */
    @Before
    public void setUp() throws ParseException, IOException {
        final String filePath = GALAXY_DIRECTORY + "TestWorkflow.ga";
        final String jsonContent = Joiner.on("\n").join(Files.readAllLines(Paths.get(filePath), Charsets.UTF_8));
        final JSONObject workflowJson = (JSONObject) new JSONParser().parse(jsonContent);
        final JSONObject stepsMapJson = (JSONObject) workflowJson.get("steps");
        workflowStep = new GalaxyWorkflowStep((JSONObject) stepsMapJson.get("2"));
    }

    /**
     * Test getting the simple properties (and the GalaxyWorkflowStep constructor).
     */
    @Test
    public void testGettersSimplePropertiesAndConstructor() {
        assertEquals("", workflowStep.getAnnotation());
        assertEquals("Concatenate datasets", workflowStep.getName());
        assertEquals("cat1", workflowStep.getToolId());
        assertEquals("1.0.0", workflowStep.getToolVersion());
        assertEquals("tool", workflowStep.getType());
        assertEquals(2, workflowStep.getId().longValue());
    }

    /**
     * Test the getPosition method.
     */
    @Test
    public void testGetPosition() {
        assertEquals(435, workflowStep.getPosition().getLeft(), 0.0000001);
        assertEquals(200, workflowStep.getPosition().getTop(), 0.0000001);
    }

    /**
     * Test the getInputConnections method.
     */
    @Test
    public void testGetInputConnections() {
        final Map<String, GalaxyStepInputConnection> inputConnections = workflowStep.getInputConnections();
        assertEquals(2, inputConnections.size());
        final GalaxyStepInputConnection inputConnection1 = inputConnections.get("input1");
        assertEquals(0, inputConnection1.getId().longValue());
        assertEquals("output", inputConnection1.getOutputName());
        final GalaxyStepInputConnection inputConnection2 = inputConnections.get("queries_0|input2");
        assertEquals(1, inputConnection2.getId().longValue());
        assertEquals("output", inputConnection2.getOutputName());
    }

    /**
     * Test the getInputs method.
     */
    @Test
    public void testGetInputs() {
        assertEquals(0, workflowStep.getInputs().size());
    }

    /**
     * Test the getOutputs method.
     */
    @Test
    public void testGetOutputs() {
        assertEquals(1, workflowStep.getOutputs().size());
        final GalaxyStepOutput stepOutput = workflowStep.getOutputs().get(0);
        assertEquals("out_file1", stepOutput.getName());
        assertEquals("input", stepOutput.getType());
    }

    /**
     * Test the getToolErrors method.
     */
    @Test
    public void testGetToolErrors() {
        assertEquals(0, workflowStep.getToolErrors().size());
    }

    /**
     * Test the getToolState method.
     */
    @Test
    public void testGetToolState() {
        assertEquals(4, workflowStep.getToolState().size());
        assertEquals(0L, workflowStep.getToolState().get("__page__"));
        assertNull(workflowStep.getToolState().get("__rerun_remap_job_id__"));
        assertNull(workflowStep.getToolState().get("input1"));
        assertEquals("[{\"input2\": null, \"__index__\": 0}]", workflowStep.getToolState().get("queries"));
    }

    /**
     * Test the getUserOutputs method.
     */
    @Test
    public void testGetUserOutputs() {
        assertEquals(0, workflowStep.getUserOutputs().size());
    }
}
