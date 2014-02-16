/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.biomedbridges;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.beans.Dataset;
import com.github.jmchilton.blend4j.galaxy.beans.HistoryContents;

import javax.net.ssl.HttpsURLConnection;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Simple utility method for working with Galaxy histories.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class HistoryUtils {
    private static final int DOWNLOAD_BUFFER_SIZE = 8 * 1024;

    public static boolean downloadDataset(final GalaxyInstance galaxyInstance, final HistoriesClient historiesClient,
                                          final String historyId, final String datasetId, final String filePath,
                                          final boolean useDefaultFilename, final String toExt) {
        final Dataset dataset = historiesClient.showDataset(historyId, datasetId);
        final String dataType = (toExt != null) ? toExt : dataset.getDataType();
        final String url = galaxyInstance.getGalaxyUrl() + "/datasets/" + dataset.getId() + "/display/" + "?to_ext="
                           + dataType;
        final String fileLocalPath = filePath + (useDefaultFilename ? File.separator + dataset.getName() : "");
        return downloadFileFromUrl(url, fileLocalPath);
    }

    private static boolean downloadFileFromUrl(final String url, final String fileLocalPath) {
        boolean success = true;
        OutputStream outputStream = null;
        try {
            final HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
            final int responseCode = connection.getResponseCode();
            final InputStream inputStream = (responseCode == HttpURLConnection.HTTP_OK)
                                            ? connection.getInputStream()
                                            : connection.getErrorStream();
            outputStream = new FileOutputStream(fileLocalPath);
            final byte[] buffer = new byte[DOWNLOAD_BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (final IOException e) {
            System.err.println("HistoryUtils.downloadFileFromUrl: error downloading or writing dataset file.");
            e.printStackTrace();
            success = false;
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (final IOException e) {
                    System.err.println("HistoryUtils.downloadFileFromUrl: error closing dataset file.");
                    e.printStackTrace();
                    success = false;
                }
            }
        }
        return success;
    }

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
