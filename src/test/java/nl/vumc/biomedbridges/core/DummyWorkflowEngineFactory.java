package nl.vumc.biomedbridges.core;

public class DummyWorkflowEngineFactory implements WorkflowEngineFactory {
    /**
     * The workflow engine.
     */
    private final DummyWorkflowEngine dummyWorkflowEngine = new DummyWorkflowEngine();

    @Override
    public WorkflowEngine getWorkflowEngine(final String workflowType) {
        return dummyWorkflowEngine;
    }

    @Override
    public WorkflowEngine getWorkflowEngine(final String workflowType, final Object configurationData) {
        return dummyWorkflowEngine;
    }
}
