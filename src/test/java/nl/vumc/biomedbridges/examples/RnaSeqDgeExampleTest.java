/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.examples;

import com.google.inject.Guice;
import com.google.inject.Injector;

import nl.vumc.biomedbridges.core.TestGuiceModule;
import nl.vumc.biomedbridges.core.WorkflowEngineFactory;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Unit tests for the RnaSeqDgeExample class.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class RnaSeqDgeExampleTest {
    /**
     * Test the RNA-Seq differential gene expression example under normal circumstances.
     */
    @Test
    public void testRnaSeqDgeExampleNormal() {
        final RnaSeqDgeExample rnaSeqDgeExample = initializeRnaSeqDgeExample();

        final String directory = RnaSeqDgeExample.EXAMPLES_DIRECTORY;
        assertTrue(rnaSeqDgeExample.runExample(WorkflowEngineFactory.GALAXY_TYPE,
                                               directory + "MCF7_featureCounts_concatenated.txt",
                                               directory + "design_matrix.txt", "Control-E2", 0.05));
    }


    /**
     * Create and initialize a concatenate example instance.
     *
     * @return the concatenate example instance.
     */
    private RnaSeqDgeExample initializeRnaSeqDgeExample() {
        // Create a Guice injector and use it to build the RnaSeqDgeExample object.
        final Injector injector = Guice.createInjector(new TestGuiceModule());
        return injector.getInstance(RnaSeqDgeExample.class);
    }
}
