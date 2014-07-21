/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.core;

import java.io.IOException;

/**
 * The workflow engine implementation for testing purposes.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class DummyWorkflowEngine implements WorkflowEngine {
    /**
     * The workflow.
     */
    private final Workflow dummyWorkflow = new BaseWorkflow("test workflow");

    @Override
    public boolean configure() {
        return true;
    }

    @Override
    public boolean configure(final String configurationData) {
        return true;
    }

    @Override
    public Workflow getWorkflow(final String workflowName) {
        return dummyWorkflow;
    }

    @Override
    public boolean runWorkflow(final Workflow workflow) throws InterruptedException, IOException {
        return true;
    }
}
