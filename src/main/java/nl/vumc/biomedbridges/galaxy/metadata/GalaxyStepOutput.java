/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.biomedbridges.galaxy.metadata;

import org.json.simple.JSONObject;

/**
 * The Galaxy workflow step output metadata (which is part of a GalaxyWorkflowStep object).
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 * @author <a href="mailto:y.hoogstrate@erasmusmc.nl">Youri Hoogstrate</a>
 */
public class GalaxyStepOutput {
    private final String name;
    private final String type;

    public GalaxyStepOutput(final JSONObject inputJson) {
        this.name = inputJson.get("name").toString();
        this.type = inputJson.get("type").toString();
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
