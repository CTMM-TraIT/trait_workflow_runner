package nl.vumc.biomedbridges.molgenis;

import nl.vumc.biomedbridges.core.Workflow;
import nl.vumc.biomedbridges.core.WorkflowEngine;

/**
 * The workflow engine implementation for Molgenis Compute.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class MolgenisWorkflowEngine implements WorkflowEngine {
    @Override
    public boolean configure(final String configurationData) {
        System.out.println("MolgenisWorkflowEngine.configure...");
        return true;
    }

    @Override
    public Workflow getWorkflow(final String workflowName) {
        return new MolgenisWorkflow(workflowName);
    }

    @Override
    public void runWorkflow(final Workflow workflow) {
        System.out.println("MolgenisWorkflowEngine.runWorkflow...");
    }
}
