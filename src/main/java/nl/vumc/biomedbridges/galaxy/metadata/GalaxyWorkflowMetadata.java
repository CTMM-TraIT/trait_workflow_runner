/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
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

/**
 * The Galaxy workflow metadata (which is read from a .ga json file).
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 * @author <a href="mailto:y.hoogstrate@erasmusmc.nl">Youri Hoogstrate</a>
 */
public class GalaxyWorkflowMetadata {
    private final boolean aGalaxyWorkflow;
    private final String annotation;
    private final String formatVersion;
    private final String name;
    private final List<GalaxyWorkflowStep> steps;

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

    public boolean isGalaxyWorkflow() {
        return aGalaxyWorkflow;
    }

    public String getAnnotation() {
        return annotation;
    }

    public String getFormatVersion() {
        return formatVersion;
    }

    public String getName() {
        return name;
    }

    public List<GalaxyWorkflowStep> getSteps() {
        return steps;
    }

    public Set<String> getToolIds() {
        final Set<String> toolIds = new HashSet<>();
        for (final GalaxyWorkflowStep workflowStep : steps)
            if (workflowStep.getToolId() != null)
                toolIds.add(workflowStep.getToolId());
        return toolIds;
    }
}
