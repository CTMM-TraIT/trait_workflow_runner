/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.biomedbridges.v1;

import com.github.jmchilton.blend4j.galaxy.beans.WorkflowDetails;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputDefinition;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs;

import static com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs.WorkflowInput;

import static java.util.Map.Entry;

/**
 * Simple utility method for working with Galaxy workflows.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class WorkflowUtils {
    /**
     * Hidden constructor. Only the static method of this class are meant to be used.
     */
    private WorkflowUtils() {
    }

    /**
     * Set a workflow input using the input label and the workflow details to search for the input ID.
     *
     * @param label      the input label.
     * @param details    the workflow details.
     * @param inputs     the workflow inputs object.
     * @param inputValue the workflow input value.
     */
    public static void setInputByLabel(final String label, final WorkflowDetails details, final WorkflowInputs inputs,
                                       final WorkflowInput inputValue) {
        for (final Entry<String, WorkflowInputDefinition> inputEntry : details.getInputs().entrySet())
            if (inputEntry.getValue().getLabel().equals(label)) {
                inputs.setInput(inputEntry.getKey(), inputValue);
                break;
            }
    }
}
