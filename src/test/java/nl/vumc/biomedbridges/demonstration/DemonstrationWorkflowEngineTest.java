/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.biomedbridges.demonstration;

import java.io.File;
import java.math.BigInteger;

import nl.vumc.biomedbridges.core.Constants;
import nl.vumc.biomedbridges.core.FileUtils;
import nl.vumc.biomedbridges.core.Workflow;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for the DemonstrationWorkflowEngine class.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class DemonstrationWorkflowEngineTest {
    /**
     * The workflow engine to test.
     */
    private DemonstrationWorkflowEngine demonstrationWorkflowEngine;

    /**
     * The workflow to use.
     */
    private Workflow demonstrationWorkflow;

    /**
     * Set up for each unit test.
     */
    @Before
    public void setUp() {
        demonstrationWorkflowEngine = new DemonstrationWorkflowEngine();
        demonstrationWorkflow = demonstrationWorkflowEngine.getWorkflow(Constants.TEST_WORKFLOW_CONCATENATE);
    }

    /**
     * Test the runWorkflow method with an invalid parameter.
     */
    @Test
    public void testRunWorkflowInvalidParameter() {
        demonstrationWorkflow.addInput("WorkflowInput1", new BigInteger("123456"));
        demonstrationWorkflowEngine.runWorkflow(demonstrationWorkflow);
    }

    /**
     * Test the runWorkflow method with two non-existing files.
     */
    @Test
    public void testRunWorkflowNonExistingFiles() {
        demonstrationWorkflow.addInput("WorkflowInput1", new File("non-existing file 1"));
        demonstrationWorkflow.addInput("WorkflowInput2", new File("non-existing file 2"));
        demonstrationWorkflowEngine.runWorkflow(demonstrationWorkflow);
    }

    /**
     * Test the runWorkflow method with two correct parameters.
     */
    @Test
    public void testRunWorkflowCorrectParameters() {
        demonstrationWorkflow.addInput("WorkflowInput1", FileUtils.createInputFile("line 1"));
        demonstrationWorkflow.addInput("WorkflowInput2", FileUtils.createInputFile("line 2"));
        demonstrationWorkflowEngine.runWorkflow(demonstrationWorkflow);
    }
}
