/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.biomedbridges.galaxy.metadata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

/**
 * The Galaxy tool parameter metadata.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 * @author <a href="mailto:y.hoogstrate@erasmusmc.nl">Youri Hoogstrate</a>
 */
public class GalaxyToolParameterMetadata {
    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(GalaxyToolParameterMetadata.class);

    private final String name;
    private final String label;
    private final String type;
    private final String size;
    private final String format;
    private final String value;
    private final String help;

    public GalaxyToolParameterMetadata(final Element parameterElement) {
        this.name = parameterElement.getAttribute("name");
        this.label = parameterElement.getAttribute("label");
        this.type = parameterElement.getAttribute("type");
        this.size = parameterElement.getAttribute("size");
        this.format = parameterElement.getAttribute("format");
        this.value = parameterElement.getAttribute("value");
        this.help = parameterElement.getAttribute("help");
        logger.trace("");
        logger.info("Constructed {} parameter.", this.name);
        final NamedNodeMap paramAttributes = parameterElement.getAttributes();
        for (int attributeIndex = 0; attributeIndex < paramAttributes.getLength(); attributeIndex++) {
            final String attributeName = paramAttributes.item(attributeIndex).getNodeName();
            final String attributeValue = paramAttributes.item(attributeIndex).getNodeValue();
            logger.trace("parameter attribute " + attributeName + ": " + attributeValue);
        }
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }

    public String getType() {
        return type;
    }

    public String getSize() {
        return size;
    }

    public String getFormat() {
        return format;
    }

    public String getValue() {
        return value;
    }

    public String getHelp() {
        return help;
    }
}
