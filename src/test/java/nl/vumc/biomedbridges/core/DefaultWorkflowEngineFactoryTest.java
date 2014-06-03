/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.core;

import nl.vumc.biomedbridges.demonstration.DemonstrationWorkflow;
import nl.vumc.biomedbridges.demonstration.DemonstrationWorkflowEngine;
import nl.vumc.biomedbridges.galaxy.GalaxyWorkflow;
import nl.vumc.biomedbridges.galaxy.GalaxyWorkflowEngine;
import nl.vumc.biomedbridges.molgenis.MolgenisWorkflow;
import nl.vumc.biomedbridges.molgenis.MolgenisWorkflowEngine;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Unit test for the DefaultWorkflowEngineFactory class.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class DefaultWorkflowEngineFactoryTest {
    /**
     * Test the getWorkflowEngine method.
     */
    @Test
    public void testGetWorkflowEngine() {
        checkEngineReturnType(DemonstrationWorkflowEngine.class, WorkflowEngineFactory.DEMONSTRATION_TYPE);
        checkEngineReturnType(GalaxyWorkflowEngine.class, WorkflowEngineFactory.GALAXY_TYPE);
        checkEngineReturnType(MolgenisWorkflowEngine.class, WorkflowEngineFactory.MOLGENIS_TYPE);
        checkEngineReturnType(null, "unknown workflow (engine) type");
    }

    private void checkEngineReturnType(final Class<? extends WorkflowEngine> returnType, final String workflowType) {
        final WorkflowEngine workflowEngine = new DefaultWorkflowEngineFactory().getWorkflowEngine(workflowType);
        if (returnType == null)
            assertNull(workflowEngine);
        else
            assertEquals(returnType, workflowEngine.getClass());
    }

    /**
     * Test the getWorkflow method.
     *
     * todo: move to the workflow engine unit tests?
     */
    @Test
    public void testGetWorkflow() {
        checkWorkflowReturnType(DemonstrationWorkflow.class, WorkflowEngineFactory.DEMONSTRATION_TYPE);
        checkWorkflowReturnType(GalaxyWorkflow.class, WorkflowEngineFactory.GALAXY_TYPE);
        checkWorkflowReturnType(MolgenisWorkflow.class, WorkflowEngineFactory.MOLGENIS_TYPE);
    }

    private void checkWorkflowReturnType(final Class<? extends Workflow> workflowClass, final String workflowType) {
        final WorkflowEngine workflowEngine = new DefaultWorkflowEngineFactory().getWorkflowEngine(workflowType);
        final Workflow workflow = workflowEngine.getWorkflow("unused workflow name");
        if (workflowClass == null)
            assertNull(workflow);
        else
            assertEquals(workflowClass, workflow.getClass());
    }
}
