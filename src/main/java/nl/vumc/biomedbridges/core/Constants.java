/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.biomedbridges.core;

/**
 * This class contains several general constants.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class Constants {
    /**
     * The name of the first concatenate workflow.
     */
    public static final String TEST_WORKFLOW_CONCATENATE = "TestWorkflowConcatenate";

    /**
     * The name of the scatterplot test workflow.
     */
    public static final String TEST_WORKFLOW_SCATTERPLOT = "TestWorkflowScatterplot";

    /**
     * The name of the histogram test workflow.
     */
    public static final String TEST_WORKFLOW_HISTOGRAM = "Histogram";

    /**
     * The name of the current test workflow.
     */
    public static final String TEST_WORKFLOW_NAME = TEST_WORKFLOW_SCATTERPLOT;

    /**
     * Hidden constructor. Only the static fields of this class are meant to be used.
     */
    private Constants() {
    }
}
