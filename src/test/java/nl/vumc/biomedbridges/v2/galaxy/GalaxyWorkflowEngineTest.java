/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.biomedbridges.v2.galaxy;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Unit test for the GalaxyWorkflowEngine class.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class GalaxyWorkflowEngineTest {
    /**
     * Test the runWorkflow method.
     */
    @Ignore
    @Test
    public void testRunWorkflow() throws Exception {
        // todo: Prepare some mocks for the Galaxy instance and the workflows client so we do not have to run an actual
        // todo: workflow for this test.
        final GalaxyWorkflowEngine galaxyWorkflowEngine = new GalaxyWorkflowEngine();
        galaxyWorkflowEngine.runWorkflow(new GalaxyWorkflow("TestWorkflow"));
    }
}
