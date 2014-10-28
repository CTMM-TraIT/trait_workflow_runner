/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.core;

/**
 * Factory interface for creating workflows based on workflow type.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public interface WorkflowFactory {
    /**
     * Create a workflow based on the workflow type, configuration, and workflow name.
     *
     * @param workflowType      the workflow (engine) type.
     * @param configurationData the configuration data.
     * @param workflowName      the workflow name.
     * @return the new workflow engine (or null if the type was not recognized).
     */
    Workflow getWorkflow(final WorkflowType workflowType, final Object configurationData, final String workflowName);
}
