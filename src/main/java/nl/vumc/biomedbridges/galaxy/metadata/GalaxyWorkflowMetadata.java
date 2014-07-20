/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.galaxy.metadata;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Galaxy workflow metadata (which is read from a .ga file in json format).
 *
 * todo [hackathon]: Use the API to get the tool configuration details in json format.
 *     - example: https://usegalaxy.org/api/tools/random_lines1?io_details=true
 *     - see: https://bitbucket.org/galaxy/galaxy-dist/src/5273e0bf9ae54599f1dadbce9d943dcac828d0f0
 *            /lib/galaxy/webapps/galaxy/api/tools.py?at=default
 *            * search for: "GET /api/tools/{tool_id}"
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 * @author <a href="mailto:y.hoogstrate@erasmusmc.nl">Youri Hoogstrate</a>
 */
public class GalaxyWorkflowMetadata {
    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(GalaxyWorkflowMetadata.class);

    /**
     * Whether this is a Galaxy workflow: should always be true.
     */
    private final boolean aGalaxyWorkflow;

    /**
     * The optional annotation describing this workflow.
     */
    private final String annotation;

    /**
     * The format version which currently (in 2014) is equal to "0.1".
     */
    private final String formatVersion;

    /**
     * The name.
     */
    private final String name;

    /**
     * The steps that form this workflow. A tool can be called during a step.
     */
    private final List<GalaxyWorkflowStep> steps;

    /**
     * Create a Galaxy workflow metadata object from a json object.
     *
     * @param workflowJson the workflow json object.
     */
    public GalaxyWorkflowMetadata(final JSONObject workflowJson) {
        this.aGalaxyWorkflow = "true".equals(workflowJson.get("a_galaxy_workflow"));
        this.annotation = workflowJson.get("annotation").toString();
        this.formatVersion = workflowJson.get("format-version").toString();
        this.name = workflowJson.get("name").toString();
        this.steps = new ArrayList<>();

        final JSONObject stepsMapJson = (JSONObject) workflowJson.get("steps");

        // Sort the steps to have a well defined order.
        final SortedMap<Integer, JSONObject> sortedStepsMap = new TreeMap<>();
        for (final Object stepObject : stepsMapJson.entrySet())
            if (stepObject instanceof Map.Entry) {
                final Map.Entry stepEntry = (Map.Entry) stepObject;
                final int stepId = Integer.parseInt((String) stepEntry.getKey());
                sortedStepsMap.put(stepId, (JSONObject) stepEntry.getValue());
            }

        for (final JSONObject stepJson : sortedStepsMap.values())
            steps.add(new GalaxyWorkflowStep(stepJson));
    }

    /**
     * Whether this is a Galaxy workflow; this method should always return true.
     *
     * @return true (whether this is a Galaxy workflow).
     */
    public boolean isGalaxyWorkflow() {
        return aGalaxyWorkflow;
    }

    /**
     * Get the optional annotation describing the workflow.
     *
     * @return the optional annotation describing the workflow.
     */
    public String getAnnotation() {
        return annotation;
    }

    /**
     * Get the format version which currently (in 2014) is equal to "0.1".
     *
     * @return the format version which currently (in 2014) is equal to "0.1".
     */
    public String getFormatVersion() {
        return formatVersion;
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
     * Get the steps that form this workflow. A tool can be called during a step.
     *
     * @return the steps that form this workflow.
     */
    public List<GalaxyWorkflowStep> getSteps() {
        return steps;
    }

    /**
     * Get the tools that are referenced by this workflow.
     *
     * @return the tools that are referenced by this workflow.
     */
    public Set<GalaxyToolReference> getToolReferences() {
        final Set<GalaxyToolReference> toolIds = new HashSet<>();
        for (final GalaxyWorkflowStep workflowStep : steps)
            if (workflowStep.getToolId() != null)
                toolIds.add(new GalaxyToolReference(workflowStep.getToolId(), workflowStep.getToolVersion()));
        return toolIds;
    }

    /**
     * Add tool metadata objects to the workflow steps.
     *
     * @param toolsMetadata the tool metadata objects.
     */
    public void addToolsMetadata(final List<GalaxyToolMetadata> toolsMetadata) {
        for (final GalaxyWorkflowStep workflowStep : steps)
            workflowStep.addToolsMetadata(toolsMetadata);
    }

    /**
     * Get the tool parameters for this workflow.
     *
     * @return the tool parameters for this workflow.
     */
    public List<GalaxyToolParameterMetadata> getParameters() {
        final List<GalaxyToolParameterMetadata> parameters = new ArrayList<>();
        final List<String> parameterNames = new ArrayList<>();
        boolean duplicateParameterName = false;
        for (final GalaxyWorkflowStep workflowStep : steps)
            if (workflowStep.getToolMetadata() != null) {
                final List<GalaxyToolParameterMetadata> toolParameters = workflowStep.getToolMetadata().getParameters();
                for (final GalaxyToolParameterMetadata toolParameter : toolParameters)
                    if (!parameterNames.contains(toolParameter.getName()))
                        parameterNames.add(toolParameter.getName());
                    else
                        duplicateParameterName = true;
                parameters.addAll(toolParameters);
            }
        if (duplicateParameterName)
            logger.trace("At least one parameter name is used more than once...");
        else
            logger.trace("No duplicate parameter names were found.");
        // (If there is at least one duplicate, we need to make some names more specific...)
        // todo: to make the user interface clear, it's probably best to support groups of parameters (steps).
        return parameters;
    }
}
