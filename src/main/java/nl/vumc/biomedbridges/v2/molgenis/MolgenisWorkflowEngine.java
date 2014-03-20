package nl.vumc.biomedbridges.v2.molgenis;

import nl.vumc.biomedbridges.v2.core.Workflow;
import nl.vumc.biomedbridges.v2.core.WorkflowEngine;

/**
 * The workflow engine implementation for Molgenis Compute.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class MolgenisWorkflowEngine implements WorkflowEngine {
    @Override
    public void runWorkflow(final Workflow workflow) {
        System.out.println("MolgenisWorkflowEngine.runWorkflow...");
    }
}
