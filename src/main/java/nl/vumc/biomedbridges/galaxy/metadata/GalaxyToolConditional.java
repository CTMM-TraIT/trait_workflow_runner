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
    private final GalaxyToolParameterMetadata selectorParameter;
    private final List<GalaxyToolOption> options;
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

    public GalaxyToolParameterMetadata getSelectorParameter() {
        return selectorParameter;
    }

    public List<GalaxyToolOption> getOptions() {
        return options;
    }

    public List<GalaxyToolWhen> getWhens() {
        return whens;
    }
}
