/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.core;

/**
 * Factory interface for creating workflow engines based on workflow type.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public interface WorkflowEngineFactory {
    /**
     * Demonstration workflow (engine) type.
     */
    String DEMONSTRATION_TYPE = "demonstration";

    /**
     * Galaxy workflow (engine) type.
     */
    String GALAXY_TYPE = "galaxy";

    /**
     * Molgenis workflow (engine) type.
     */
    String MOLGENIS_TYPE = "molgenis";

    /**
     * Create a workflow engine based on the workflow (engine) type.
     *
     * @param workflowType the workflow (engine) type.
     * @return the new workflow engine (or null if the type was not recognized).
     */
    WorkflowEngine getWorkflowEngine(final String workflowType);

    /**
     * Create a workflow engine based on the workflow (engine) type.
     *
     * @param workflowType      the workflow (engine) type.
     * @param configurationData the configuration data.
     * @return the new workflow engine (or null if the type was not recognized).
     */
    WorkflowEngine getWorkflowEngine(final String workflowType, final Object configurationData);
}
