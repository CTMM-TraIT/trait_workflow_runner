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

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for the ConcatenateExample class.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class ConcatenateExampleTest {
    /**
     * Test the concatenate example under normal circumstances.
     */
    @Test
    public void testConcatenateExampleNormal() {
        final String line1 = ConcatenateExample.LINE_TEST_FILE_1;
        final String line2 = ConcatenateExample.LINE_TEST_FILE_2;
        final ConcatenateExample concatenateExample = initializeConcatenateExample(true, line1, line2);

        assertTrue(concatenateExample.runExample());
    }

    /**
     * Test the concatenate example when no output is generated.
     */
    @Test
    public void testConcatenateExampleNoOutput() {
        final ConcatenateExample concatenateExample = initializeConcatenateExample(false, null, null);

        assertFalse(concatenateExample.runExample());
    }

    /**
     * Test the concatenate example when invalid output is generated.
     */
    @Test
    public void testConcatenateExampleInvalidOutput() {
        final String line1 = ConcatenateExample.LINE_TEST_FILE_1 + " - something";
        final String line2 = ConcatenateExample.LINE_TEST_FILE_2 + " - is wrong!";
        final ConcatenateExample concatenateExample = initializeConcatenateExample(true, line1, line2);

        assertFalse(concatenateExample.runExample());
    }

    /**
     * Create and initialize a concatenate example instance.
     *
     * @param generateOutput whether an output file should be generated.
     * @param line1 the first output line.
     * @param line2 the second output line.
     * @return the concatenate example instance.
     */
    private ConcatenateExample initializeConcatenateExample(final boolean generateOutput, final String line1,
                                                            final String line2) {
        // Create a Guice injector and use it to build the ConcatenateExample object.
        final Injector injector = Guice.createInjector(new TestGuiceModule());
        final ConcatenateExample concatenateExample = injector.getInstance(ConcatenateExample.class);

        if (generateOutput)
            addOutputFilesToOutputMap(concatenateExample.workflowEngineFactory, line1, line2);
        return concatenateExample;
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
        final Workflow workflow = workflowEngineFactory.getWorkflowEngine(null).getWorkflow(null);
        workflow.addOutput(ConcatenateExample.OUTPUT_NAME, temporaryOutputFile);
    }
}
