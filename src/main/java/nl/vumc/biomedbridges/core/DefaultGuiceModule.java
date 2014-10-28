/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.core;

import com.google.inject.AbstractModule;

/**
 * Default module to use for dependency injection using the Guice framework.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class DefaultGuiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(WorkflowEngineFactory.class).to(DefaultWorkflowEngineFactory.class);
        bind(WorkflowFactory.class).to(DefaultWorkflowFactory.class);
    }
}
