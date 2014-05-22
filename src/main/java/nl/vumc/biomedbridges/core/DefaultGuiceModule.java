package nl.vumc.biomedbridges.core;

import com.google.inject.AbstractModule;

public class DefaultGuiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(WorkflowEngineFactory.class).to(DefaultWorkflowEngineFactory.class);
    }
}
