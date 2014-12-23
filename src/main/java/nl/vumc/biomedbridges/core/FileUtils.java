/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple utility method for creating temporary files.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class FileUtils {
    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    /**
     * File name prefix for files that are created.
     */
    private static final String FILE_NAME_PREFIX = "workflow-runner";

    /**
     * File name suffix for files that are created.
     */
    private static final String FILE_NAME_SUFFIX = ".txt";

    /**
     * New line character.
     */
    private static final char NEW_LINE = '\n';

    /**
     * Hidden constructor. Only the static methods of this class are meant to be used.
     */
    private FileUtils() {
    }

    /**
     * Create a file with some lines.
     *
     * @param filePath the file path to use.
     * @param lines    the lines to write to the test file.
     * @return the test file.
     */
    public static File createFile(final String filePath, final String... lines) {
        try {
            final File newFile = new File(filePath);
            writeLinesToFile(newFile, lines);
            return newFile;
        } catch (final IOException e) {
            logger.error("Exception while creating a file.", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Write lines to a file.
     *
     * @param file  the file to write to.
     * @param lines the lines to write to the file.
     * @throws IOException if writing to the file fails.
     */
    private static void writeLinesToFile(final File file, final String... lines) throws IOException {
        try (final Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            for (final String line : lines) {
                writer.write(line);
                writer.write(NEW_LINE);
            }
        }
    }

    /**
     * Create a file with some lines.
     *
     * todo: compare to createTemporaryFile.
     * todo: eliminate workflow parameter.
     * todo: make filenamePrefix a parameter?
     *
     * @param workflow the workflow where this output file is created for.
     * @param lines    the lines to write to the output file.
     * @return the test file.
     */
    public static File createOutputFile(final Workflow workflow, final String... lines) {
        final String filenamePrefix = cleanFileName(String.format("%s-%s-output", FILE_NAME_PREFIX,
                                                                  workflow.getName().toLowerCase()));
        return createTemporaryFileWithPrefix(filenamePrefix, lines);
    }

    /**
     * Create a temporary file with some lines (using "workflow-runner" as the file name prefix).
     *
     * @param lines the lines to write to the test file.
     * @return the test file.
     */
    public static File createTemporaryFile(final String... lines) {
        return createTemporaryFileWithPrefix(FILE_NAME_PREFIX, lines);
    }

    /**
     * Create a temporary file from the contents of a URL.
     *
     * @param url the URL to read from.
     * @return the test file.
     */
    public static File createTemporaryFileFromURL(final URL url) {
        File tempFile = null;
        try {
            tempFile = File.createTempFile(FILE_NAME_PREFIX, FILE_NAME_SUFFIX);
            Files.copy(url.openStream(), tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return tempFile;
    }

    /**
     * Create a temporary file with some lines.
     *
     * @param filenamePrefix the file name prefix to use.
     * @param lines          the lines to write to the test file.
     * @return the test file.
     */
    public static File createTemporaryFileWithPrefix(final String filenamePrefix, final String... lines) {
        try {
            final File tempFile = File.createTempFile(filenamePrefix, FILE_NAME_SUFFIX);
            writeLinesToFile(tempFile, lines);
            return tempFile;
        } catch (final IOException e) {
            logger.error("Exception while creating the output file.", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Replace all characters that could give file system problems with underscores.
     *
     * Inspired by http://stackoverflow.com/a/5626340/1694043 and sun.nio.fs.WindowsPathParser.isInvalidPathChar.
     *
     * @param originalFileName the original file name.
     * @return the clean file name that should be safe on the popular platforms.
     */
    public static String cleanFileName(final String originalFileName) {
        // See https://en.wikipedia.org/wiki/ASCII#ASCII_control_code_chart for information on ASCII control characters.
        final int controlCharactersLimit = 32;
        final int ruboutCharacter = 127;
        final StringBuilder cleanName = new StringBuilder();
        for (final char character : originalFileName.toCharArray()) {
            final boolean controlCharacter = character < controlCharactersLimit || character == ruboutCharacter;
            cleanName.append(!controlCharacter && "\\/<>:\"|?*".indexOf(character) == -1 ? character : "_");
        }
        return cleanName.toString();
    }

    /**
     * Create a unique file path from a directory, base name, and suffix. While the file path exists, a minus and an
     * increasing number are added after the base name.
     *
     * @param directory the directory where the file should be created.
     * @param baseName the base name.
     * @param suffix the suffix (starting with a period).
     * @return a unique file path.
     */
    public static String createUniqueFilePath(final String directory, final String baseName, final String suffix) {
        Path filePath = Paths.get(directory, baseName + suffix);
        int index = 1;
        while (Files.exists(filePath)) {
            filePath = Paths.get(directory, baseName + "-" + index + suffix);
            index++;
        }
        return filePath.toString();
    }
}
