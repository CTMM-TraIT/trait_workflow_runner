/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.core;

import com.google.inject.Guice;
import com.google.inject.Injector;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Unit test for the WorkflowRunner class.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class WorkflowRunnerTest {
    @Test
    public void testWorkflowRunner() {
        // Create a Guice injector and use it to build the WorkflowRunner object.
        final WorkflowRunner runner = Guice.createInjector(new DefaultGuiceModule()).getInstance(WorkflowRunner.class);

        assertTrue(runner.runWorkflowRunner(WorkflowType.DEMONSTRATION));
    }
}
