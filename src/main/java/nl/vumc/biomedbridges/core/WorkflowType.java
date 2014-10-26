/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.core;

/**
 * This enumerated type lists the supported workflow (engine) types.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public enum WorkflowType {
    /**
     * Galaxy workflow (engine) type.
     */
    GALAXY,

    /**
     * Demonstration workflow (engine) type.
     */
    DEMONSTRATION,

    /**
     * Molgenis workflow (engine) type.
     */
    MOLGENIS,

    /**
     * Unknown workflow (engine) type.
     */
    UNKNOWN
}
