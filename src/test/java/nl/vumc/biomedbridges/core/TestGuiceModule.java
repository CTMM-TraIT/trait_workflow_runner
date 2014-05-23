package nl.vumc.biomedbridges.core;

import com.google.inject.AbstractModule;

/**
 * Created with IntelliJ IDEA.
 * User: Freek
 * Date: 23-5-2014
 * Time: 23:30
 */
public class TestGuiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(WorkflowEngineFactory.class).to(DummyWorkflowEngineFactory.class);
    }
}
