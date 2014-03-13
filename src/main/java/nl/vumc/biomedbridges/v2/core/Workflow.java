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
    String getName();

    void addInput(final String inputName, final Object inputObject);

    Object getInput(final String inputName);

    Collection<Object> getAllInputValues();

    Set<Map.Entry<String, Object>> getAllInputEntries();

    void addOutput(final String outputName, final Object outputObject);

    Object getOutput(final String outputName);
}
