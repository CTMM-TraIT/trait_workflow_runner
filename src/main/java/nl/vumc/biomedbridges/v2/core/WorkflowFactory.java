/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.biomedbridges.v2.core;

import nl.vumc.biomedbridges.v2.demonstration.DemonstrationWorkflow;
import nl.vumc.biomedbridges.v2.demonstration.DemonstrationWorkflowEngine;
import nl.vumc.biomedbridges.v2.galaxy.GalaxyWorkflow;
import nl.vumc.biomedbridges.v2.galaxy.GalaxyWorkflowEngine;
import nl.vumc.biomedbridges.v2.molgenis.MolgenisWorkflow;
import nl.vumc.biomedbridges.v2.molgenis.MolgenisWorkflowEngine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory class for creating workflow engines and workflows based on workflow type.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class WorkflowFactory {
    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(WorkflowRunnerVersion2.class);

    /**
     * Demonstration workflow (engine) type.
     */
    public static final String DEMONSTRATION_TYPE = "demonstration";

    /**
     * Galaxy workflow (engine) type.
     */
    public static final String GALAXY_TYPE = "galaxy";

    /**
     * Molgenis workflow (engine) type.
     */
    public static final String MOLGENIS_TYPE = "molgenis";

    /**
     * Create a workflow engine based on the workflow (engine) type.
     *
     * @param workflowType the workflow (engine) type.
     * @return the new workflow engine (or null if the type was not recognized).
     */
    public static WorkflowEngine getWorkflowEngine(final String workflowType) {
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
                logger.error("Unexpected workflow type: {}.", workflowType);
                workflowEngine = null;
                break;
        }
        return workflowEngine;
    }

    /**
     * Create a workflow based on the workflow type.
     *
     * @param workflowType the workflow type.
     * @return the new workflow (or null if the type was not recognized).
     */
    public static Workflow getWorkflow(final String workflowType, final String workflowName) {
        final Workflow workflow;
        switch (workflowType) {
            case DEMONSTRATION_TYPE:
                workflow = new DemonstrationWorkflow(workflowName);
                break;
            case GALAXY_TYPE:
                workflow = new GalaxyWorkflow(workflowName);
                break;
            case MOLGENIS_TYPE:
                workflow = new MolgenisWorkflow(workflowName);
                break;
            default:
                logger.error("Unexpected workflow type: {}.", workflowType);
                workflow = null;
                break;
        }
        return workflow;
    }
}
