/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.core;

import java.io.IOException;

/**
 * Dummy workflow for testing purposes.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class DummyWorkflow extends BaseWorkflow {
    /**
     * The value that will be returned by the run method.
     */
    private static boolean returnedResult = true;

    /**
     * Whether an exception should be thrown by the run method.
     */
    private static boolean throwException;

    /**
     * Set the value that will be returned by the run method.
     *
     * @param returnedResult the value that will be returned by the run method.
     */
    public static void setReturnedResult(final boolean returnedResult) {
        DummyWorkflow.returnedResult = returnedResult;
    }

    /**
     * Set whether an exception should be thrown by the run method.
     *
     * @param throwException whether an exception should be thrown by the run method.
     */
    public static void setThrowException(final boolean throwException) {
        DummyWorkflow.throwException = throwException;
    }

    /**
     * Create a dummy workflow.
     *
     * @param name the workflow name.
     */
    public DummyWorkflow(final String name) {
        super(name);
    }

    @Override
    public boolean run() throws IOException, InterruptedException {
        // todo: can this be moved to the test (see HistogramExampleTest)?
        if (Constants.WORKFLOW_RNA_SEQ_DGE.equals(getName()))
            for (int outputIndex = 0; outputIndex < 7; outputIndex++)
                addOutput("dummy-output-" + (outputIndex + 1), "dummy");
        // todo: can this be moved to the test (see HistogramExampleTest)?

        if (throwException)
            throw new IOException("DummyWorkflow.run");

        return returnedResult;
    }
}
