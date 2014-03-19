/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.biomedbridges.v2.demonstration;

import nl.vumc.biomedbridges.v2.core.DefaultWorkflow;
import nl.vumc.biomedbridges.v2.core.Workflow;

/**
 * This is a very simple implementation of the Workflow interface.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class DemonstrationWorkflow extends DefaultWorkflow implements Workflow {
    public DemonstrationWorkflow(final String name) {
        super(name);
    }
}
