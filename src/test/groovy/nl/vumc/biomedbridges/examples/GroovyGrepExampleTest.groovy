/**
 * Copyright 2015 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.examples

import nl.vumc.biomedbridges.core.Constants
import nl.vumc.biomedbridges.core.DummyWorkflowFactory
import nl.vumc.biomedbridges.core.FileUtils
import nl.vumc.biomedbridges.core.WorkflowFactory
import org.junit.Test

import static org.junit.Assert.assertTrue

/**
 * Unit test for the GroovyGrepExample class.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
class GroovyGrepExampleTest {
    /**
     * Test the grep example under normal circumstances.
     */
    @Test
    public void testGrepExampleNormal() throws IOException, InterruptedException {
        final grepExample = initializeGrepExample()

        assertTrue(grepExample.run(Constants.THE_HYVE_GALAXY_URL, Constants.GREP_WORKFLOW,
                   GroovyGrepExample.INPUT_FILE, GroovyGrepExample.PATTERN))
    }

    /**
     * Create and initialize a grep example instance.
     *
     * @return the grep example instance.
     */
    private static initializeGrepExample() throws IOException {
        final grepExample = new GroovyGrepExample(new DummyWorkflowFactory())

        final expectedLines = GroovyGrepExample.internalGrep(GroovyGrepExample.INPUT_FILE, GroovyGrepExample.PATTERN)
        addOutputFileToOutputMap(grepExample.workflowFactory, expectedLines)

        grepExample
    }

    /**
     * Add a temporary output file to the output map of the dummy workflow.
     *
     * @param workflowFactory the workflow factory.
     * @param expectedLines the lines that we expect in the output file.
     */
    private static addOutputFileToOutputMap(final WorkflowFactory workflowFactory, final List<String> expectedLines) {
        final temporaryOutputFile = FileUtils.createTemporaryFile(expectedLines)
        final workflow = workflowFactory.getWorkflow(null, null, Constants.GREP_WORKFLOW)
        workflow.addOutput("matching_lines", temporaryOutputFile)
    }
}
