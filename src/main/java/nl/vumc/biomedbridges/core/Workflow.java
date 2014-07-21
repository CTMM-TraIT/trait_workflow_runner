/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.core;

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

    /**
     * Get all the input values.
     *
     * @return all the input values.
     */
    Collection<Object> getAllInputValues();

    /**
     * Get all the input key-value pairs.
     *
     * @return all the input entries.
     */
    Set<Map.Entry<String, Object>> getAllInputEntries();

    /**
     * Get all the inputs in a map.
     *
     * @return the map with all inputs.
     */
    Map<String, Object> getInputMap();

    /**
     * Get the directory where output files should be downloaded. If this directory is not set, files will be downloaded
     * in a temporary directory.
     *
     * @return the directory where output files should be downloaded or null (use temporary directory).
     */
    String getDownloadDirectory();

    /**
     * Set the directory where output files should be downloaded. If this directory is not set, files will be downloaded
     * in a temporary directory.
     *
     * @param downloadDirectory the directory where output files should be downloaded or null.
     */
    void setDownloadDirectory(final String downloadDirectory);

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

    /**
     * Get all the outputs in a map.
     *
     * @return the map with all outputs.
     */
    Map<String, Object> getOutputMap();

    /**
     * Set a workflow parameter.
     *
     * @param stepId the Galaxy step id.
     * @param name the parameter name.
     * @param value the parameter value.
     */
    // todo: make parameter handling independent of Galaxy?
    void setParameter(final int stepId, String name, Object value);

    /**
     * Retrieve all workflow parameters.
     *
     * @return the workflow parameters.
     */
    // todo: make parameter handling independent of Galaxy?
    Map<Object, Map<String, Object>> getParameters();
}
