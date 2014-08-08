/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.core;

import nl.vumc.biomedbridges.demonstration.DemonstrationWorkflow;
import nl.vumc.biomedbridges.galaxy.GalaxyWorkflow;
import nl.vumc.biomedbridges.galaxy.HistoryUtils;
import nl.vumc.biomedbridges.galaxy.configuration.GalaxyConfiguration;
import nl.vumc.biomedbridges.molgenis.MolgenisWorkflow;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * This class contains a unit test for the getWorkflow method of the WorkflowEngine interface.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class WorkflowEngineTest {
    /**
     * Test the getWorkflow method. This method is specified by the WorkflowEngine interface and implemented by the
     */
    @Test
    public void testGetWorkflow() {
        final GalaxyConfiguration galaxyConfiguration = new GalaxyConfiguration();
        galaxyConfiguration.buildConfiguration(Constants.GALAXY_INSTANCE_URL, galaxyConfiguration.getGalaxyApiKey(),
                                               "history name");

        checkWorkflowReturnType(DemonstrationWorkflow.class, WorkflowEngineFactory.DEMONSTRATION_TYPE, null);
        checkWorkflowReturnType(GalaxyWorkflow.class, WorkflowEngineFactory.GALAXY_TYPE, galaxyConfiguration);
        checkWorkflowReturnType(MolgenisWorkflow.class, WorkflowEngineFactory.MOLGENIS_TYPE, null);
    }

    private void checkWorkflowReturnType(final Class<? extends Workflow> workflowClass, final String workflowType,
                                         final Object configurationData) {
        final WorkflowEngineFactory workflowEngineFactory = new DefaultWorkflowEngineFactory();
        final WorkflowEngine workflowEngine = workflowEngineFactory.getWorkflowEngine(workflowType, configurationData,
                                                                                      new HistoryUtils());
        final Workflow workflow = workflowEngine.getWorkflow("unused workflow name");
        if (workflowClass == null)
            assertNull(workflow);
        else {
            assertNotNull(workflow);
            assertEquals(workflowClass, workflow.getClass());
        }
    }
}
