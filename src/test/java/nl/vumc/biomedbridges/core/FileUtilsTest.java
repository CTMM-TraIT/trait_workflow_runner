/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

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
        assertEquals("_", FileUtils.cleanFileName(":"));
        assertEquals("output edgeR DGE on 1_ design_matrix.txt - differentially expressed genes",
                     FileUtils.cleanFileName("output edgeR DGE on 1: design_matrix.txt - differentially expressed genes"));
    }
}
