package nl.vumc.biomedbridges.v2.molgenis;

import nl.vumc.biomedbridges.v2.core.Workflow;
import nl.vumc.biomedbridges.v2.core.WorkflowEngine;

/**
 * Created with IntelliJ IDEA.
 * User: Freek
 * Date: 11-3-14
 * Time: 12:03
 */
public class MolgenisWorkflowEngine implements WorkflowEngine {
    @Override
    public void runWorkflow(final Workflow workflow) {
        System.out.println("MolgenisWorkflowEngine.runWorkflow...");
    }
}
