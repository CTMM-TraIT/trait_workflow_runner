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

import nl.vumc.biomedbridges.core.DummyWorkflow;
import nl.vumc.biomedbridges.core.FileUtils;
import nl.vumc.biomedbridges.core.TestGuiceModule;
import nl.vumc.biomedbridges.core.WorkflowEngineFactory;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Unit test for the RandomLinesExample class.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class RandomLinesExampleTest {
    /**
     * Test the random lines example.
     */
    @Test
    public void testRandomLinesExample() {
        // Create a Guice injector and use it to build the RandomLinesExample object.
        final Injector injector = Guice.createInjector(new TestGuiceModule());
        final RandomLinesExample randomLinesExample = injector.getInstance(RandomLinesExample.class);

        addOutputFileToOutputMap(randomLinesExample.workflowEngineFactory);
        assertTrue(randomLinesExample.runExample(WorkflowEngineFactory.GALAXY_TYPE,
                                                 RandomLinesExample.INITIAL_LINE_COUNT,
                                                 RandomLinesExample.DEFINITIVE_LINE_COUNT));
    }

    /**
     * Add a temporary output file to the output map of the dummy workflow.
     *
     * @param workflowEngineFactory the dummy workflow engine factory.
     */
    private void addOutputFileToOutputMap(final WorkflowEngineFactory workflowEngineFactory) {
        final List<String> dummyLines = new ArrayList<>();
        for (int lineIndex = 0; lineIndex < RandomLinesExample.DEFINITIVE_LINE_COUNT; lineIndex++)
            dummyLines.add("");
        final File temporaryOutputFile = FileUtils.createTemporaryFile(dummyLines.toArray(new String[dummyLines.size()]));
        final DummyWorkflow workflow = (DummyWorkflow) workflowEngineFactory.getWorkflowEngine(null).getWorkflow(null);
        workflow.addToOutputMap(RandomLinesExample.OUTPUT_NAME, temporaryOutputFile);
    }
}
