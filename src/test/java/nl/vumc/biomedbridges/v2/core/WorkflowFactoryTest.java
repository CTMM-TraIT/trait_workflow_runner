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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Unit test for the WorkflowFactory class.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class WorkflowFactoryTest {
    /**
     * Test the getWorkflowEngine method.
     */
    @Test
    public void testGetWorkflowEngine() {
        checkEngineReturnType(DemonstrationWorkflowEngine.class, WorkflowFactory.DEMONSTRATION_TYPE);
        checkEngineReturnType(GalaxyWorkflowEngine.class, WorkflowFactory.GALAXY_TYPE);
        checkEngineReturnType(MolgenisWorkflowEngine.class, WorkflowFactory.MOLGENIS_TYPE);
        checkEngineReturnType(null, "unknown workflow (engine) type");
    }

    private void checkEngineReturnType(final Class<? extends WorkflowEngine> returnType, final String workflowType) {
        final WorkflowEngine workflowEngine = WorkflowFactory.getWorkflowEngine(workflowType);
        if (returnType == null)
            assertNull(workflowEngine);
        else
            assertEquals(returnType, workflowEngine.getClass());
    }

    /**
     * Test the getWorkflow method.
     */
    @Test
    public void testGetWorkflow() {
        checkWorkflowReturnType(DemonstrationWorkflow.class, WorkflowFactory.DEMONSTRATION_TYPE);
        checkWorkflowReturnType(GalaxyWorkflow.class, WorkflowFactory.GALAXY_TYPE);
        checkWorkflowReturnType(MolgenisWorkflow.class, WorkflowFactory.MOLGENIS_TYPE);
        checkWorkflowReturnType(null, "unknown workflow type");
    }

    private void checkWorkflowReturnType(final Class<? extends Workflow> returnType, final String workflowType) {
        final Workflow workflow = WorkflowFactory.getWorkflow(workflowType, "unused workflow name");
        if (returnType == null)
            assertNull(workflow);
        else
            assertEquals(returnType, workflow.getClass());
    }
}
