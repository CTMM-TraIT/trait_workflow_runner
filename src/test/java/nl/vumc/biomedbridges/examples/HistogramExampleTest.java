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

import nl.vumc.biomedbridges.core.Constants;
import nl.vumc.biomedbridges.core.DummyWorkflow;
import nl.vumc.biomedbridges.core.FileUtils;
import nl.vumc.biomedbridges.core.TestGuiceModule;
import nl.vumc.biomedbridges.core.Workflow;
import nl.vumc.biomedbridges.core.WorkflowFactory;
import nl.vumc.biomedbridges.core.WorkflowType;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for the HistogramExample class.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class HistogramExampleTest {
    /**
     * Test the histogram example in several different scenarios.
     */
    @Test
    public void testHistogramExampleScenarios() {
        testHistogramExample(true, true, 496, true);
        testHistogramExample(true, true, 6, false);
        testHistogramExample(true, true, 8128, false);
        testHistogramExample(true, false, 496, false);
        testHistogramExample(false, false, 496, false);
    }

    /**
     * Test the histogram example with a workflow returning false.
     */
    @Test
    public void testHistogramExampleReturningFalse() {
        testHistogramExample(false, false, true, true, 496, false);
    }

    /**
     * Test the histogram example with a workflow throwing an exception.
     */
    @Test
    public void testHistogramExampleThrowingException() {
        testHistogramExample(true, true, true, true, 496, false);
    }

    /**
     * Test the histogram example.
     *
     * @param withOutput     whether an output file should be generated to simulate the workflow result.
     * @param withHeader     whether the output file should contain a pdf header line.
     * @param lineCount      the total number of lines to add to the file.
     * @param expectedResult the result expected from running the example.
     */
    private void testHistogramExample(final boolean withOutput, final boolean withHeader, final int lineCount,
                                      final boolean expectedResult) {
        testHistogramExample(true, false, withOutput, withHeader, lineCount, expectedResult);
    }

    /**
     * Test the histogram example.
     *
     * @param returnedResult the value that will be returned by the run method.
     * @param throwException whether an exception should be thrown by the run method.
     * @param withOutput     whether an output file should be generated to simulate the workflow result.
     * @param withHeader     whether the output file should contain a pdf header line.
     * @param lineCount      the total number of lines to add to the file.
     * @param expectedResult the result expected from running the example.
     */
    private void testHistogramExample(final boolean returnedResult, final boolean throwException, final boolean withOutput,
                                      final boolean withHeader, final int lineCount, final boolean expectedResult) {
        // Create a Guice injector and use it to build the HistogramExample object.
        final Injector injector = Guice.createInjector(new TestGuiceModule());
        final HistogramExample histogramExample = injector.getInstance(HistogramExample.class);
        if (withOutput)
            addPdfToOutputMap(histogramExample.workflowFactory, withHeader, lineCount);
        DummyWorkflow.clear();
        DummyWorkflow.setReturnedResult(returnedResult);
        DummyWorkflow.setThrowException(throwException);
        assertEquals(expectedResult, histogramExample.runExample());
    }

    /**
     * Add a dummy temporary pdf file to the output map of the dummy workflow.
     *
     * @param workflowFactory the dummy workflow factory.
     * @param withHeader      whether the output file should contain a pdf header line.
     * @param lineCount       the total number of lines to add to the file.
     */
    private void addPdfToOutputMap(final WorkflowFactory workflowFactory, final boolean withHeader, final int lineCount) {
        final List<String> dummyLines = new ArrayList<>();
        if (withHeader)
            dummyLines.add("%PDF-1.4");
        for (int lineIndex = 0; lineIndex < lineCount - (withHeader ? 1 : 0); lineIndex++)
            dummyLines.add("");
        final File temporaryPdfFile = FileUtils.createTemporaryFile(dummyLines.toArray(new String[dummyLines.size()]));
        final Workflow workflow = workflowFactory.getWorkflow(WorkflowType.DEMONSTRATION, null, Constants.WORKFLOW_HISTOGRAM);
        workflow.addOutput(HistogramExample.OUTPUT_NAME, temporaryPdfFile);
    }
}
