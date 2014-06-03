/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.molgenis;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Unit tests for the MolgenisWorkflowEngine class.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class MolgenisWorkflowEngineTest {
    /**
     * Test the configure method.
     */
    @Test
    public void testConfigure() {
        assertTrue(new MolgenisWorkflowEngine().configure(null));
    }

    /**
     * Test the runWorkflow method.
     */
    @Test
    public void testRunWorkflow() {
        final MolgenisWorkflowEngine workflowEngine = new MolgenisWorkflowEngine();
        assertTrue(workflowEngine.runWorkflow(workflowEngine.getWorkflow(null)));
    }
}
