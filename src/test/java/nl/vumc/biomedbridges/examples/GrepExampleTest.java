/**
 * Copyright 2015 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.examples;

import java.io.File;
import java.io.IOException;
import java.util.List;

import nl.vumc.biomedbridges.core.Constants;
import nl.vumc.biomedbridges.core.DummyWorkflowFactory;
import nl.vumc.biomedbridges.core.FileUtils;
import nl.vumc.biomedbridges.core.Workflow;
import nl.vumc.biomedbridges.core.WorkflowFactory;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Unit tests for the GrepExample class.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class GrepExampleTest {
    /**
     * Test the grep example under normal circumstances.
     */
    @Test
    public void testGrepExampleNormal() throws IOException {
        final File inputFile = FileUtils.createTemporaryFile("8\t21", "9\t34", "10\t55", "11\t89", "12\t144");
        final String pattern = "5[0-9]";

        final GrepExample grepExample = initializeGrepExample(true, GrepExample.internalGrep(inputFile, pattern));

        assertTrue(grepExample.run(Constants.CENTRAL_GALAXY_URL, Constants.GREP_WORKFLOW, inputFile, pattern));
    }

//    /**
//     * Test the concatenate example when no output is generated.
//     */
//    @Test
//    public void testConcatenateExampleNoOutput() {
//        final ConcatenateExample concatenateExample = initializeGrepExample(false, null, null);
//
//        assertFalse(concatenateExample.runExample(Constants.CENTRAL_GALAXY_URL));
//    }
//
//    /**
//     * Test the concatenate example when invalid output is generated.
//     */
//    @Test
//    public void testConcatenateExampleInvalidOutput() {
//        final String line1 = ConcatenateExample.LINE_TEST_FILE_1 + " - something";
//        final String line2 = ConcatenateExample.LINE_TEST_FILE_2 + " - is wrong!";
//        final ConcatenateExample concatenateExample = initializeGrepExample(true, line1, line2);
//
//        assertFalse(concatenateExample.runExample(Constants.CENTRAL_GALAXY_URL));
//    }

    /**
     * Create and initialize a grep example instance.
     *
     * @param generateOutput whether an output file should be generated.
     * @param expectedLines  the lines that we expect in the output file.
     * @return the grep example instance.
     */
    private GrepExample initializeGrepExample(final boolean generateOutput, final List<String> expectedLines) {
        final GrepExample grepExample = new GrepExample(new DummyWorkflowFactory());

        if (generateOutput)
            addOutputFileToOutputMap(grepExample.workflowFactory, expectedLines);

        return grepExample;
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
        workflow.addOutput("matching_lines", temporaryOutputFile);
    }
}
