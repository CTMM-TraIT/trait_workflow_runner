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
     * @return whether the download was successful.
     */
    public boolean downloadDataset(final GalaxyInstance galaxyInstance, final HistoriesClient historiesClient,
                                   final String historyId, final String datasetId, final String filePath) {
        final Dataset dataset = historiesClient.showDataset(historyId, datasetId);
        final File destinationFile = new File(filePath);
        final String destinationPath = destinationFile.getAbsolutePath();
        logger.trace("Downloading dataset \"{}\" to local file {}.", dataset.getName(), destinationPath);
        boolean successful = true;
        if (destinationFile.exists()) {
            if (destinationFile.delete())
                logger.warn("The local file {} already existed and was removed.", destinationPath);
            else {
                logger.error("The local file {} already existed and could not be removed.", destinationPath);
                successful = false;
            }
        }
        if (successful) {
            final WebResource historyResource = galaxyInstance.getWebResource().path("histories");
            final WebResource contentsResource = historyResource.path(historyId).path("contents");
            final File downloadedFile = contentsResource.path(datasetId).path("display").get(File.class);
            logger.trace("downloadedFile.getAbsolutePath(): {}", downloadedFile.getAbsolutePath());
            successful = downloadedFile.exists();
            if (!successful)
                logger.error("Exception while downloading dataset {} from history {} to local file {}.", datasetId,
                             historyId, filePath);
        }
        return successful;
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
