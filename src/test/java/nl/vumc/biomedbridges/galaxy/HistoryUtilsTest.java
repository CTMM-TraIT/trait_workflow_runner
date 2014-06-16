/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.galaxy;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.beans.Dataset;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryContents;

import java.io.File;
import java.util.Arrays;

import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
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

    /**
     * Test the downloadFileFromUrl method.
     */
    @Test
    public void testDownloadFileFromUrl() throws Exception {
        final HistoryUtils historyUtils = new HistoryUtils();
        final String flagUrl = "http://www.biomedbridges.eu/sites/biomedbridges.eu/files/images/euflag.png";
        final String temporaryDirectory = System.getProperty("java.io.tmpdir");
        final String separatorIfNeeded = !temporaryDirectory.endsWith(File.separator) ? File.separator : "";
        final String filePath = temporaryDirectory + separatorIfNeeded + "testDownloadFileFromUrl.txt";

        assertTrue(historyUtils.downloadFileFromUrl(flagUrl, filePath));
        assertTrue(new File(filePath).exists());
        assertTrue(new File(filePath).delete());
    }

    /**
     * Test the getDatasetIdByName method.
     */
    @Test
    public void testGetDatasetIdByName() throws Exception {
        final HistoryUtils historyUtils = new HistoryUtils();
        final HistoriesClient historiesClientMock = Mockito.mock(HistoriesClient.class);
        final String historyId = "history-id";
        final String datasetId = "dataset-id";
        final String datasetName = "dataset-name";
        final HistoryContents historyContents = new HistoryContents();
        historyContents.setId(datasetId);
        historyContents.setName(datasetName);
        Mockito.when(historiesClientMock.showHistoryContents(historyId)).thenReturn(Arrays.asList(historyContents));

        assertEquals(datasetId, historyUtils.getDatasetIdByName(datasetName, historiesClientMock, historyId));
    }
}
