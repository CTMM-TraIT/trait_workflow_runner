/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.galaxy.metadata;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * The Galaxy tool when metadata (related to a conditional parameter; see the GalaxyToolConditional class).
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class GalaxyToolWhen {
    /**
     * The option identifier.
     */
    private final String value;

    /**
     * The parameters that will be effective when this option/when is active.
     */
    private final List<GalaxyToolParameterMetadata> parameters;

    /**
     * Create a Galaxy tool when metadata object (related to a conditional parameter).
     *
     * @param optionElement the conditional element.
     */
    public GalaxyToolWhen(final Element optionElement) {
        this.value = optionElement.getAttribute("value");
        final NodeList parameterElements = optionElement.getElementsByTagName("param");
        this.parameters = new ArrayList<>();
        for (int parameterIndex = 0; parameterIndex < parameterElements.getLength(); parameterIndex++)
            this.parameters.add(new GalaxyToolParameterMetadata((Element) parameterElements.item(parameterIndex)));
    }

    /**
     * Get the option identifier.
     *
     * @return the option identifier.
     */
    public String getValue() {
        return value;
    }

    /**
     * Get the parameters that will be effective when this option/when is active.
     *
     * @return whether this option is selected by default.
     */
    public List<GalaxyToolParameterMetadata> getParameters() {
        return parameters;
    }
}
