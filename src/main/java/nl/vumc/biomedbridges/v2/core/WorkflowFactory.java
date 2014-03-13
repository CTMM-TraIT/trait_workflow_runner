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
 * Created with IntelliJ IDEA.
 * User: Freek
 * Date: 12-3-14
 * Time: 9:47
 */
public class WorkflowFactory {
    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(WorkflowRunnerVersion2.class);

    public static final String DEMONSTRATION_TYPE = "demonstration";
    public static final String GALAXY_TYPE = "galaxy";
    public static final String MOLGENIS_TYPE = "molgenis";

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
