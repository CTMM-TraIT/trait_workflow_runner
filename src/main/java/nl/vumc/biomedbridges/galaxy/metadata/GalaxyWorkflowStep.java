/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.biomedbridges.galaxy.metadata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Galaxy workflow step metadata (which is part of a GalaxyWorkflowMetadata object).
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 * @author <a href="mailto:y.hoogstrate@erasmusmc.nl">Youri Hoogstrate</a>
 */
public class GalaxyWorkflowStep {
    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(GalaxyWorkflowStep.class);

    private final Long id;
    private final String name;
    private final String type;
    private final String toolId;
    private final String toolVersion;
    private final String annotation;
    private final GalaxyStepPosition position;
    private final Map<Object, Object> inputConnections;
    private final List<GalaxyStepInput> inputs;
    private final List<GalaxyStepOutput> outputs;
    private final Map<Object, Object> toolErrors;
    private final Map<String, Object> toolState;
    private final List<Object> userOutputs;  // Appears to be unused.

    private GalaxyToolMetadata toolMetadata;

    public GalaxyWorkflowStep(final JSONObject stepJson) {
        this.id = getJsonLong(stepJson, "id");
        this.name = getJsonString(stepJson, "name");
        this.type = getJsonString(stepJson, "type");
        this.toolId = getJsonString(stepJson, "tool_id");
        this.toolVersion = getJsonString(stepJson, "tool_version");
        this.annotation = getJsonString(stepJson, "annotation");
        this.position = new GalaxyStepPosition((JSONObject) stepJson.get("position"));
        this.inputConnections = new HashMap<>();
        this.toolErrors = new HashMap<>();
        // Initialize inputs.
        this.inputs = new ArrayList<>();
        final JSONArray inputsArray = (JSONArray) stepJson.get("inputs");
        for (final Object input : inputsArray)
            this.inputs.add(new GalaxyStepInput((JSONObject) input));
        // Initialize outputs.
        this.outputs = new ArrayList<>();
        final JSONArray outputsArray = (JSONArray) stepJson.get("outputs");
        for (final Object output : outputsArray)
            this.outputs.add(new GalaxyStepOutput((JSONObject) output));
        // Initialize toolState.
        this.toolState = new HashMap<>();
        try {
            final Object toolStateObject = stepJson.get("tool_state");
            if (toolStateObject != null) {
                final JSONObject toolStateJson = (JSONObject) new JSONParser().parse(toolStateObject.toString());
                for (final Object parameterObject : toolStateJson.entrySet()) {
                    final Map.Entry parameterEntry = (Map.Entry) parameterObject;
                    final Object toolStateValue = getToolStateValue(parameterEntry.getValue());
                    logger.trace(parameterEntry.getKey() + " -> " + toolStateValue
                                 + (toolStateValue != null ? " (" + toolStateValue.getClass().getName() + ")" : ""));
                    this.toolState.put((String) parameterEntry.getKey(), toolStateValue);
                }
            }
        } catch (final ParseException e) {
            e.printStackTrace();
        }
        // Initialize userOutputs.
        this.userOutputs = new ArrayList<>();
    }

    private Object getToolStateValue(final Object initialValue) {
        Object result = null;
        if (initialValue != null) {
            final String initialString = initialValue.toString();
            if (initialString.startsWith("\"") && initialString.endsWith("\"")) {
                final String cleanString = initialString.substring(1, initialString.length() - 1);
                if (cleanString.equals("True") || cleanString.equals("False"))
                    result = cleanString.equals("True");
                else if (cleanString.matches("[-+]?\\d+(\\.\\d+)?"))
                    result = Long.parseLong(cleanString);
                else
                    result = cleanString;
            } else if (!"null".equals(initialString)) {
                if (initialString.matches("[-+]?\\d+(\\.\\d+)?"))
                    result = Long.parseLong(initialString);
                else
                    result = initialString;
            }
        }
        return result;
    }

    private String getJsonString(final JSONObject jsonObject, final String key) {
        final Object objectValue = jsonObject.get(key);
        return objectValue != null ? objectValue.toString() : null;
    }

    private Long getJsonLong(final JSONObject jsonObject, final String key) {
        final String stringValue = getJsonString(jsonObject, key);
        return stringValue != null ? Long.parseLong(stringValue) : null;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getToolId() {
        return toolId;
    }

    public String getToolVersion() {
        return toolVersion;
    }

    public String getAnnotation() {
        return annotation;
    }

    public GalaxyStepPosition getPosition() {
        return position;
    }

    public Map<Object, Object> getInputConnections() {
        return inputConnections;
    }

    public List<GalaxyStepOutput> getOutputs() {
        return outputs;
    }

    public List<GalaxyStepInput> getInputs() {
        return inputs;
    }

    public Map<Object, Object> getToolErrors() {
        return toolErrors;
    }

    public Map<String, Object> getToolState() {
        return toolState;
    }

    public List<Object> getUserOutputs() {
        return userOutputs;
    }

    /**
     * Add a tool metadata object to this workflow step.
     *
     * @param toolsMetadata the tool metadata objects.
     */
    public void addToolsMetadata(final List<GalaxyToolMetadata> toolsMetadata) {
        for (final GalaxyToolMetadata availableToolMetadata : toolsMetadata)
            if (toolId.equals(availableToolMetadata.getId()) && toolVersion.equals(availableToolMetadata.getVersion())) {
                toolMetadata = availableToolMetadata;
                break;
            }
    }

    public GalaxyToolMetadata getToolMetadata() {
        return toolMetadata;
    }
}
