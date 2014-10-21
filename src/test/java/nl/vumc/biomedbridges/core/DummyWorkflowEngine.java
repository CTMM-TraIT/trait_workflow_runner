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
    private Workflow dummyWorkflow;

    @Override
    public Workflow getWorkflow(final String workflowName) {
        if (dummyWorkflow == null)
            dummyWorkflow = new BaseWorkflow(workflowName != null ? workflowName : "test workflow");
        return dummyWorkflow;
    }

    @Override
    public boolean runWorkflow(final Workflow workflow) throws InterruptedException, IOException {
        if (Constants.WORKFLOW_RNA_SEQ_DGE.equals(workflow.getName()))
            for (int outputIndex = 0; outputIndex < 7; outputIndex++)
                workflow.addOutput("dummy-output-" + (outputIndex + 1), "dummy");
        return true;
    }
}
