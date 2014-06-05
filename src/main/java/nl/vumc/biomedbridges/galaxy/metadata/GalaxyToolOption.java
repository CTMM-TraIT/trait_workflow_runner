/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.galaxy.metadata;

import org.w3c.dom.Element;

/**
 * The Galaxy tool option metadata (related to a conditional parameter; see the GalaxyToolConditional class).
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class GalaxyToolOption {
    /**
     * The text.
     */
    private final String text;

    /**
     * The option identifier.
     */
    private final String value;

    /**
     * Whether this option is selected by default.
     */
    private final boolean selected;

    /**
     * Create a Galaxy tool option metadata object (related to a conditional parameter).
     *
     * @param optionElement the conditional element.
     */
    public GalaxyToolOption(final Element optionElement) {
        this.text = optionElement.getTextContent();
        this.value = optionElement.getAttribute("value");
        this.selected = Boolean.parseBoolean(optionElement.getAttribute("selected"));
    }

    /**
     * Get the text.
     *
     * @return the text.
     */
    public String getText() {
        return text;
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
     * Is this option selected by default.
     *
     * @return whether this option is selected by default.
     */
    public boolean isSelected() {
        return selected;
    }
}
