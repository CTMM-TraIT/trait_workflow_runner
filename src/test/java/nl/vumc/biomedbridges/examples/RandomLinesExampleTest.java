/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.examples;

import com.google.inject.Guice;

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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for the RandomLinesExample class.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class RandomLinesExampleTest {
    /**
     * Test the random lines example under normal circumstances.
     */
    @Test
    public void testRandomLinesExampleNormal() {
        final int definitiveLineCount = RandomLinesExample.DEFINITIVE_LINE_COUNT;
        final RandomLinesExample randomLinesExample = initializeRandomLinesExample(true, definitiveLineCount);

        assertTrue(randomLinesExample.runExample(Constants.CENTRAL_GALAXY_URL));
    }

    /**
     * Test the random lines example when no output is generated.
     */
    @Test
    public void testRandomLinesExampleNoOutput() {
        final RandomLinesExample randomLinesExample = initializeRandomLinesExample(false, 0);

        assertFalse(randomLinesExample.runExample(Constants.CENTRAL_GALAXY_URL));
    }

    /**
     * Test the random lines example when invalid output is generated.
     */
    @Test
    public void testRandomLinesExampleInvalidOutput() {
        final int definitiveLineCount = RandomLinesExample.DEFINITIVE_LINE_COUNT + 6;
        final RandomLinesExample randomLinesExample = initializeRandomLinesExample(true, definitiveLineCount);

        assertFalse(randomLinesExample.runExample(Constants.CENTRAL_GALAXY_URL));
    }

    /**
     * Test the random lines example with a workflow returning false.
     */
    @Test
    public void testRandomLinesExampleReturningFalse() {
        final int definitiveLineCount = RandomLinesExample.DEFINITIVE_LINE_COUNT + 6;
        final RandomLinesExample randomLinesExample = initializeRandomLinesExample(true, definitiveLineCount);

        DummyWorkflow.setReturnedResult(false);
        assertFalse(randomLinesExample.runExample(Constants.CENTRAL_GALAXY_URL));
    }

    /**
     * Test the random lines example with a workflow throwing an exception.
     */
    @Test
    public void testRandomLinesExampleThrowingException() {
        final int definitiveLineCount = RandomLinesExample.DEFINITIVE_LINE_COUNT + 6;
        final RandomLinesExample randomLinesExample = initializeRandomLinesExample(true, definitiveLineCount);

        DummyWorkflow.setThrowException(true);
        assertFalse(randomLinesExample.runExample(Constants.CENTRAL_GALAXY_URL));
    }

    /**
     * Create and initialize a random lines example instance.
     *
     * @param generateOutput whether an output file should be generated.
     * @param definitiveLineCount the number of lines to put in the output file.
     * @return the random lines example instance.
     */
    private RandomLinesExample initializeRandomLinesExample(final boolean generateOutput, final int definitiveLineCount) {
        // Create a Guice injector and use it to build the RandomLinesExample object.
        final RandomLinesExample example = Guice.createInjector(new TestGuiceModule()).getInstance(RandomLinesExample.class);
        if (generateOutput)
            addOutputFileToOutputMap(example.workflowFactory, definitiveLineCount);
        return example;
    }

    /**
     * Add a temporary output file to the output map of the dummy workflow.
     *
     * @param workflowFactory     the dummy workflow factory.
     * @param definitiveLineCount the number of lines to put in the output file.
     */
    private void addOutputFileToOutputMap(final WorkflowFactory workflowFactory,
                                          final int definitiveLineCount) {
        final List<String> dummyLines = new ArrayList<>();
        for (int lineIndex = 0; lineIndex < definitiveLineCount; lineIndex++)
            dummyLines.add("");
        final File temporaryOutputFile = FileUtils.createTemporaryFile(dummyLines.toArray(new String[dummyLines.size()]));
        final Workflow workflow = workflowFactory.getWorkflow(WorkflowType.DEMONSTRATION, null,
                                                              Constants.WORKFLOW_RANDOM_LINES_TWICE);
        workflow.addOutput(RandomLinesExample.OUTPUT_NAME, temporaryOutputFile);
    }
}
