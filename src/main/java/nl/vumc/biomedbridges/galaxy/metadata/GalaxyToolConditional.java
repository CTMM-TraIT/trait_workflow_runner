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
 * The Galaxy tool conditional (parameter) metadata.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class GalaxyToolConditional {
    /**
     * The selector parameter (which contains the available options).
     */
    private final GalaxyToolParameterMetadata selectorParameter;

    /**
     * The options (related to the selector parameter).
     */
    private final List<GalaxyToolOption> options;

    /**
     * The whens: the option values and zero or more parameters for each option value.
     */
    private final List<GalaxyToolWhen> whens;

    /**
     * Create a Galaxy tool conditional (parameter) metadata object.
     *
     * @param conditionalElement the conditional element.
     */
    public GalaxyToolConditional(final Element conditionalElement) {
        final NodeList selectorParameterElements = conditionalElement.getElementsByTagName("param");
        if (selectorParameterElements.getLength() > 0) {
            // Add selector parameter.
            final Element selectorParameterElement = (Element) selectorParameterElements.item(0);
            this.selectorParameter = new GalaxyToolParameterMetadata(selectorParameterElement);
            // Add options.
            final NodeList optionElements = selectorParameterElement.getElementsByTagName("option");
            this.options = new ArrayList<>();
            for (int optionIndex = 0; optionIndex < optionElements.getLength(); optionIndex++)
                this.options.add(new GalaxyToolOption((Element) optionElements.item(optionIndex)));
            // Add whens.
            final NodeList whenElements = conditionalElement.getElementsByTagName("when");
            this.whens = new ArrayList<>();
            for (int whenIndex = 0; whenIndex < whenElements.getLength(); whenIndex++)
                this.whens.add(new GalaxyToolWhen((Element) whenElements.item(whenIndex)));
        } else {
            this.selectorParameter = null;
            this.options = null;
            this.whens = null;
        }
    }

    /**
     * Get the selector parameter (which contains the available options).
     *
     * @return the selector parameter.
     */
    public GalaxyToolParameterMetadata getSelectorParameter() {
        return selectorParameter;
    }

    /**
     * Get the options (which are related to the selector parameter).
     *
     * @return the options.
     */
    public List<GalaxyToolOption> getOptions() {
        return options;
    }

    /**
     * Get the whens: the option values and zero or more parameters for each option value.
     *
     * @return the whens.
     */
    public List<GalaxyToolWhen> getWhens() {
        return whens;
    }
}
