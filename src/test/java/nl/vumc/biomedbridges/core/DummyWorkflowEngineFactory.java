package nl.vumc.biomedbridges.core;

public class DummyWorkflowEngineFactory implements WorkflowEngineFactory {
    /**
     *
     */
    private final DummyWorkflowEngine dummyWorkflowEngine = new DummyWorkflowEngine();

    @Override
    public WorkflowEngine getWorkflowEngine(final String workflowType) {
        return dummyWorkflowEngine;
    }

    @Override
    public WorkflowEngine getWorkflowEngine(final String workflowType, final String configurationData) {
        return dummyWorkflowEngine;
    }
}
