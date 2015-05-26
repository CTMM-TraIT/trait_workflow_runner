/**
 * Copyright 2015 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.examples;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import nl.vumc.biomedbridges.core.Constants;
import nl.vumc.biomedbridges.core.DummyWorkflowFactory;
import nl.vumc.biomedbridges.core.FileUtils;
import nl.vumc.biomedbridges.core.Workflow;
import nl.vumc.biomedbridges.core.WorkflowFactory;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for the LineCountExample class.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class LineCountExampleTest {
    /**
     * Test the line count example under normal circumstances.
     */
    @Test
    public void testLineCountExampleNormal() throws IOException {
        final File inputFile = FileUtils.createTemporaryFile("8\t21", "9\t34", "10\t55", "11\t89", "12\t144");
        final List<String> expectedLines = LineCountExample.internalCounts(inputFile);

        final LineCountExample lineCountExample = initializeLineCountExample(true, expectedLines);

        assertTrue(lineCountExample.run(Constants.CENTRAL_GALAXY_URL, Constants.LINE_COUNT_WORKFLOW, inputFile, true));
    }

    /**
     * Test the getExpectedLines method.
     */
    @Test
    public void testGetExpectedLines() {
        final LineCountExample example = new LineCountExample(null);
        assertEquals(Arrays.asList(LineCountExample.HEADER_LINE, "13052\t107533"), example.getExpectedLines());
    }

    /**
     * Create and initialize a line count example instance.
     *
     * @param generateOutput whether an output file should be generated.
     * @param expectedLines  the lines that we expect in the output file.
     * @return the line count example instance.
     */
    private LineCountExample initializeLineCountExample(final boolean generateOutput, final List<String> expectedLines) {
        final LineCountExample lineCountExample = new LineCountExample(new DummyWorkflowFactory());

        if (generateOutput)
            addOutputFileToOutputMap(lineCountExample.workflowFactory, expectedLines);

        return lineCountExample;
    }

    /**
     * Add a temporary output file to the output map of the dummy workflow.
     *
     * @param workflowFactory the workflow factory.
     * @param expectedLines   the lines that we expect in the output file.
     */
    private void addOutputFileToOutputMap(final WorkflowFactory workflowFactory, final List<String> expectedLines) {
        final File temporaryOutputFile = FileUtils.createTemporaryFile(expectedLines);
        final Workflow workflow = workflowFactory.getWorkflow(null, null, Constants.GREP_WORKFLOW);
        workflow.addOutput(LineCountExample.OUTPUT_NAME, temporaryOutputFile);
    }
}
