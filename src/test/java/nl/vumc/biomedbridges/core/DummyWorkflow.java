package nl.vumc.biomedbridges.core;

import java.util.HashMap;
import java.util.Map;

public class DummyWorkflow extends BaseWorkflow {
    /**
     *
     */
    private final Map<String, Object> outputMap;

    /**
     *
     *
     * @param name
     */
    public DummyWorkflow(final String name) {
        super(name);
        outputMap = new HashMap<>();
    }

    @Override
    public Map<String, Object> getOutputMap() {
        return outputMap;
    }

    /**
     *
     *
     * @param outputName
     * @param outputValue
     */
    public void addToOutputMap(final String outputName, final Object outputValue) {
        outputMap.put(outputName, outputValue);
    }
}
