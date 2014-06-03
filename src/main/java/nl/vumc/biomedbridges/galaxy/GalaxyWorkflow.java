/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.galaxy;

import com.github.jmchilton.blend4j.galaxy.WorkflowsClient;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import nl.vumc.biomedbridges.core.BaseWorkflow;
import nl.vumc.biomedbridges.core.Workflow;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The workflow implementation for Galaxy.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 * @author <a href="mailto:y.hoogstrate@erasmusmc.nl">Youri Hoogstrate</a>
 */
public class GalaxyWorkflow extends BaseWorkflow implements Workflow {
    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(GalaxyWorkflow.class);

    /**
     * The inputs definition from the Galaxy workflow JSON file.
     */
    private List<Map<String, String>> inputs;

    /**
     * The outputs definition from the Galaxy workflow JSON file.
     */
    private List<Map<String, String>> outputs;

    /**
     * Construct a Galaxy workflow.
     *
     * @param name the name of the workflow.
     */
    protected GalaxyWorkflow(final String name) {
        super(name);
        parseJson();
    }

    /**
     * Ensure the workflow is present on the Galaxy server. If it is not found, it will be created.
     *
     * @param workflowsClient the workflows client used to interact with the Galaxy workflows on the server.
     * @return whether the workflow was already present or successfully created on the Galaxy server.
     */
    public boolean ensureWorkflowIsOnServer(final WorkflowsClient workflowsClient) {
        boolean isOnServer = isWorkflowOnServer(workflowsClient);
        if (!isOnServer) {
            workflowsClient.importWorkflow(getJsonContent());
            isOnServer = isWorkflowOnServer(workflowsClient);
        }
        return isOnServer;
    }

    /**
     * Check whether the workflow is present on the Galaxy server.
     *
     * @param workflowsClient the workflows client used to interact with the Galaxy workflows on the server.
     * @return whether the workflow is present on the Galaxy server.
     */
    private boolean isWorkflowOnServer(final WorkflowsClient workflowsClient) {
        boolean found = false;
        // CHECKSTYLE_OFF: IllegalCatchCheck
        try {
            for (final com.github.jmchilton.blend4j.galaxy.beans.Workflow blend4jWorkflow : workflowsClient.getWorkflows())
                if (blend4jWorkflow.getName().equals(getName())
                    || blend4jWorkflow.getName().equals(getName() + " (imported from API)")) {
                    found = true;
                    break;
                }
        } catch (final RuntimeException e) {
            // todo: could blend4j catch the com.sun.jersey.api.client.ClientHandlerException and throw a known one?
            logger.error("Error retrieving the available workflows from the Galaxy server.");
        }
        // CHECKSTYLE_ON: IllegalCatchCheck
        return found;
    }

    /**
     * Give the filename of the Galaxy workflow description.
     *
     * @return the GA file's filename
     */
    private String getJsonFilename() {
        return getName() + ".ga";
    }

    /**
     * Get the content of the GA-file.
     *
     * todo: use an absolute file path instead of the classpath.
     *
     * @return the json design of the workflow.
     */
    private String getJsonContent() {
        try {
            final URL resourceUrl = GalaxyWorkflow.class.getResource(getJsonFilename());
            return resourceUrl != null ? Resources.asCharSource(resourceUrl, Charsets.UTF_8).read() : null;
        } catch (final IOException e) {
            logger.error("Exception while retrieving json design in workflow file {}.", getJsonFilename(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Parses the JSON / GA-file of a Galaxy workflow.
     *
     * // todo: Use the GalaxyWorkflowMetadata class instead.
     */
    public void parseJson() {
        try {
            inputs = new ArrayList<>();
            outputs = new ArrayList<>();
            final String jsonContent = getJsonContent();
            if (jsonContent != null) {
                final JSONObject workflowJson = (JSONObject) new JSONParser().parse(jsonContent);
                final JSONObject stepsMapJson = (JSONObject) workflowJson.get("steps");
                logger.info("This workflow contains " + stepsMapJson.size() + " step"
                            + (stepsMapJson.size() != 1 ? "s" : "") + ":");

                // Sort the steps to have a well defined order.
                final SortedMap<Integer, JSONObject> sortedStepsMap = new TreeMap<>();
                for (final Object stepObject : stepsMapJson.entrySet())
                    if (stepObject instanceof Map.Entry) {
                        final Map.Entry stepEntry = (Map.Entry) stepObject;
                        final int stepId = Integer.parseInt((String) stepEntry.getKey());
                        sortedStepsMap.put(stepId, (JSONObject) stepEntry.getValue());
                    }

                for (final JSONObject stepJson : sortedStepsMap.values()) {
                    addJsonInputs((JSONArray) stepJson.get("inputs"));
                    addJsonOutputs((JSONArray) stepJson.get("outputs"));
                }
            }
        } catch (final ParseException e) {
            logger.error("Exception while parsing json design in workflow file {}.", getJsonFilename(), e);
        }

        //http://www.tutorialspoint.com/json/json_java_example.htm
    }

    /**
     * Parse an "inputs" section of the JSON file.
     *
     * @param jsonInputs the json array with the inputs.
     */
    public void addJsonInputs(final JSONArray jsonInputs) {
        inputs.addAll(createListOfMaps(jsonInputs));
        logger.trace("inputs: " + inputs);
    }

    /**
     * Get the inputs definition from the Galaxy workflow JSON file.
     *
     * @return the inputs definition.
     */
    public List<Map<String, String>> getInputs() {
        return inputs;
    }

    /**
     * Parse an "outputs" section of the JSON file.
     *
     * @param jsonOutputs the json array with the outputs.
     */
    public void addJsonOutputs(final JSONArray jsonOutputs) {
        outputs.addAll(createListOfMaps(jsonOutputs));
        logger.trace("outputs: " + outputs);
    }

    /**
     * Get the outputs definition from the Galaxy workflow JSON file.
     *
     * @return the outputs definition.
     */
    public List<Map<String, String>> getOutputs() {
        return outputs;
    }

    /**
     * Create a list of maps from a json array.
     *
     * @param jsonArray the json array.
     * @return the list of maps.
     */
    private List<Map<String, String>> createListOfMaps(final JSONArray jsonArray) {
        final List<Map<String, String>> listOfMaps = new ArrayList<>();
        for (final Object object : jsonArray) {
            final JSONObject jsonObject = (JSONObject) object;
            //logger.trace("jsonObject: " + jsonObject);
            final Map<String, String> propertyMap = new HashMap<>();
            for (final Object entry : jsonObject.entrySet())
                if (entry instanceof Map.Entry) {
                    final Map.Entry mapEntry = (Map.Entry) entry;
                    propertyMap.put((String) mapEntry.getKey(), (String) mapEntry.getValue());
                }
            listOfMaps.add(propertyMap);
        }
        return listOfMaps;
    }
}
