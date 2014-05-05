/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
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
    private final String description;
    private final String name;

    public GalaxyStepInput(final JSONObject inputJson) {
        this.description = inputJson.get("description").toString();
        this.name = inputJson.get("name").toString();
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }
}
