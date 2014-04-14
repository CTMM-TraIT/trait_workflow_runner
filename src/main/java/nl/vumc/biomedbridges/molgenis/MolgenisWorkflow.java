package nl.vumc.biomedbridges.molgenis;

import nl.vumc.biomedbridges.core.DefaultWorkflow;
import nl.vumc.biomedbridges.core.Workflow;

/**
 * The workflow implementation for Molgenis Compute.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class MolgenisWorkflow extends DefaultWorkflow implements Workflow {
    /**
     * Create a Molgenis Compute workflow.
     *
     * @param name the workflow name.
     */
    protected MolgenisWorkflow(final String name) {
        super(name);
    }
}
