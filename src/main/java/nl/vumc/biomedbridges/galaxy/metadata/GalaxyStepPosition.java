/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
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
    private final double left;
    private final double top;

    public GalaxyStepPosition(final JSONObject positionJson) {
        this.left = Double.parseDouble(positionJson.get("left").toString());
        this.top = Double.parseDouble(positionJson.get("top").toString());
    }

    public double getLeft() {
        return left;
    }

    public double getTop() {
        return top;
    }
}
