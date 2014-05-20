/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.biomedbridges.galaxy.metadata;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for the GalaxyWorkflowStep class.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class GalaxyWorkflowStepTest {
    /**
     * The resources directory for the configuration package.
     */
    private static final String GALAXY_DIRECTORY = "src\\test\\resources\\nl\\vumc\\biomedbridges\\galaxy\\";

    /**
     * Test the GalaxyWorkflowStep constructor: check the simple properties.
     */
    @Test
    public void testGalaxyWorkflowStepConstructorSimpleProperties() throws ParseException, IOException {
        final String filePath = GALAXY_DIRECTORY + "TestWorkflow.ga";
        final String jsonContent = Joiner.on("\n").join(Files.readAllLines(Paths.get(filePath), Charsets.UTF_8));
        final JSONObject workflowJson = (JSONObject) new JSONParser().parse(jsonContent);
        final JSONObject stepsMapJson = (JSONObject) workflowJson.get("steps");
        final GalaxyWorkflowStep workflowStep = new GalaxyWorkflowStep((JSONObject) stepsMapJson.get("2"));
        Assert.assertEquals("", workflowStep.getAnnotation());
        Assert.assertEquals("Concatenate datasets", workflowStep.getName());
        Assert.assertEquals("cat1", workflowStep.getToolId());
        Assert.assertEquals("1.0.0", workflowStep.getToolVersion());
        Assert.assertEquals("tool", workflowStep.getType());
        Assert.assertEquals(2, workflowStep.getId().longValue());
    }

    /**
     * Test the GalaxyWorkflowStep constructor: check the complex properties.
     */
    @Test
    public void testGalaxyWorkflowStepConstructorComplexProperties() throws ParseException, IOException {
        final String filePath = GALAXY_DIRECTORY + "TestWorkflow.ga";
        final String jsonContent = Joiner.on("\n").join(Files.readAllLines(Paths.get(filePath), Charsets.UTF_8));
        final JSONObject workflowJson = (JSONObject) new JSONParser().parse(jsonContent);
        final JSONObject stepsMapJson = (JSONObject) workflowJson.get("steps");
        final GalaxyWorkflowStep workflowStep = new GalaxyWorkflowStep((JSONObject) stepsMapJson.get("2"));
        Assert.assertEquals(0, workflowStep.getInputs().size());
        System.out.println("workflowStep.getInputConnections(): " + workflowStep.getInputConnections());
        // todo: parse "input_connections" in GalaxyWorkflowStep constructor.
        //Assert.assertEquals("", workflowStep.getInputConnections());
    }
}
