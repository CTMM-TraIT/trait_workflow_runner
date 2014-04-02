/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.biomedbridges.v2.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple utility method for creating temporary input files.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class FileUtils {
    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    /**
     * Hidden constructor. Only the static methods of this class are meant to be used.
     */
    private FileUtils() {
    }

    /**
     * Create a temporary file with a single line.
     *
     * @param lines the lines to write to the test file.
     * @return the test file.
     */
    public static File createInputFile(final String... lines) {
        try {
            final File tempFile = File.createTempFile("workflow-runner", ".txt");
            try (final Writer writer = new OutputStreamWriter(new FileOutputStream(tempFile), StandardCharsets.UTF_8)) {
                for (final String line : lines) {
                    writer.write(line);
                    writer.write("\n");
                }
            }
            return tempFile;
        } catch (final IOException e) {
            logger.error("Exception while creating a temporary input file.", e);
            throw new RuntimeException(e);
        }
    }
}
