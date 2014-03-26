/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.biomedbridges.v2.galaxy;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.beans.Dataset;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertTrue;

/**
 * Unit test for the HistoryUtils class.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({HistoryUtils.class, URL.class})
public class HistoryUtilsTest {
    /**
     * Test the downloadDataset method.
     */
    @Test
    public void testDownloadDataset() throws Exception {
        final HistoriesClient historiesClient = Mockito.mock(HistoriesClient.class);
        final String historyId = "123456";
        final String datasetId = "dataset-id";
        final Dataset dataset = new Dataset();
        final GalaxyInstance galaxyInstance = Mockito.mock(GalaxyInstance.class);
        final URL url = PowerMockito.mock(URL.class);
        final HttpURLConnection urlConnection = Mockito.mock(HttpURLConnection.class);
        final InputStream inputStream = new ByteArrayInputStream("testDownloadDataset".getBytes());
        final String filePath = System.getProperty("java.io.tmpdir") + "testDownloadDataset.txt";

        Mockito.when(historiesClient.showDataset(Mockito.eq(historyId), Mockito.eq(datasetId))).thenReturn(dataset);
        Mockito.when(galaxyInstance.getGalaxyUrl()).thenReturn("http://");
        PowerMockito.whenNew(URL.class).withArguments(Mockito.anyString()).thenReturn(url);
        Mockito.when(url.openConnection()).thenReturn(urlConnection);
        Mockito.when(urlConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
        Mockito.when(urlConnection.getInputStream()).thenReturn(inputStream);

        assertTrue(HistoryUtils.downloadDataset(galaxyInstance, historiesClient, historyId, datasetId, filePath,
                                                false, null));

        assertTrue(new File(filePath).exists());
        assertTrue(new File(filePath).delete());
    }
}
