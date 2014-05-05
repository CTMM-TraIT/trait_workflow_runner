/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.biomedbridges.galaxy.metadata;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * The Galaxy workflow step metadata (which is part of a GalaxyWorkflowMetadata object).
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 * @author <a href="mailto:y.hoogstrate@erasmusmc.nl">Youri Hoogstrate</a>
 */
public class GalaxyWorkflowStep {
    private final String annotation;
    private final long id;
    //            "input_connections": {},
    private final List<GalaxyStepInput> inputs;
    //            "inputs": [
    //                {
    //                    "description": "",
    //                    "name": "input"
    //                }
    //            ],
    private final String name;
    //            "outputs": [],
    //            "position": {
    //                "left": 193.5,
    //                "top": 270.5
    //            },
    //            "tool_errors": null,
    //            "tool_id": null,
    //            "tool_state": "{\"name\": \"input\"}",
    //            "tool_version": null,
    private final String type;
    //            "user_outputs": []


    public GalaxyWorkflowStep(final JSONObject stepJson) {
        this.annotation = stepJson.get("annotation").toString();
        this.id = Long.parseLong(stepJson.get("id").toString());
        this.name = stepJson.get("name").toString();
        this.type = stepJson.get("type").toString();
        this.inputs = new ArrayList<>();

        final JSONArray inputsArray = (JSONArray) stepJson.get("inputs");
        for (final Object input : inputsArray)
            this.inputs.add(new GalaxyStepInput((JSONObject) input));
    }

    public String getAnnotation() {
        return annotation;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
