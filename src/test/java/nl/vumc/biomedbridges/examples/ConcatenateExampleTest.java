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

import static org.junit.Assert.assertTrue;

/**
 * Unit test for the ConcatenateExample class.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class ConcatenateExampleTest {
    /**
     * Test the concatenate example.
     */
    @Test
    public void testConcatenateExample() {
        // Create a Guice injector and use it to build the ConcatenateExample object.
        final Injector injector = Guice.createInjector(new TestGuiceModule());
        final ConcatenateExample concatenateExample = injector.getInstance(ConcatenateExample.class);

        addOutputFilesToOutputMap(concatenateExample.workflowEngineFactory);
        assertTrue(concatenateExample.runExample());
    }

    /**
     * Add a temporary output file to the output map of the dummy workflow.
     *
     * @param workflowEngineFactory the dummy workflow engine factory.
     */
    private void addOutputFilesToOutputMap(final WorkflowEngineFactory workflowEngineFactory) {
        final List<String> dummyLines = new ArrayList<>();
        dummyLines.add(ConcatenateExample.LINE_TEST_FILE_1);
        dummyLines.add(ConcatenateExample.LINE_TEST_FILE_2);
        final File temporaryOutputFile = FileUtils.createTemporaryFile(dummyLines.toArray(new String[dummyLines.size()]));
        final Workflow workflow = workflowEngineFactory.getWorkflowEngine(null).getWorkflow(null);
        workflow.addOutput(ConcatenateExample.OUTPUT_NAME, temporaryOutputFile);
    }
}
