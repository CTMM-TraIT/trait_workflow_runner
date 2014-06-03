/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.galaxy.metadata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

/**
 * The Galaxy tool parameter metadata.
 *
 * todo: Decide whether the Workflow Runner will only support a subset of all Galaxy workflow and tool functionality.
 * - Some functionality might not be supported by the Galaxy API and/or blend4j.
 * - Some functionality (like conditional parameters) will be too complicated to be supported by Workflow Runner version
 *   1 (and/or the tranSMART workflow plugin version 1).
 * - See galaxy-central-repository/lib/galaxy/tools/parameters/*.py for more details.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 * @author <a href="mailto:y.hoogstrate@erasmusmc.nl">Youri Hoogstrate</a>
 */
public class GalaxyToolParameterMetadata {
    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(GalaxyToolParameterMetadata.class);

    /**
     * The name.
     */
    private final String name;

    /**
     * The label.
     */
    private final String label;

    /**
     * The type.
     */
    private final String type;

    /**
     * The size.
     */
    private final String size;

    /**
     * The format.
     */
    private final String format;

    /**
     * The value.
     */
    private final String value;

    /**
     * The help text explaining this parameter.
     */
    private final String help;

    /**
     * Create a Galaxy tool parameter metadata object.
     *
     * @param parameterElement the parameter element.
     */
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

    /**
     * Get the name.
     *
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the label.
     *
     * @return the label.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Get the type.
     *
     * @return the type.
     */
    public String getType() {
        return type;
    }

    /**
     * Get the size.
     *
     * @return the size.
     */
    public String getSize() {
        return size;
    }

    /**
     * Get the format.
     *
     * @return the format.
     */
    public String getFormat() {
        return format;
    }

    /**
     * Get the value.
     *
     * @return the value.
     */
    public String getValue() {
        return value;
    }

    /**
     * Get the help text explaining this parameter.
     *
     * @return the help text.
     */
    public String getHelp() {
        return help;
    }
}
