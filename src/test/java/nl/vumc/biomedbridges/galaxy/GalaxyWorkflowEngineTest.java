/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.galaxy;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.GalaxyInstanceFactory;
import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.WorkflowsClient;
import com.github.jmchilton.blend4j.galaxy.beans.Dataset;
import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryDetails;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowOutputs;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

import nl.vumc.biomedbridges.galaxy.configuration.GalaxyConfiguration;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for the GalaxyWorkflowEngine class.
 *
 * todo: rewrite without PowerMock, since it interferes with JaCoCo that is determining code coverage.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
//@RunWith(PowerMockRunner.class)
//@PrepareForTest({GalaxyInstanceFactory.class, HistoryUtils.class, URL.class})
public class GalaxyWorkflowEngineTest {
    /**
     * Test the configure method with nonsense configuration data.
     */
    @Test
    public void testConfigureNonsense() {
        assertFalse(new GalaxyWorkflowEngine().configure("nonsense"));
    }

    /**
     * Test the configure method with configuration data that appears to be valid but is not.
     */
    @Test
    public void testConfigureInvalid() {
        final String configurationData = GalaxyConfiguration.PROPERTY_SEPARATOR
                                         + GalaxyConfiguration.KEY_VALUE_SEPARATOR
                                         + GalaxyConfiguration.GALAXY_INSTANCE_PROPERTY_KEY
                                         + GalaxyConfiguration.KEY_VALUE_SEPARATOR
                                         + GalaxyConfiguration.API_KEY_PROPERTY_KEY
                                         + GalaxyConfiguration.KEY_VALUE_SEPARATOR;
        assertFalse(new GalaxyWorkflowEngine().configure(configurationData));
    }

    /**
     * Test the configure method with valid configuration data.
     */
    @Test
    public void testConfigureValid() {
        final String configurationData = GalaxyConfiguration.GALAXY_INSTANCE_PROPERTY_KEY
                                         + GalaxyConfiguration.KEY_VALUE_SEPARATOR + "https://usegalaxy.org/"
                                         + GalaxyConfiguration.PROPERTY_SEPARATOR
                                         + GalaxyConfiguration.API_KEY_PROPERTY_KEY
                                         + GalaxyConfiguration.KEY_VALUE_SEPARATOR
                                         + "some-api-key"
                                         + GalaxyConfiguration.PROPERTY_SEPARATOR
                                         + GalaxyConfiguration.HISTORY_NAME_PROPERTY_KEY
                                         + GalaxyConfiguration.KEY_VALUE_SEPARATOR
                                         + "some-history-name";
        assertTrue(new GalaxyWorkflowEngine().configure(configurationData));
    }

    /**
     * Test the runWorkflow method.
     *
     * todo: complete this test.
     */
    @Ignore
    @Test
    public void testRunWorkflowV2() throws Exception {
        final GalaxyWorkflow galaxyWorkflow = Mockito.mock(GalaxyWorkflow.class);
        final String configuration = GalaxyConfiguration.buildConfiguration("...", "...", "...");
        final GalaxyWorkflowEngine galaxyWorkflowEngine = new GalaxyWorkflowEngine();
        galaxyWorkflowEngine.configure(configuration);
        //galaxyWorkflowEngine.runWorkflow(new GalaxyWorkflow("TestWorkflow"));
        galaxyWorkflowEngine.runWorkflow(galaxyWorkflow);
    }

    /**
     * Test the runWorkflow method.
     */
    @Ignore
    @Test
    public void testRunWorkflow() throws Exception {
        // todo: prepare some mocks for the Galaxy instance and the workflows client so we do not have to run an actual
        // todo: workflow for this test.
        PowerMockito.mockStatic(GalaxyInstanceFactory.class);
        PowerMockito.mockStatic(HistoryUtils.class);
        final GalaxyInstance galaxyMock = Mockito.mock(GalaxyInstance.class);
        final WorkflowsClient workflowsClientMock = Mockito.mock(WorkflowsClient.class);
        final HistoriesClient historiesClientMock = Mockito.mock(HistoriesClient.class);
        final History historyMock = new History();
        final HistoryDetails historyDetailsRunning = new HistoryDetails();
        final HistoryDetails historyDetailsOK = new HistoryDetails();
        final WorkflowOutputs workflowOutputsMock = Mockito.mock(WorkflowOutputs.class);
        final Dataset datasetMock = new Dataset();
        final URL url = PowerMockito.mock(URL.class);
        final HttpURLConnection urlConnection = Mockito.mock(HttpURLConnection.class);
        final InputStream inputStream = new ByteArrayInputStream("testDownloadDataset".getBytes());
        historyDetailsRunning.setState("running");
        historyDetailsOK.setState("ok");

        // todo: retrieving the workflow output file is done multiple times; this dummy file works only once.
        final File dummyFile = new File((String) getHiddenStaticField(GalaxyWorkflowEngine.class, "OUTPUT_FILE_PATH"));
        //System.out.println("dummyFile.getAbsolutePath(): " + dummyFile.getAbsolutePath());
        assertTrue("Create a dummy file.", dummyFile.createNewFile());

        PowerMockito.when(GalaxyInstanceFactory.get(Mockito.anyString(), Mockito.anyString())).thenReturn(galaxyMock);
        Mockito.when(galaxyMock.getWorkflowsClient()).thenReturn(workflowsClientMock);
        Mockito.when(galaxyMock.getHistoriesClient()).thenReturn(historiesClientMock);
        Mockito.when(historiesClientMock.create(Mockito.any(History.class))).thenReturn(historyMock);
        Mockito.when(historiesClientMock.showHistory(Mockito.anyString())).thenReturn(historyDetailsRunning);
        Mockito.when(historiesClientMock.showHistory(Mockito.anyString())).thenReturn(historyDetailsOK);
        Mockito.when(workflowsClientMock.runWorkflow(Mockito.any(WorkflowInputs.class))).thenReturn(workflowOutputsMock);
        PowerMockito.when(new HistoryUtils().downloadDataset(Mockito.eq(galaxyMock), Mockito.eq(historiesClientMock),
                                                             Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                                                             Mockito.anyBoolean(), Mockito.anyString())).thenReturn(true);
        Mockito.when(historiesClientMock.showDataset(Mockito.anyString(), Mockito.anyString())).thenReturn(datasetMock);
        Mockito.when(galaxyMock.getGalaxyUrl()).thenReturn("http://");
        PowerMockito.whenNew(URL.class).withArguments(Mockito.anyString()).thenReturn(url);
        Mockito.when(url.openConnection()).thenReturn(urlConnection);
        Mockito.when(urlConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
        Mockito.when(urlConnection.getInputStream()).thenReturn(inputStream);
        Mockito.when(workflowOutputsMock.getOutputIds()).thenReturn(Arrays.asList("one item"));

        final String configuration = GalaxyConfiguration.buildConfiguration("GALAXY_INSTANCE_URL", "apiKey", "HISTORY_NAME");
        final GalaxyWorkflowEngine galaxyWorkflowEngine = new GalaxyWorkflowEngine();
        galaxyWorkflowEngine.configure(configuration);
        galaxyWorkflowEngine.runWorkflow(new GalaxyWorkflow("TestWorkflow"));

        assertFalse(dummyFile.delete());
    }

    /**
     * Get a reference to a private/protected static field from a class for testing purposes.
     *
     * @param clazz     the class containing the static field.
     * @param fieldName the name of the field.
     * @return a reference to the private/protected field.
     * @throws IllegalAccessException if access is for some reason not allowed.
     * @throws NoSuchFieldException   if the field is not found.
     */
    private Object getHiddenStaticField(final Class clazz, final String fieldName)
            throws IllegalAccessException, NoSuchFieldException {
        Field field;
        try {
            field = clazz.getDeclaredField(fieldName);
        } catch (final NoSuchFieldException e) {
            // If the class does not have the field, try the super class.
            field = clazz.getSuperclass().getDeclaredField(fieldName);
        }
        field.setAccessible(true);
        return field.get(null);
    }
}
