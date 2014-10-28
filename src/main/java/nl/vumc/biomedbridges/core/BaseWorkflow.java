/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.core;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class provides a default implementation of the Workflow interface and is used as a base class by specific
 * implementations of the Workflow interface. For testing purposes, it is also used directly.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class BaseWorkflow implements Workflow {
    /**
     * The mapping of the output names to the actual output objects.
     */
    protected final Map<String, Object> outputFiles = new HashMap<>();

    /**
     * The name of the workflow.
     */
    private final String name;

    /**
     * The mapping of the input names to the actual input objects.
     */
    private final Map<String, Object> inputFiles = new HashMap<>();

    /**
     * The mapping of the parameter names to the actual parameter objects. For the moment, the parameters are stored in
     * a map for each Galaxy step id.
     */
    // todo: make parameter handling independent of Galaxy?
    private final Map<Object, Map<String, Object>> parameters = new HashMap<>();

    /**
     * Whether all output files should be downloaded automatically after the workflow has finished.
     */
    private boolean automaticDownload;

    /**
     * The directory where output files should be downloaded. If this directory is not set, files will be downloaded
     * in a temporary directory.
     */
    private String downloadDirectory;

    /**
     * Construct a base workflow. This is only meant to be used by subclasses.
     *
     * @param name the name of the workflow.
     */
    protected BaseWorkflow(final String name) {
        this.name = name;
        // todo: set/leave automaticDownload false by default (change later to make sure current code is not breaking).
        this.automaticDownload = true;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void addInput(final String inputName, final Object inputValue) {
        inputFiles.put(inputName, inputValue);
    }

    @Override
    public Object getInput(final String inputName) {
        return inputFiles.get(inputName);
    }

    @Override
    public Collection<Object> getAllInputValues() {
        return inputFiles.values();
    }

    @Override
    public Set<Map.Entry<String, Object>> getAllInputEntries() {
        return inputFiles.entrySet();
    }

    @Override
    public Map<String, Object> getInputMap() {
        return new HashMap<>(inputFiles);
    }

    @Override
    public boolean getAutomaticDownload() {
        return automaticDownload;
    }

    @Override
    public void setAutomaticDownload(final boolean automaticDownload) {
        this.automaticDownload = automaticDownload;
    }

    @Override
    public String getDownloadDirectory() {
        return downloadDirectory;
    }

    @Override
    public void setDownloadDirectory(final String downloadDirectory) {
        this.downloadDirectory = downloadDirectory;
    }

    @Override
    public void addOutput(final String outputName, final Object outputValue) {
        outputFiles.put(outputName, outputValue);
    }

    @Override
    public Object getOutput(final String outputName) {
        return outputFiles.get(outputName);
    }

    @Override
    public Map<String, Object> getOutputMap() {
        return new HashMap<>(outputFiles);
    }

    @Override
    // todo: make setting parameters independent of Galaxy?
    public void setParameter(final int stepNumber, final String name, final Object value) {
        Map<String, Object> keyValueMap = parameters.get(stepNumber);
        if (keyValueMap == null)
            keyValueMap = new HashMap<>();
        keyValueMap.put(name, value);
        parameters.put(stepNumber, keyValueMap);
    }

    @Override
    public Map<Object, Map<String, Object>> getParameters() {
        return parameters;
    }

    @Override
    public boolean run() throws IOException, InterruptedException {
        return true;
    }
}
