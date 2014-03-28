/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.biomedbridges.v2.demonstration;

import java.io.File;
import java.math.BigInteger;

import nl.vumc.biomedbridges.v2.core.FileUtils;

import org.junit.Test;

/**
 * Unit test for the DemonstrationWorkflowEngine class.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class DemonstrationWorkflowEngineTest {
    /**
     * Test the runWorkflow method with an invalid parameter.
     */
    @Test
    public void testRunWorkflowInvalidParameter() {
        final DemonstrationWorkflowEngine demonstrationWorkflowEngine = new DemonstrationWorkflowEngine();
        final DemonstrationWorkflow demonstrationWorkflow = new DemonstrationWorkflow("TestWorkflowConcatenate");
        demonstrationWorkflow.addInput("input1", new BigInteger("123456"));
        demonstrationWorkflowEngine.runWorkflow(demonstrationWorkflow);
    }

    /**
     * Test the runWorkflow method with two non-existing files.
     */
    @Test
    public void testRunWorkflowNonExistingFiles() {
        final DemonstrationWorkflowEngine demonstrationWorkflowEngine = new DemonstrationWorkflowEngine();
        final DemonstrationWorkflow demonstrationWorkflow = new DemonstrationWorkflow("TestWorkflowConcatenate");
        demonstrationWorkflow.addInput("input1", new File("non-existing file 1"));
        demonstrationWorkflow.addInput("input2", new File("non-existing file 2"));
        demonstrationWorkflowEngine.runWorkflow(demonstrationWorkflow);
    }

    /**
     * Test the runWorkflow method with two correct parameters.
     */
    @Test
    public void testRunWorkflowCorrectParameters() {
        final DemonstrationWorkflowEngine demonstrationWorkflowEngine = new DemonstrationWorkflowEngine();
        final DemonstrationWorkflow demonstrationWorkflow = new DemonstrationWorkflow("TestWorkflowConcatenate");
        demonstrationWorkflow.addInput("input1", FileUtils.createInputFile("line 1"));
        demonstrationWorkflow.addInput("input2", FileUtils.createInputFile("line 2"));
        demonstrationWorkflowEngine.runWorkflow(demonstrationWorkflow);
    }
}
