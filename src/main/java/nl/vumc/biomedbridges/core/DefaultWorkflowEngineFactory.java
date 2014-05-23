/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.biomedbridges.core;

import nl.vumc.biomedbridges.demonstration.DemonstrationWorkflowEngine;
import nl.vumc.biomedbridges.galaxy.GalaxyWorkflowEngine;
import nl.vumc.biomedbridges.molgenis.MolgenisWorkflowEngine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory class for creating workflow engines based on workflow type.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class DefaultWorkflowEngineFactory implements WorkflowEngineFactory {
    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(DefaultWorkflowEngineFactory.class);

    /**
     * Message for an unexpected workflow type.
     */
    private static final String UNEXPECTED_TYPE_MESSAGE = "Unexpected workflow type: {}.";

    /**
     * Hidden constructor. Instances of this class are created by Guice.
     */
    protected DefaultWorkflowEngineFactory() {
    }

    /**
     * Create a workflow engine based on the workflow (engine) type.
     *
     * @param workflowType the workflow (engine) type.
     * @return the new workflow engine (or null if the type was not recognized).
     */
    @Override
    public WorkflowEngine getWorkflowEngine(final String workflowType) {
        return getWorkflowEngine(workflowType, null);
    }

    /**
     * Create a workflow engine based on the workflow (engine) type.
     *
     * @param workflowType      the workflow (engine) type.
     * @param configurationData the configuration data.
     * @return the new workflow engine (or null if the type was not recognized).
     */
    @Override
    public WorkflowEngine getWorkflowEngine(final String workflowType, final String configurationData) {
        final WorkflowEngine workflowEngine;
        switch (workflowType) {
            case DEMONSTRATION_TYPE:
                workflowEngine = new DemonstrationWorkflowEngine();
                break;
            case GALAXY_TYPE:
                workflowEngine = new GalaxyWorkflowEngine();
                break;
            case MOLGENIS_TYPE:
                workflowEngine = new MolgenisWorkflowEngine();
                break;
            default:
                logger.error(UNEXPECTED_TYPE_MESSAGE, workflowType);
                workflowEngine = null;
                break;
        }
        if (workflowEngine != null && configurationData != null)
            workflowEngine.configure(configurationData);
        return workflowEngine;
    }
}
