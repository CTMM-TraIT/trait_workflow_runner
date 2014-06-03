/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.demonstration;

import java.io.File;
import java.math.BigInteger;

import nl.vumc.biomedbridges.core.Constants;
import nl.vumc.biomedbridges.core.FileUtils;
import nl.vumc.biomedbridges.core.Workflow;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for the DemonstrationWorkflowEngine class.
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
        assertFalse(demonstrationWorkflowEngine.runWorkflow(demonstrationWorkflow));
    }

    /**
     * Test the runWorkflow method with two non-existing files.
     */
    @Test
    public void testRunWorkflowNonExistingFiles() {
        demonstrationWorkflow.addInput("WorkflowInput1", new File("non-existing file 1"));
        demonstrationWorkflow.addInput("WorkflowInput2", new File("non-existing file 2"));
        assertFalse(demonstrationWorkflowEngine.runWorkflow(demonstrationWorkflow));
    }

    /**
     * Test the runWorkflow method with two correct parameters.
     */
    @Test
    public void testRunWorkflowCorrectParameters() {
        demonstrationWorkflow.addInput("WorkflowInput1", FileUtils.createTemporaryFile("line 1"));
        demonstrationWorkflow.addInput("WorkflowInput2", FileUtils.createTemporaryFile("line 2"));
        assertTrue(demonstrationWorkflowEngine.runWorkflow(demonstrationWorkflow));
    }

    /**
     * Test the configure method.
     */
    @Test
    public void testConfigure() {
        assertTrue(demonstrationWorkflowEngine.configure(null));
    }
}
