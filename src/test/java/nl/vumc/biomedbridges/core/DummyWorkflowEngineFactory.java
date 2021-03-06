/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.core;

import nl.vumc.biomedbridges.galaxy.HistoryUtils;

/**
 * Factory class usable for testing purposes for creating workflow engines based on workflow type. Using dependency
 * injection, the TestGuiceModule class binds the WorkflowEngineFactory interface to this class.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class DummyWorkflowEngineFactory implements WorkflowEngineFactory {
    /**
     * The workflow engine.
     */
    private final DummyWorkflowEngine dummyWorkflowEngine = new DummyWorkflowEngine();

    @Override
    public WorkflowEngine getWorkflowEngine(final WorkflowType workflowType, final HistoryUtils historyUtils) {
        return dummyWorkflowEngine;
    }

    @Override
    public WorkflowEngine getWorkflowEngine(final WorkflowType workflowType, final Object configurationData,
                                            final HistoryUtils historyUtils) {
        return dummyWorkflowEngine;
    }
}
