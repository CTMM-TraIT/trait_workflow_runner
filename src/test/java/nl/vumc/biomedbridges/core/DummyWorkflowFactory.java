/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.core;

/**
 * Factory class usable for testing purposes for creating workflow engines based on workflow type. Using dependency
 * injection, the TestGuiceModule class binds the WorkflowEngineFactory interface to this class.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class DummyWorkflowFactory implements WorkflowFactory {
    /**
     * The dummy workflow.
     */
    private Workflow dummyWorkflow;

    @Override
    public Workflow getWorkflow(final WorkflowType workflowType, final Object configurationData, final String workflowName) {
        return getWorkflow(workflowName);
    }

    /**
     * Get a dummy workflow.
     *
     * @param workflowName the workflow name.
     * @return the dummy workflow.
     */
    private Workflow getWorkflow(final String workflowName) {
        if (dummyWorkflow == null)
            dummyWorkflow = new DummyWorkflow(workflowName != null ? workflowName : "test workflow");
        return dummyWorkflow;
    }
}
