/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.galaxy.metadata;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import nl.vumc.biomedbridges.utilities.JsonUtilities;

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

    /**
     * The tool state type string (enclosed in double quotes).
     */
    private static final int TOOL_STATE_TYPE_STRING = 1;

    /**
     * The tool state type map (enclosed in accolades).
     */
    private static final int TOOL_STATE_TYPE_MAP = 2;

    /**
     * The tool state type boolean (either "true" or "false", ignoring case).
     */
    private static final int TOOL_STATE_TYPE_BOOLEAN = 3;

    /**
     * The tool state type other.
     */
    private static final int TOOL_STATE_TYPE_OTHER = 4;

    /**
     * The double quote (").
     */
    private static final String DOUBLE_QUOTE = "\"";

    /**
     * The id.
     */
    private final Long id;

    /**
     * The name.
     */
    private final String name;

    /**
     * The type.
     */
    private final String type;

    /**
     * The tool id.
     */
    private final String toolId;

    /**
     * The tool version.
     */
    private final String toolVersion;

    /**
     * The optional annotation describing this step.
     */
    private final String annotation;

    /**
     * The position of this step in the graphical representation of the workflow.
     */
    private final GalaxyStepPosition position;

    /**
     * The input connections (to the previous step?).
     */
    private final Map<String, GalaxyStepInputConnection> inputConnections;

    /**
     * The input files.
     */
    private final List<GalaxyStepInput> inputs;

    /**
     * The output files.
     */
    private final List<GalaxyStepOutput> outputs;

    /**
     * The tool errors (if any).
     */
    private final Map<Object, Object> toolErrors;

    /**
     * The tool state with inputs and parameters.
     */
    private final Map<String, Object> toolState;

    /**
     * The user outputs (appears to be unused).
     */
    private final List<Object> userOutputs;

    /**
     * The tool metadata (if there is a tool related to this step).
     */
    private GalaxyToolMetadata toolMetadata;

    /**
     * Create a Galaxy workflow step from a step json object.
     *
     * @param stepJson the json step object that contains the data for this step.
     */
    public GalaxyWorkflowStep(final JSONObject stepJson) {
        this.id = JsonUtilities.getJsonLong(stepJson, "id");
        this.name = JsonUtilities.getJsonString(stepJson, "name");
        this.type = JsonUtilities.getJsonString(stepJson, "type");
        this.toolId = JsonUtilities.getJsonString(stepJson, "tool_id");
        this.toolVersion = JsonUtilities.getJsonString(stepJson, "tool_version");
        this.annotation = JsonUtilities.getJsonString(stepJson, "annotation");
        this.position = new GalaxyStepPosition((JSONObject) stepJson.get("position"));
        this.toolErrors = new HashMap<>();
        // Initialize inputConnections.
        this.inputConnections = new HashMap<>();
        final JSONObject inputConnectionsMap = (JSONObject) stepJson.get("input_connections");
        for (final Object inputConnectionObject : inputConnectionsMap.entrySet()) {
            final Map.Entry inputConnectionEntry = (Map.Entry) inputConnectionObject;
            final JSONObject inputConnectionJson = (JSONObject) inputConnectionEntry.getValue();
            final GalaxyStepInputConnection inputConnection = new GalaxyStepInputConnection(inputConnectionJson);
            this.inputConnections.put((String) inputConnectionEntry.getKey(), inputConnection);
        }
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
        fillToolState(stepJson.get("tool_state"));
        // Initialize userOutputs.
        this.userOutputs = new ArrayList<>();
    }

    /**
     * Fill the tool state map based on the values in the tool state json object.
     *
     * @param toolStateObject the tool state json object.
     */
    private void fillToolState(final Object toolStateObject) {
        try {
            if (toolStateObject != null) {
                final JSONObject toolStateJson = (JSONObject) new JSONParser().parse(toolStateObject.toString());
                for (final Object parameterObject : toolStateJson.entrySet()) {
                    final Map.Entry parameterEntry = (Map.Entry) parameterObject;
                    final Object parameterValue = parameterEntry.getValue();
                    final Object toolStateValue = parameterValue != null ? getToolStateValue(parameterValue) : null;
                    logger.trace(parameterEntry.getKey() + " -> " + toolStateValue
                                 + (toolStateValue != null ? " (" + toolStateValue.getClass().getName() + ")" : ""));
                    this.toolState.put((String) parameterEntry.getKey(), toolStateValue);
                }
            }
        } catch (final ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get an individual tool state value. Strip superfluous double quotes and test for booleans & longs.
     *
     * @param initialValue the initial value from the json file, which should be non null.
     * @return the transformed value.
     */
    private Object getToolStateValue(@Nonnull final Object initialValue) {
        Preconditions.checkNotNull(initialValue);
        Object result = null;
        final String initialString = initialValue.toString();
        final String trueString = "True";
        final int toolStateType = getToolStateType(initialString);
        if (toolStateType == TOOL_STATE_TYPE_STRING)
            result = getToolStateValue(initialString.substring(1, initialString.length() - 1));
        else if (toolStateType == TOOL_STATE_TYPE_MAP)
            result = readToolStateMap(initialString.substring(2, initialString.length() - 2));
        else if (toolStateType == TOOL_STATE_TYPE_BOOLEAN)
            result = trueString.equalsIgnoreCase(initialString);
        else if (initialString.matches("[-+]?\\d+\\.\\d+"))
            result = Double.parseDouble(initialString);
        else if (initialString.matches("[-+]?\\d+"))
            result = Long.parseLong(initialString);
        else if (!"null".equals(initialString))
            result = initialString;
        return result;
    }

    /**
     * Determine the tool state type for a json value.
     *
     * @param jsonValue the json value.
     * @return the tool state type.
     */
    private int getToolStateType(final String jsonValue) {
        final int toolStateType;
        final String doubleQuote = DOUBLE_QUOTE;
        final List<String> booleanStrings = Arrays.asList("true", "false");
        if (jsonValue.startsWith(doubleQuote) && jsonValue.endsWith(doubleQuote))
            toolStateType = TOOL_STATE_TYPE_STRING;
        else if (jsonValue.startsWith("{\"") && jsonValue.endsWith("\"}"))
            toolStateType = TOOL_STATE_TYPE_MAP;
        else if (booleanStrings.contains(jsonValue.toLowerCase()))
            toolStateType = TOOL_STATE_TYPE_BOOLEAN;
        else
            toolStateType = TOOL_STATE_TYPE_OTHER;
        return toolStateType;
    }

    /**
     * Read the key value pairs from the string representation of a map in the tool state.
     *
     * @param mapString the string representation of a map in the tool state.
     * @return the map representation of a map in the tool state.
     */
    private Map<String, Object> readToolStateMap(final String mapString) {
        final Map<String, Object> toolStateMap = new HashMap<>();
        final String entrySeparator = ", \"";
        final String keyValueSeparator = "\": ";
        for (final String mapEntryString : mapString.split(entrySeparator)) {
            final int separatorIndex = mapEntryString.indexOf(keyValueSeparator);
            final String key = mapEntryString.substring(mapEntryString.startsWith(DOUBLE_QUOTE) ? 1 : 0, separatorIndex);
            final int beginValueIndex = separatorIndex + keyValueSeparator.length();
            final int quoteOffset = mapEntryString.charAt(beginValueIndex) == '"' ? 1 : 0;
            final int endValueIndex = mapEntryString.length() - (mapEntryString.endsWith(DOUBLE_QUOTE) ? 1 : 0);
            final Object value = getToolStateValue(mapEntryString.substring(beginValueIndex + quoteOffset, endValueIndex));
            toolStateMap.put(key, value);
        }
        return toolStateMap;
    }

    /**
     * Get the id.
     *
     * @return the id.
     */
    public Long getId() {
        return id;
    }

    /**
     * Get the name.
     *
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the type.
     *
     * @return the type.
     */
    public String getType() {
        return type;
    }

    /**
     * Get the tool id.
     *
     * @return the tool id.
     */
    public String getToolId() {
        return toolId;
    }

    /**
     * Get the tool version.
     *
     * @return the tool version.
     */
    public String getToolVersion() {
        return toolVersion;
    }

    /**
     * Get the optional annotation describing this step.
     *
     * @return the optional annotation describing this step.
     */
    public String getAnnotation() {
        return annotation;
    }

    /**
     * Get the position of this step in the graphical representation of the workflow.
     *
     * @return the position of this step in the graphical representation of the workflow.
     */
    public GalaxyStepPosition getPosition() {
        return position;
    }

    /**
     * Get the input connections (to the previous step?).
     *
     * @return the input connections (to the previous step?).
     */
    public Map<String, GalaxyStepInputConnection> getInputConnections() {
        return inputConnections;
    }

    /**
     * Get the input files.
     *
     * @return the input files.
     */
    public List<GalaxyStepInput> getInputs() {
        return inputs;
    }

    /**
     * Get the output files.
     *
     * @return the output files.
     */
    public List<GalaxyStepOutput> getOutputs() {
        return outputs;
    }

    /**
     * Get the tool errors (if any).
     *
     * @return the tool errors (if any).
     */
    public Map<Object, Object> getToolErrors() {
        return toolErrors;
    }

    /**
     * Get the tool state with inputs and parameters.
     *
     * @return the tool state with inputs and parameters.
     */
    public Map<String, Object> getToolState() {
        return toolState;
    }

    /**
     * Get the user outputs (appears to be unused).
     *
     * @return the user outputs (appears to be unused).
     */
    public List<Object> getUserOutputs() {
        return userOutputs;
    }

    /**
     * Add a tool metadata object to this workflow step.
     *
     * @param toolsMetadata the tool metadata objects.
     */
    public void addToolsMetadata(final List<GalaxyToolMetadata> toolsMetadata) {
        for (final GalaxyToolMetadata availableToolMetadata : toolsMetadata) {
            final String availableToolVersion = availableToolMetadata.getVersion();
            final boolean availableToolVersionEmpty = "".equals(availableToolVersion);
            if (toolId != null && toolId.equals(availableToolMetadata.getId())
                && (availableToolVersionEmpty || (toolVersion != null && toolVersion.equals(availableToolVersion)))) {
                toolMetadata = availableToolMetadata;
                break;
            }
        }
    }

    /**
     * Get the tool metadata (if there is a tool related to this step).
     *
     * @return the tool metadata (if there is a tool related to this step) or null.
     */
    public GalaxyToolMetadata getToolMetadata() {
        return toolMetadata;
    }
}
