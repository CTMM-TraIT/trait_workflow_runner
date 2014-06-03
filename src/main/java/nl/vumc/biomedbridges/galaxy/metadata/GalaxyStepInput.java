/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.galaxy.metadata;

import org.json.simple.JSONObject;

/**
 * The Galaxy workflow step input metadata (which is part of a GalaxyWorkflowStep object).
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 * @author <a href="mailto:y.hoogstrate@erasmusmc.nl">Youri Hoogstrate</a>
 */
public class GalaxyStepInput {
    /**
     * The name of the input file.
     */
    private final String name;

    /**
     * The description of the input file.
     */
    private final String description;

    /**
     * Create a Galaxy step input object.
     *
     * @param inputJson the input json object.
     */
    public GalaxyStepInput(final JSONObject inputJson) {
        this.name = inputJson.get("name").toString();
        this.description = inputJson.get("description").toString();
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
     * Get the description.
     *
     * @return the description.
     */
    public String getDescription() {
        return description;
    }
}
