/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
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
    /**
     * The name of the output file.
     */
    private final String name;

    /**
     * The type of the output file.
     */
    private final String type;

    /**
     * Create a Galaxy step output object.
     *
     * @param outputJson the output json object.
     */
    public GalaxyStepOutput(final JSONObject outputJson) {
        this.name = outputJson.get("name").toString();
        this.type = outputJson.get("type").toString();
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
}
