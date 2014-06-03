/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.galaxy.metadata;

import org.json.simple.JSONObject;

/**
 * The Galaxy workflow step position metadata (which is part of a GalaxyWorkflowStep object).
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 * @author <a href="mailto:y.hoogstrate@erasmusmc.nl">Youri Hoogstrate</a>
 */
public class GalaxyStepPosition {
    /**
     * The left coordinate of a Galaxy workflow step.
     */
    private final double left;

    /**
     * The top coordinate of a Galaxy workflow step.
     */
    private final double top;

    /**
     * Create a Galaxy step position object.
     *
     * @param positionJson the position json object.
     */
    public GalaxyStepPosition(final JSONObject positionJson) {
        this.left = Double.parseDouble(positionJson.get("left").toString());
        this.top = Double.parseDouble(positionJson.get("top").toString());
    }

    /**
     * Get the left coordinate.
     *
     * @return the left coordinate.
     */
    public double getLeft() {
        return left;
    }

    /**
     * Get the top coordinate.
     *
     * @return the top coordinate.
     */
    public double getTop() {
        return top;
    }
}
