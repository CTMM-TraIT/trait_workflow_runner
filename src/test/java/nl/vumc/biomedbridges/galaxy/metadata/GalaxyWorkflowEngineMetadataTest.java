/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.galaxy.metadata;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for the GalaxyWorkflowEngineMetadata class.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 * @author <a href="mailto:y.hoogstrate@erasmusmc.nl">Youri Hoogstrate</a>
 */
public class GalaxyWorkflowEngineMetadataTest {
    /**
     * Test the getWorkflows method.
     */
    @Test
    public void testGetWorkflows() {
        final GalaxyWorkflowEngineMetadata metadata = new GalaxyWorkflowEngineMetadata();
        assertEquals(5, metadata.getWorkflows().size());
    }

    /**
     * Test the getWorkflow method.
     */
    @Test
    public void testGetWorkflow() {
        final GalaxyWorkflowEngineMetadata metadata = new GalaxyWorkflowEngineMetadata();
        final GalaxyWorkflowMetadata histogramWorkflowMetadata = metadata.getWorkflow("Histogram");
        assertEquals(2, histogramWorkflowMetadata.getSteps().size());
        assertEquals("histogram_rpy", histogramWorkflowMetadata.getSteps().get(1).getToolId());
        checkParameters(histogramWorkflowMetadata, Arrays.asList("input", "data", "numerical_column", "data_column",
                                                                 "breaks", "integer", "title", "text", "xlab", "text",
                                                                 "density", "boolean", "frequency", "boolean"));
        final GalaxyWorkflowMetadata randomLinesTwiceWorkflowMetadata = metadata.getWorkflow("RandomLinesTwice");
        assertEquals(3, randomLinesTwiceWorkflowMetadata.getSteps().size());
        assertEquals("random_lines1", randomLinesTwiceWorkflowMetadata.getSteps().get(1).getToolId());
        checkParameters(randomLinesTwiceWorkflowMetadata, Arrays.asList("num_lines", "integer", "input", "data",
                                                                        "num_lines", "integer", "input", "data"));
    }

    /**
     * Check the metadata of the workflow parameters.
     *
     * @param workflowMetadata the workflow metadata.
     * @param expectedMetadata the expected metadata as a list of strings, with name and type for each parameter.
     */
    private void checkParameters(final GalaxyWorkflowMetadata workflowMetadata, final List<String> expectedMetadata) {
        for (int parameterIndex = 0; parameterIndex < workflowMetadata.getParameters().size(); parameterIndex++) {
            final GalaxyToolParameterMetadata parameterMetadata = workflowMetadata.getParameters().get(parameterIndex);
            assertEquals(expectedMetadata.get(2 * parameterIndex), parameterMetadata.getName());
            assertEquals(expectedMetadata.get(2 * parameterIndex + 1), parameterMetadata.getType());
        }
    }
}
