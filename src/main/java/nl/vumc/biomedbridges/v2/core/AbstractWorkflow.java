/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.biomedbridges.v2.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class implements some methods of the Workflow interface and is used as a base class for full implementations of
 * this interface.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public abstract class AbstractWorkflow implements Workflow {
    protected final String name;
    protected final Map<String, Object> inputs = new HashMap<>();
    protected final Map<String, Object> outputs = new HashMap<>();

    protected AbstractWorkflow(final String name) {
        this.name = name;
    }

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
    public void addOutput(final String outputName, final Object outputValue) {
        outputs.put(outputName, outputValue);
    }

    @Override
    public Object getOutput(final String outputName) {
        return outputs.get(outputName);
    }
}
