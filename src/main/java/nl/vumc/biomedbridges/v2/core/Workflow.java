/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.biomedbridges.v2.core;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * This interface describes the methods each workflow should implement.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public interface Workflow {
    /**
     * Get the workflow name.
     *
     * @return the workflow name.
     */
    String getName();

    /**
     * Add an actual input object.
     *
     * @param inputName the input name.
     * @param inputObject the input object.
     */
    void addInput(final String inputName, final Object inputObject);

    /**
     * Retrieve an input object.
     *
     * @param inputName the input name.
     * @return the input object.
     */
    Object getInput(final String inputName);

    Collection<Object> getAllInputValues();

    Set<Map.Entry<String, Object>> getAllInputEntries();

    /**
     * Add an actual output object.
     *
     * @param outputName the output name.
     * @param outputObject the output object.
     */
    void addOutput(final String outputName, final Object outputObject);

    /**
     * Retrieve an output object.
     *
     * @param outputName the output name.
     * @return the output object.
     */
    Object getOutput(final String outputName);
}
