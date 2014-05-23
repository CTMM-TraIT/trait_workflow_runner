package nl.vumc.biomedbridges.core;

import java.io.IOException;

public class DummyWorkflowEngine implements WorkflowEngine {
    /**
     *
     */
    private final DummyWorkflow dummyWorkflow = new DummyWorkflow("dummy workflow");

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
