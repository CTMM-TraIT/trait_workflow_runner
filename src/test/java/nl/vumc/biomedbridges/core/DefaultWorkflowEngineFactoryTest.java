/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.core;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.beans.History;

import nl.vumc.biomedbridges.demonstration.DemonstrationWorkflowEngine;
import nl.vumc.biomedbridges.galaxy.GalaxyWorkflowEngine;
import nl.vumc.biomedbridges.galaxy.HistoryUtils;
import nl.vumc.biomedbridges.galaxy.configuration.GalaxyConfiguration;
import nl.vumc.biomedbridges.molgenis.MolgenisWorkflowEngine;

import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
        final GalaxyConfiguration galaxyConfigurationMock = Mockito.mock(GalaxyConfiguration.class);
        final GalaxyInstance galaxyInstanceMock = Mockito.mock(GalaxyInstance.class);
        final HistoriesClient historiesClientMock = Mockito.mock(HistoriesClient.class);
        final History historyMock = Mockito.mock(History.class);

        Mockito.when(galaxyConfigurationMock.determineGalaxyInstance(Mockito.anyString())).thenReturn(galaxyInstanceMock);
        Mockito.when(galaxyInstanceMock.getHistoriesClient()).thenReturn(historiesClientMock);
        Mockito.when(historiesClientMock.create(Mockito.any(History.class))).thenReturn(historyMock);

        checkEngineReturnType(WorkflowType.DEMONSTRATION, null, DemonstrationWorkflowEngine.class);
        checkEngineReturnType(WorkflowType.GALAXY, galaxyConfigurationMock, GalaxyWorkflowEngine.class);
        checkEngineReturnType(WorkflowType.GALAXY, null, null);
        checkEngineReturnType(WorkflowType.MOLGENIS, null, MolgenisWorkflowEngine.class);
        checkEngineReturnType(WorkflowType.UNKNOWN, null, null);
    }

    /**
     * Check whether a specific workflow engine can be created and has the expected return type.
     *
     * @param workflowType the workflow (engine) type to create.
     * @param configurationData the configuration data (if necessary) or null.
     * @param returnType the expected return type.
     */
    private void checkEngineReturnType(final WorkflowType workflowType, final Object configurationData,
                                       final Class<? extends WorkflowEngine> returnType) {
        final WorkflowEngineFactory workflowEngineFactory = new DefaultWorkflowEngineFactory();
        final WorkflowEngine workflowEngine = workflowEngineFactory.getWorkflowEngine(workflowType, configurationData,
                                                                                      new HistoryUtils());
        if (returnType == null)
            assertNull(workflowEngine);
        else {
            assertNotNull(workflowEngine);
            assertEquals(returnType, workflowEngine.getClass());
        }
    }
}
