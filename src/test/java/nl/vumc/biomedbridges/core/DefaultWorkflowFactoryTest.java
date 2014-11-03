/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.core;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.beans.History;

import nl.vumc.biomedbridges.demonstration.DemonstrationWorkflow;
import nl.vumc.biomedbridges.galaxy.GalaxyWorkflow;
import nl.vumc.biomedbridges.galaxy.configuration.GalaxyConfiguration;
import nl.vumc.biomedbridges.molgenis.MolgenisWorkflow;

import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Unit test for the DefaultWorkflowFactory class.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class DefaultWorkflowFactoryTest {
    /**
     * Test the getWorkflow method.
     */
    @Test
    public void testGetWorkflow() {
        final GalaxyConfiguration galaxyConfigurationMock = Mockito.mock(GalaxyConfiguration.class);
        final GalaxyInstance galaxyInstanceMock = Mockito.mock(GalaxyInstance.class);
        final HistoriesClient historiesClientMock = Mockito.mock(HistoriesClient.class);
        final History historyMock = Mockito.mock(History.class);

        Mockito.when(galaxyConfigurationMock.determineGalaxyInstance(Mockito.anyString())).thenReturn(galaxyInstanceMock);
        Mockito.when(galaxyInstanceMock.getHistoriesClient()).thenReturn(historiesClientMock);
        Mockito.when(historiesClientMock.create(Mockito.any(History.class))).thenReturn(historyMock);

        checkWorkflowReturnType(WorkflowType.DEMONSTRATION, null, DemonstrationWorkflow.class);
        checkWorkflowReturnType(WorkflowType.GALAXY, galaxyConfigurationMock, GalaxyWorkflow.class);
        checkWorkflowReturnType(WorkflowType.GALAXY, null, null);
        checkWorkflowReturnType(WorkflowType.MOLGENIS, null, MolgenisWorkflow.class);
        checkWorkflowReturnType(WorkflowType.UNKNOWN, null, null);
    }

    /**
     * Check whether a specific workflow can be created and has the expected return type.
     *
     * @param workflowType      the workflow type to create.
     * @param configurationData the configuration data (if necessary) or null.
     * @param returnType        the expected return type.
     */
    private void checkWorkflowReturnType(final WorkflowType workflowType, final Object configurationData,
                                         final Class<? extends Workflow> returnType) {
        final WorkflowFactory workflowFactory = new DefaultWorkflowFactory();
        final String workflowName = "test workflow name";
        final Workflow workflow = workflowFactory.getWorkflow(workflowType, configurationData, workflowName);
        if (returnType == null)
            assertNull(workflow);
        else {
            assertNotNull(workflow);
            assertEquals(returnType, workflow.getClass());
            assertEquals(workflowName, workflow.getName());
        }
    }
}
