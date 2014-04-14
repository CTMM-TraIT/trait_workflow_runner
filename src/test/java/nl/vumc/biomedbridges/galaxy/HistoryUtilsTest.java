/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.biomedbridges.galaxy;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.beans.Dataset;

import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertTrue;

/**
 * Unit test for the HistoryUtils class.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class HistoryUtilsTest {
    /**
     * Test the downloadDataset method.
     */
    @Test
    public void testDownloadDataset() throws Exception {
        final HistoryUtils historyUtilsSpy = Mockito.spy(new HistoryUtils());
        final HistoriesClient historiesClient = Mockito.mock(HistoriesClient.class);
        final String historyId = "123456";
        final String datasetId = "dataset-id";
        final Dataset dataset = new Dataset();
        final GalaxyInstance galaxyInstance = Mockito.mock(GalaxyInstance.class);
        final String filePath = System.getProperty("java.io.tmpdir") + "testDownloadDataset.txt";

        Mockito.when(historiesClient.showDataset(Mockito.eq(historyId), Mockito.eq(datasetId))).thenReturn(dataset);
        Mockito.when(galaxyInstance.getGalaxyUrl()).thenReturn("http://");
        Mockito.when(historyUtilsSpy.downloadFileFromUrl(Mockito.anyString(), Mockito.anyString())).thenReturn(true);

        assertTrue(historyUtilsSpy.downloadDataset(galaxyInstance, historiesClient, historyId, datasetId, filePath,
                                                   false, null));
    }
}
