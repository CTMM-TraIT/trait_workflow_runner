/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.biomedbridges.galaxy;

import com.github.jmchilton.blend4j.galaxy.beans.WorkflowDetails;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputDefinition;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

import org.junit.Test;

import static com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs.InputSourceType;
import static com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs.WorkflowInput;
import static org.junit.Assert.assertEquals;

/**
 * This class contains a unit test for the WorkflowUtils class.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class WorkflowUtilsTest {
    /**
     * Test the setInputByLabel method.
     */
    @Test
    public void testSetInputByLabel() {
        final String label = "some-label";
        final WorkflowInputDefinition workflowInputDefinition = new WorkflowInputDefinition();
        final WorkflowDetails workflowDetails = new WorkflowDetails();
        final WorkflowInputs workflowInputs = new WorkflowInputs();
        final WorkflowInput inputValue = new WorkflowInput("input-id", InputSourceType.HDA);
        workflowInputDefinition.setLabel(label);
        workflowDetails.setInputs(ImmutableMap.of(label, workflowInputDefinition));

        WorkflowUtils.setInputByLabel(label, workflowDetails, workflowInputs, inputValue);

        final Map<String, WorkflowInput> expectedWorkflowInputs = ImmutableMap.of(label, inputValue);
        assertEquals(expectedWorkflowInputs, workflowInputs.getInputs());
    }
}
