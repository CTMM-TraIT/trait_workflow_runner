/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.core;

import java.io.IOException;

/**
 * Dummy workflow for testing purposes.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class DummyWorkflow extends BaseWorkflow {
    /**
     * Create a dummy workflow.
     *
     * @param name the workflow name.
     */
    public DummyWorkflow(final String name) {
        super(name);
    }

    @Override
    public boolean run() throws IOException, InterruptedException {
        if (Constants.WORKFLOW_RNA_SEQ_DGE.equals(getName()))
            for (int outputIndex = 0; outputIndex < 7; outputIndex++)
                addOutput("dummy-output-" + (outputIndex + 1), "dummy");
        return true;
    }
}
