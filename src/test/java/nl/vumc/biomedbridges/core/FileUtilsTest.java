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
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * This class contains a unit test for the getWorkflow method of the WorkflowEngine interface.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class FileUtilsTest {
    /**
     * Test the cleanFileName method.
     */
    @Test
    public void testCleanFileName() {
        final String solutionInUkrainian = "розв'язування";
        assertEquals("_", FileUtils.cleanFileName(":"));
        assertEquals(solutionInUkrainian, FileUtils.cleanFileName(solutionInUkrainian));
        assertEquals("output edgeR DGE on 1_ design_matrix.txt - differentially expressed genes",
                     FileUtils.cleanFileName("output edgeR DGE on 1: design_matrix.txt - differentially expressed genes"));
    }

    /**
     * Test the createUniqueFilePath method.
     */
    @Test
    public void testCreateUniqueFilePath() throws IOException {
        final String temporaryDirectory = System.getProperty("java.io.tmpdir");
        final String baseName = getClass().getName() + System.currentTimeMillis();
        final String suffix = ".txt";
        final File uniqueFile1 = new File(FileUtils.createUniqueFilePath(temporaryDirectory, baseName, suffix));
        createFile(uniqueFile1, "1");
        final File uniqueFile2 = new File(FileUtils.createUniqueFilePath(temporaryDirectory, baseName, suffix));
        createFile(uniqueFile2, "2");
        final File uniqueFile3 = new File(FileUtils.createUniqueFilePath(temporaryDirectory, baseName, suffix));
        createFile(uniqueFile3, "3");

        final String uniquePath2 = uniqueFile2.getAbsolutePath();
        final String uniquePath3 = uniqueFile3.getAbsolutePath();
        assertTrue(uniqueFile1.getAbsolutePath().endsWith(suffix));
        assertTrue(uniquePath2.endsWith(suffix));
        assertTrue(uniquePath3.endsWith(suffix));
        final int index2 = Integer.parseInt(uniquePath2.substring(uniquePath2.lastIndexOf('-') + 1, uniquePath2.lastIndexOf('.')));
        final int index3 = Integer.parseInt(uniquePath3.substring(uniquePath3.lastIndexOf('-') + 1, uniquePath3.lastIndexOf('.')));
        assertEquals(index2 + 1, index3);

        assertTrue(uniqueFile1.delete());
        assertTrue(uniqueFile2.delete());
        assertTrue(uniqueFile3.delete());
    }

    /**
     * Create a test file.
     *
     * @param file the file to be created.
     * @param line the line to be written.
     * @throws IOException if creating or writing to the file fails.
     */
    private void createFile(final File file, final String line) throws IOException {
        try (final Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            writer.write(line);
            writer.write("\n");
        }
    }
}
