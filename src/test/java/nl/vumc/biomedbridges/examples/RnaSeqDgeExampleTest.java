/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.examples;

import com.google.inject.Guice;
import com.google.inject.Injector;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import nl.vumc.biomedbridges.core.FileUtils;
import nl.vumc.biomedbridges.core.TestGuiceModule;
import nl.vumc.biomedbridges.core.Workflow;
import nl.vumc.biomedbridges.core.WorkflowEngineFactory;

import org.junit.Ignore;
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
    @Ignore
    @Test
    public void testRnaSeqDgeExampleNormal() {
        final String line1 = ConcatenateExample.LINE_TEST_FILE_1;
        final String line2 = ConcatenateExample.LINE_TEST_FILE_2;
        final RnaSeqDgeExample rnaSeqDgeExample = initializeConcatenateExample(true, line1, line2);

        final String directory = RnaSeqDgeExample.EXAMPLES_DIRECTORY;
        assertTrue(rnaSeqDgeExample.runExample(WorkflowEngineFactory.GALAXY_TYPE,
                                               directory + "MCF7_featureCounts_concatenated.txt",
                                               directory + "design_matrix.txt", "Control-E2"));
    }


    /**
     * Create and initialize a concatenate example instance.
     *
     * @param generateOutput whether an output file should be generated.
     * @param line1 the first output line.
     * @param line2 the second output line.
     * @return the concatenate example instance.
     */
    private RnaSeqDgeExample initializeConcatenateExample(final boolean generateOutput, final String line1,
                                                            final String line2) {
        // Create a Guice injector and use it to build the ConcatenateExample object.
        final Injector injector = Guice.createInjector(new TestGuiceModule());
        final RnaSeqDgeExample rnaSeqDgeExample = injector.getInstance(RnaSeqDgeExample.class);

        if (generateOutput)
            addOutputFilesToOutputMap(rnaSeqDgeExample.workflowEngineFactory, line1, line2);
        return rnaSeqDgeExample;
    }

    /**
     * Add a temporary output file to the output map of the dummy workflow.
     *
     * @param workflowEngineFactory the dummy workflow engine factory.
     * @param line1 the first output line.
     * @param line2 the second output line.
     */
    private void addOutputFilesToOutputMap(final WorkflowEngineFactory workflowEngineFactory, final String line1,
                                           final String line2) {
        final List<String> dummyLines = new ArrayList<>();
        dummyLines.add(line1);
        dummyLines.add(line2);
        final File temporaryOutputFile = FileUtils.createTemporaryFile(dummyLines.toArray(new String[dummyLines.size()]));
        final Workflow workflow = workflowEngineFactory.getWorkflowEngine(null, null).getWorkflow(null);
        workflow.addOutput(ConcatenateExample.OUTPUT_NAME, temporaryOutputFile);
    }
}
