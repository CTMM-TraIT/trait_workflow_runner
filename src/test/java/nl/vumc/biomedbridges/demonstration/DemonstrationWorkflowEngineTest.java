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
     * The concatenate workflow.
     */
    private Workflow concatenateWorkflow;

    /**
     * The "random lines twice" workflow.
     */
    private Workflow randomLinesWorkflow;

    /**
     * Set up for each unit test.
     */
    @Before
    public void setUp() {
        demonstrationWorkflowEngine = new DemonstrationWorkflowEngine();
        concatenateWorkflow = demonstrationWorkflowEngine.getWorkflow(Constants.TEST_WORKFLOW_CONCATENATE);
        randomLinesWorkflow = demonstrationWorkflowEngine.getWorkflow(Constants.WORKFLOW_RANDOM_LINES_TWICE);
    }

    /**
     * Test the configure method.
     */
    @Test
    public void testConfigure() {
        assertTrue(demonstrationWorkflowEngine.configure());
        assertTrue(demonstrationWorkflowEngine.configure(null));
    }

    /**
     * Test the runWorkflow method with an invalid parameter.
     */
    @Test
    public void testRunWorkflowInvalidParameter() {
        concatenateWorkflow.addInput("WorkflowInput1", new BigInteger("123456"));
        assertFalse(demonstrationWorkflowEngine.runWorkflow(concatenateWorkflow));

        randomLinesWorkflow.addInput("Input Dataset", new BigInteger("654321"));
        assertFalse(demonstrationWorkflowEngine.runWorkflow(randomLinesWorkflow));
    }

    /**
     * Test the runWorkflow method with two non-existing files.
     */
    @Test
    public void testRunWorkflowNonExistingFiles() {
        concatenateWorkflow.addInput("WorkflowInput1", new File("non-existing file 1"));
        concatenateWorkflow.addInput("WorkflowInput2", new File("non-existing file 2"));
        assertFalse(demonstrationWorkflowEngine.runWorkflow(concatenateWorkflow));

        setRandomLinesInputAndParameters(new File("non-existing file 3"));
        assertFalse(demonstrationWorkflowEngine.runWorkflow(randomLinesWorkflow));
    }

    /**
     * Test the runWorkflow method with two correct parameters.
     */
    @Test
    public void testRunWorkflowCorrectParameters() {
        concatenateWorkflow.addInput("WorkflowInput1", FileUtils.createTemporaryFile("line 1"));
        concatenateWorkflow.addInput("WorkflowInput2", FileUtils.createTemporaryFile("line 2"));
        assertTrue(demonstrationWorkflowEngine.runWorkflow(concatenateWorkflow));

        setRandomLinesInputAndParameters(FileUtils.createTemporaryFile("line a", "line b", "line c", "line d"));
        assertTrue(demonstrationWorkflowEngine.runWorkflow(randomLinesWorkflow));
    }

    /**
     * Set the input file and the parameters of the "random lines twice" workflow.
     *
     * @param inputFile the input file.
     */
    private void setRandomLinesInputAndParameters(final File inputFile) {
        final int stepNumber2 = 2;
        final int stepNumber3 = 3;
        final String numberOfLinesParameter = "num_lines";
        randomLinesWorkflow.addInput("Input Dataset", inputFile);
        randomLinesWorkflow.setParameter(stepNumber2, numberOfLinesParameter, 1);
        randomLinesWorkflow.setParameter(stepNumber3, numberOfLinesParameter, 6);
    }
}
