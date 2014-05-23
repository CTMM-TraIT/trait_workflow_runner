/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.biomedbridges.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class provides a default implementation of the Workflow interface and is used as a base class by specific
 * implementations of the Workflow interface.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class BaseWorkflow implements Workflow {
    /**
     * The name of the workflow.
     */
    private final String name;

    /**
     * The mapping of the input names to the actual input objects.
     */
    private final Map<String, Object> inputs = new HashMap<>();

    /**
     * The mapping of the parameter names to the actual parameter objects. For the moment, the parameters are stored in
     * a map for each Galaxy step id.
     */
    // todo: make parameter handling independent of Galaxy?
    private final Map<Object, Map<String, Object>> parameters = new HashMap<>();

    /**
     * The mapping of the output names to the actual output objects.
     */
    private final Map<String, Object> outputs = new HashMap<>();

    /**
     * Construct a base workflow. This is only meant to be used by subclasses.
     *
     * @param name the name of the workflow.
     */
    protected BaseWorkflow(final String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void addInput(final String inputName, final Object inputValue) {
        inputs.put(inputName, inputValue);
    }

    @Override
    public Object getInput(final String inputName) {
        return inputs.get(inputName);
    }

    @Override
    public Collection<Object> getAllInputValues() {
        return inputs.values();
    }

    @Override
    public Set<Map.Entry<String, Object>> getAllInputEntries() {
        return inputs.entrySet();
    }

    @Override
    public Map<String, Object> getInputMap() {
        return new HashMap<>(inputs);
    }

    @Override
    public void addOutput(final String outputName, final Object outputValue) {
        outputs.put(outputName, outputValue);
    }

    @Override
    public Object getOutput(final String outputName) {
        return outputs.get(outputName);
    }

    @Override
    public Map<String, Object> getOutputMap() {
        return new HashMap<>(outputs);
    }

    @Override
    // todo: make setting parameters independent of Galaxy?
    public void setParameter(final int stepId, final String name, final Object value) {
        Map<String, Object> keyValueMap = parameters.get(stepId);
        if (keyValueMap == null)
            keyValueMap = new HashMap<>();
        keyValueMap.put(name, value);
        parameters.put(stepId, keyValueMap);
    }

    @Override
    public Map<Object, Map<String, Object>> getParameters() {
        return parameters;
    }
}
