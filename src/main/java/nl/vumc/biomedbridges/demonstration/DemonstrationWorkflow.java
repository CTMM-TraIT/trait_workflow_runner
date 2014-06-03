/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.demonstration;

import nl.vumc.biomedbridges.core.BaseWorkflow;
import nl.vumc.biomedbridges.core.Workflow;

/**
 * This is a very simple implementation of the Workflow interface.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class DemonstrationWorkflow extends BaseWorkflow implements Workflow {
    /**
     * Create a demonstration workflow.
     *
     * @param name the workflow name.
     */
    protected DemonstrationWorkflow(final String name) {
        super(name);
    }
}
