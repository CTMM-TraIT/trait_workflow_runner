/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.biomedbridges.v1;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.beans.Dataset;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.net.ssl.HttpsURLConnection;
import java.net.URL;

import static org.junit.Assert.assertTrue;

/**
 * This class contains a unit test for the HistoryUtils class.
 *
 * todo: complete the unit test.
 * http://stackoverflow.com/questions/13364406/mockito-mock-a-constructor-with-parameter
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(URL.class)
public class HistoryUtilsTest {
    /**
     * Test the downloadDataset method.
     */
    @Ignore
    @Test
    public void testDownloadDataset() throws Exception {
        final GalaxyInstance galaxyInstance = Mockito.mock(GalaxyInstance.class);
        final HistoriesClient historiesClient = Mockito.mock(HistoriesClient.class);
        final URL url = PowerMockito.mock(URL.class);
        final HttpsURLConnection urlConnection = Mockito.mock(HttpsURLConnection.class);
        final String historyId = "123456";
        final String datasetId = "dataset-id";
        final Dataset dataset = new Dataset();
        Mockito.when(historiesClient.showDataset(Mockito.eq(historyId), Mockito.eq(datasetId))).thenReturn(dataset);
        Mockito.when(galaxyInstance.getGalaxyUrl()).thenReturn("https://");
        // http://stackoverflow.com/questions/13364406/mockito-mock-a-constructor-with-parameter
        // https://code.google.com/p/powermock/wiki/MockFinal
        PowerMockito.whenNew(URL.class).withArguments(Mockito.anyString()).thenReturn(url);
        Mockito.when(url.openConnection()).thenReturn(urlConnection);

        assertTrue(HistoryUtils.downloadDataset(galaxyInstance, historiesClient, historyId, datasetId, "file-path",
                                                true, null));

//        final Map<String, WorkflowInput> expectedWorkflowInputs = ImmutableMap.of(label, inputValue);
//        assertEquals(expectedWorkflowInputs, workflowInputs.getInputs());
    }
}
