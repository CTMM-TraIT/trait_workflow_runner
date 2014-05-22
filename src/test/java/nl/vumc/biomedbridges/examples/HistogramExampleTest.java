/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.biomedbridges.examples;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import nl.vumc.biomedbridges.core.DummyWorkflow;
import nl.vumc.biomedbridges.core.DummyWorkflowEngineFactory;
import nl.vumc.biomedbridges.core.FileUtils;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Unit test for the HistogramExample class.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class HistogramExampleTest {
    /**
     * Test the histogram example.
     *
     * todo: test fails when running with Maven (mvn test):
     * "java.lang.AssertionError: null
             at org.junit.Assert.fail(Assert.java:86)
             at org.junit.Assert.assertTrue(Assert.java:41)
             at org.junit.Assert.assertTrue(Assert.java:52)
             at nl.vumc.biomedbridges.examples.HistogramExampleTest.testHistogramExample(HistogramExampleTest.java:23)".
     */
    @Test
    public void testHistogramExample() {
        // todo: create Guice module for unit testing, maybe in core package?
        final DummyWorkflowEngineFactory workflowEngineFactory = new DummyWorkflowEngineFactory();
        addPdfToOutputMap(workflowEngineFactory);
        assertTrue(new HistogramExample(workflowEngineFactory).runExample());
    }

    /**
     *
     *
     * @param workflowEngineFactory
     */
    private void addPdfToOutputMap(final DummyWorkflowEngineFactory workflowEngineFactory) {
        final List<String> dummyLines = new ArrayList<>();
        dummyLines.add("%PDF-1.4");
        for (int lineIndex = 0; lineIndex < 499; lineIndex++)
            dummyLines.add("");
        final File temporaryPdfFile = FileUtils.createTemporaryFile(dummyLines.toArray(new String[dummyLines.size()]));
        final DummyWorkflow workflow = (DummyWorkflow) workflowEngineFactory.getWorkflowEngine(null).getWorkflow(null);
        workflow.addToOutputMap("out_file1", temporaryPdfFile);
    }
}
