/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.biomedbridges.v2.core;

import java.io.IOException;

/**
 * This interface describes the methods each workflow engine should implement.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public interface WorkflowEngine {
    /**
     * Run the workflow on this workflow engine.
     *
     * @param workflow the workflow to run.
     * @throws IOException          if reading the workflow results fails.
     * @throws InterruptedException if any thread has interrupted the current thread while waiting for the workflow
     *                              engine.
     */
    void runWorkflow(final Workflow workflow) throws InterruptedException, IOException;
}
