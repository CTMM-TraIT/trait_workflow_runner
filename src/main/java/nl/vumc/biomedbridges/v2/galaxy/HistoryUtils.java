/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.biomedbridges.v2.galaxy;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.beans.Dataset;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryContents;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple utility method for working with Galaxy histories.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class HistoryUtils {
    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(HistoryUtils.class);

    /**
     * The size of the buffer for downloading datasets from Galaxy.
     */
    private static final int DOWNLOAD_BUFFER_SIZE = 8 * 1024;

    /**
     * Hidden constructor. Only the static methods of this class are meant to be used.
     */
    private HistoryUtils() {
    }

    /**
     * Download a dataset from Galaxy.
     *
     * @param galaxyInstance  the Galaxy server to retrieve the dataset from.
     * @param historiesClient the client for accessing Galaxy histories.
     * @param historyId       the ID of the history that contains the dataset.
     * @param datasetId       the ID of the dataset.
     * @param filePath        the (base) file path to write the dataset to.
     * @param useDatasetName  whether the dataset name should be appended to the file path.
     * @param dataType        the explicit data type of the dataset (or null to use the data type from the dataset).
     * @return whether the download was successful.
     */
    public static boolean downloadDataset(final GalaxyInstance galaxyInstance, final HistoriesClient historiesClient,
                                          final String historyId, final String datasetId, final String filePath,
                                          final boolean useDatasetName, final String dataType) {
        final Dataset dataset = historiesClient.showDataset(historyId, datasetId);
        final String toExt = (dataType != null) ? dataType : dataset.getDataType();
        final String url = galaxyInstance.getGalaxyUrl() + "/datasets/" + dataset.getId() + "/display/?to_ext=" + toExt;
        final String fullFilePath = filePath + (useDatasetName ? File.separator + dataset.getName() : "");
        return downloadFileFromUrl(url, fullFilePath);
    }

    /**
     * Download a dataset from a Galaxy URL to a local file.
     *
     * @param url          the Galaxy url to download the dataset from.
     * @param fullFilePath the full file path to download the dataset to.
     * @return whether the download was successful.
     */
    private static boolean downloadFileFromUrl(final String url, final String fullFilePath) {
        boolean success = true;
        OutputStream outputStream = null;
        try {
            final HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            final int responseCode = connection.getResponseCode();
            final InputStream inputStream = (responseCode == HttpURLConnection.HTTP_OK)
                                            ? connection.getInputStream()
                                            : connection.getErrorStream();
            outputStream = new FileOutputStream(fullFilePath);
            final byte[] buffer = new byte[DOWNLOAD_BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (final IOException e) {
            logger.error("HistoryUtils.downloadFileFromUrl: error downloading or writing dataset file.", e);
            success = false;
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (final IOException e) {
                    logger.error("HistoryUtils.downloadFileFromUrl: error closing dataset file.", e);
                    success = false;
                }
            }
        }
        return success;
    }

    /**
     * Retrieve the ID of a dataset by its name.
     *
     * @param datasetName     the dataset name.
     * @param historiesClient the client for accessing Galaxy histories.
     * @param historyId       the ID of the history that contains the dataset.
     * @return the dataset ID.
     */
    public static String getDatasetIdByName(final String datasetName, final HistoriesClient historiesClient,
                                            final String historyId) {
        String datasetId = null;
        for (HistoryContents historyDataset : historiesClient.showHistoryContents(historyId))
            if (historyDataset.getName().equals(datasetName)) {
                datasetId = historyDataset.getId();
                break;
            }
        return datasetId;
    }
}
