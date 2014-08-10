/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.core;

import java.io.File;
import java.io.IOException;

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
        assertEquals(solutionInUkrainian, FileUtils.cleanFileName(solutionInUkrainian));
        assertEquals("_", FileUtils.cleanFileName(":"));
        assertEquals("_", FileUtils.cleanFileName(Character.toString((char) 31)));
        assertEquals("_", FileUtils.cleanFileName(Character.toString((char) 127)));
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
        FileUtils.createFile(uniqueFile1.getAbsolutePath(), "1");
        final File uniqueFile2 = new File(FileUtils.createUniqueFilePath(temporaryDirectory, baseName, suffix));
        FileUtils.createFile(uniqueFile2.getAbsolutePath(), "2");
        final File uniqueFile3 = new File(FileUtils.createUniqueFilePath(temporaryDirectory, baseName, suffix));
        FileUtils.createFile(uniqueFile3.getAbsolutePath(), "3");

        final String path2 = uniqueFile2.getAbsolutePath();
        final String path3 = uniqueFile3.getAbsolutePath();
        assertTrue(uniqueFile1.getAbsolutePath().endsWith(suffix));
        assertTrue(path2.endsWith(suffix));
        assertTrue(path3.endsWith(suffix));
        final int index2 = Integer.parseInt(path2.substring(path2.lastIndexOf('-') + 1, path2.lastIndexOf('.')));
        final int index3 = Integer.parseInt(path3.substring(path3.lastIndexOf('-') + 1, path3.lastIndexOf('.')));
        assertEquals(index2 + 1, index3);

        assertTrue(uniqueFile1.delete());
        assertTrue(uniqueFile2.delete());
        assertTrue(uniqueFile3.delete());
    }

    @Test(expected = java.lang.RuntimeException.class)
    public void testCreateFileInvalidPath() {
        FileUtils.createFile("/this\\path/should\\be/invalid\\everywhere/<>:\"|?*.txt", "this", "should", "fail");
    }
}
