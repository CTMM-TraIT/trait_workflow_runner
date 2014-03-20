package nl.vumc.biomedbridges.v2.molgenis;

import nl.vumc.biomedbridges.v2.core.DefaultWorkflow;
import nl.vumc.biomedbridges.v2.core.Workflow;

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
    public MolgenisWorkflow(final String name) {
        super(name);
    }
}
