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
    public void testGrepExampleNormal() throws IOException, InterruptedException {
        final GrepExample grepExample = initializeGrepExample();

        assertTrue(grepExample.run(Constants.THE_HYVE_GALAXY_URL, Constants.GREP_WORKFLOW,
                                   GrepExample.INPUT_FILE, GrepExample.PATTERN));
    }

    /**
     * Create and initialize a grep example instance.
     *
     * @return the grep example instance.
     */
    private GrepExample initializeGrepExample() throws IOException {
        final GrepExample grepExample = new GrepExample(new DummyWorkflowFactory());
        addOutputFileToOutputMap(grepExample, GrepExample.internalGrep(GrepExample.INPUT_FILE, GrepExample.PATTERN));

        return grepExample;
    }

    /**
     * Add a temporary output file to the output map of the dummy workflow.
     *
     * @param grepExample   the grep example.
     * @param expectedLines the lines that we expect in the output file.
     */
    private void addOutputFileToOutputMap(final GrepExample grepExample, final List<String> expectedLines) {
        final File temporaryOutputFile = FileUtils.createTemporaryFile(expectedLines);
        final Workflow workflow = grepExample.workflowFactory.getWorkflow(null, null, Constants.GREP_WORKFLOW);
        workflow.addOutput("matching_lines", temporaryOutputFile);
    }
}
