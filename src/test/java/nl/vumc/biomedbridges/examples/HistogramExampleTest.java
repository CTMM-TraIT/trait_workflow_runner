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
 * Unit test for the HistogramExample class.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class HistogramExampleTest {
    /**
     * Test the histogram example.
     */
    @Test
    public void testHistogramExample() {
        // Create a Guice injector and use it to build the HistogramExample object.
        final Injector injector = Guice.createInjector(new TestGuiceModule());
        final HistogramExample histogramExample = injector.getInstance(HistogramExample.class);
        addPdfToOutputMap(histogramExample.workflowEngineFactory);
        assertTrue(histogramExample.runExample());
    }

    /**
     * Add a dummy temporary pdf file to the output map of the dummy workflow.
     *
     * @param workflowEngineFactory the dummy workflow engine factory.
     */
    private void addPdfToOutputMap(final WorkflowEngineFactory workflowEngineFactory) {
        final List<String> dummyLines = new ArrayList<>();
        dummyLines.add("%PDF-1.4");
        for (int lineIndex = 0; lineIndex < 499; lineIndex++)
            dummyLines.add("");
        final File temporaryPdfFile = FileUtils.createTemporaryFile(dummyLines.toArray(new String[dummyLines.size()]));
        final Workflow workflow = workflowEngineFactory.getWorkflowEngine(null).getWorkflow(null);
        workflow.addOutput(HistogramExample.OUTPUT_NAME, temporaryPdfFile);
    }
}
