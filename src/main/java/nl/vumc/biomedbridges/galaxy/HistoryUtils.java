/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.galaxy;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.beans.Dataset;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryContents;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple utility methods for working with Galaxy histories.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class HistoryUtils {
    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(HistoryUtils.class);

    /**
     * Download a dataset from Galaxy.
     *
     * @param galaxyInstance  the Galaxy server to retrieve the dataset from.
     * @param historiesClient the client for accessing Galaxy histories.
     * @param historyId       the ID of the history that contains the dataset.
     * @param datasetId       the ID of the dataset.
     * @param filePath        the (base) file path to write the dataset to.
     * @param dataType        the explicit data type of the dataset (or null to use the data type from the dataset).
     * @return whether the download was successful.
     */
    public boolean downloadDataset(final GalaxyInstance galaxyInstance, final HistoriesClient historiesClient,
                                   final String historyId, final String datasetId, final String filePath,
                                   final String dataType) {
        final Dataset dataset = historiesClient.showDataset(historyId, datasetId);
        final String toExt = (dataType != null) ? dataType : dataset.getDataType();
        final String url = galaxyInstance.getGalaxyUrl() + "/datasets/" + dataset.getId() + "/display/?to_ext=" + toExt;
        logger.trace("Downloading dataset \"{}\" to local file {}.", dataset.getName(), filePath);
        return downloadFileFromUrl(url, filePath);
    }

    /**
     * Download a dataset from a Galaxy URL to a local file.
     *
     * @param url          the Galaxy url to download the dataset from.
     * @param fullFilePath the full file path to download the dataset to.
     * @return whether the download was successful.
     */
    protected boolean downloadFileFromUrl(final String url, final String fullFilePath) {
        boolean success = false;
        try {
            final HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            final int responseCode = connection.getResponseCode();
            final boolean ok = responseCode == HttpURLConnection.HTTP_OK;
            if (!ok)
                logger.error("Reading from url {} is not working ok (response code: {}).", url, responseCode);
            try (final InputStream inputStream = ok ? connection.getInputStream() : connection.getErrorStream()) {
                Files.copy(inputStream, Paths.get(fullFilePath), StandardCopyOption.REPLACE_EXISTING);
                success = ok;
            }
        } catch (final IOException e) {
            logger.error("Error downloading or writing dataset file.", e);
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
    public String getDatasetIdByName(final String datasetName, final HistoriesClient historiesClient,
                                     final String historyId) {
        String datasetId = null;
        for (final HistoryContents historyDataset : historiesClient.showHistoryContents(historyId))
            if (historyDataset.getName().equals(datasetName)) {
                datasetId = historyDataset.getId();
                break;
            }
        return datasetId;
    }
}
