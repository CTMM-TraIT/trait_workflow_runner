/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.galaxy;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.beans.Dataset;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryContents;
import com.sun.jersey.api.client.WebResource;

import java.io.File;
import java.util.Arrays;

import nl.vumc.biomedbridges.core.FileUtils;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

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
        final GalaxyInstance galaxyInstanceMock = Mockito.mock(GalaxyInstance.class);
        final WebResource webResourceMock = Mockito.mock(WebResource.class);
        final String filePath = System.getProperty("java.io.tmpdir") + "testDownloadDataset.txt";

        Mockito.when(historiesClient.showDataset(Mockito.eq(historyId), Mockito.eq(datasetId))).thenReturn(dataset);
        Mockito.when(galaxyInstanceMock.getGalaxyUrl()).thenReturn("http://");
        Mockito.when(galaxyInstanceMock.getWebResource()).thenReturn(webResourceMock);
        Mockito.when(webResourceMock.path(Mockito.eq("histories"))).thenReturn(webResourceMock);
        Mockito.when(webResourceMock.path(Mockito.eq(historyId))).thenReturn(webResourceMock);
        Mockito.when(webResourceMock.path(Mockito.eq("contents"))).thenReturn(webResourceMock);
        Mockito.when(webResourceMock.path(Mockito.eq(datasetId))).thenReturn(webResourceMock);
        Mockito.when(webResourceMock.path(Mockito.eq("display"))).thenReturn(webResourceMock);
        Mockito.when(webResourceMock.get(Mockito.eq(File.class))).thenReturn(new File(filePath));

        final Answer getFileAnswer = new Answer() {
            @Override
            public Object answer(final InvocationOnMock invocationOnMock) throws Throwable {
                FileUtils.createFile(filePath, "testDownloadDataset");
                return new File(filePath);
            }
        };

        Mockito.when(webResourceMock.get(Mockito.eq(File.class))).thenAnswer(getFileAnswer);

        assertTrue(historyUtilsSpy.downloadDataset(galaxyInstanceMock, historiesClient, historyId, datasetId, filePath));
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
