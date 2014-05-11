/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.biomedbridges.galaxy.metadata;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for the GalaxyWorkflowEngineMetadata class.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 * @author <a href="mailto:y.hoogstrate@erasmusmc.nl">Youri Hoogstrate</a>
 */
public class GalaxyWorkflowEngineMetadataTest {
    @Test
    public void testGetWorkflows() {
        final GalaxyWorkflowEngineMetadata metadata = new GalaxyWorkflowEngineMetadata();
        assertEquals(2, metadata.getWorkflows().size());
        final GalaxyWorkflowMetadata histogramWorkflowMetadata = metadata.getWorkflow("Histogram");
        assertEquals(2, histogramWorkflowMetadata.getSteps().size());
        assertEquals("histogram_rpy", histogramWorkflowMetadata.getSteps().get(1).getToolId());
        final GalaxyWorkflowMetadata randomLinesTwiceWorkflowMetadata = metadata.getWorkflow("RandomLinesTwice");
        assertEquals(3, randomLinesTwiceWorkflowMetadata.getSteps().size());
        assertEquals("random_lines1", randomLinesTwiceWorkflowMetadata.getSteps().get(1).getToolId());
    }
}
