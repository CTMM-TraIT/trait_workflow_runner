/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.biomedbridges.core;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for the DefaultWorkflow class.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class DefaultWorkflowTest {
    /**
     * Test workflow name.
     */
    private static final String WORKFLOW_NAME = "name";

    /**
     * Test workflow.
     */
    private DefaultWorkflow defaultWorkflow;

    /**
     * Initialize the test workflow.
     */
    @Before
    public void setUp() {
        defaultWorkflow = new DefaultWorkflow(WORKFLOW_NAME);
    }

    /**
     * Test the addInput and getInput methods.
     */
    @Test
    public void testAddAndGetInput() {
        final String inputKey1 = "input1";
        final File inputFile1 = new File("input file 1");
        defaultWorkflow.addInput(inputKey1, inputFile1);
        assertEquals(inputFile1, defaultWorkflow.getInput(inputKey1));
    }

    /**
     * Test the getAllInputValues and getInputMap methods.
     */
    @Test
    public void testGetAllInputValuesAndGetInputMap() {
        final String inputKey1 = "input1";
        final String inputKey2 = "input2";
        final Object inputFile1 = new File("input file 1");
        final Object inputFile2 = new File("input file 2");

        defaultWorkflow.addInput(inputKey1, inputFile1);
        defaultWorkflow.addInput(inputKey2, inputFile2);

        final Set<Object> allInputValues = new HashSet<>(defaultWorkflow.getAllInputValues());
        assertEquals(ImmutableSet.of(inputFile1, inputFile2), allInputValues);
        assertEquals(ImmutableMap.of(inputKey1, inputFile1, inputKey2, inputFile2), defaultWorkflow.getInputMap());
    }

    /**
     * Test the addOutput and getOutput methods.
     */
    @Test
    public void testAddAndGetOutput() {
        final String outputKey1 = "output1";
        final File outputFile1 = new File("output file 1");
        defaultWorkflow.addOutput(outputKey1, outputFile1);
        assertEquals(outputFile1, defaultWorkflow.getOutput(outputKey1));
    }

    /**
     * Test the getOutputMap method.
     */
    @Test
    public void testGetOutputMap() {
        final String outputKey1 = "output1";
        final String outputKey2 = "output2";
        final Object outputFile1 = new File("output file 1");
        final Object outputFile2 = new File("output file 2");

        defaultWorkflow.addOutput(outputKey1, outputFile1);
        defaultWorkflow.addOutput(outputKey2, outputFile2);

        assertEquals(ImmutableMap.of(outputKey1, outputFile1, outputKey2, outputFile2), defaultWorkflow.getOutputMap());
    }
}
