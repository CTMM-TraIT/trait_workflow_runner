/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.galaxy.metadata;

import nl.vumc.biomedbridges.utilities.JsonUtilities;

import org.json.simple.JSONObject;

/**
 * The Galaxy workflow step input connection metadata.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 * @author <a href="mailto:y.hoogstrate@erasmusmc.nl">Youri Hoogstrate</a>
 */
public class GalaxyStepInputConnection {
    /**
     * The id.
     */
    private final Long id;

    /**
     * The output name.
     */
    private final String outputName;

    /**
     * Create a Galaxy workflow step input connection from an input connection json object.
     *
     * @param inputConnectionJson the json step object that contains the data for this step.
     */
    public GalaxyStepInputConnection(final JSONObject inputConnectionJson) {
        this.id = JsonUtilities.getJsonLong(inputConnectionJson, "id");
        this.outputName = JsonUtilities.getJsonString(inputConnectionJson, "output_name");
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
     * Get the output name.
     *
     * @return the output name.
     */
    public String getOutputName() {
        return outputName;
    }
}
