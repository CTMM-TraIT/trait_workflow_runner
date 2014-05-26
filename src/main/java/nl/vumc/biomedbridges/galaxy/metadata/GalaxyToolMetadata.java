/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.biomedbridges.galaxy.metadata;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * The Galaxy tool metadata.
 *
 * At the moment, we skip the command, tests, and help elements.
 * The conditional, option, and when elements (inside the inputs element) will be added in the future.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 * @author <a href="mailto:y.hoogstrate@erasmusmc.nl">Youri Hoogstrate</a>
 */
public class GalaxyToolMetadata {
    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(GalaxyToolMetadata.class);

    /**
     * The id.
     */
    private final String id;

    /**
     * The name.
     */
    private final String name;

    /**
     * The version.
     */
    private final String version;

    /**
     * The description.
     */
    private final String description;

    /**
     * The parameters.
     */
    private final List<GalaxyToolParameterMetadata> parameters;

    /**
     * Create a Galaxy tool metadata object.
     *
     * @param toolElement the tool element.
     */
    public GalaxyToolMetadata(final Element toolElement) {
        this.id = toolElement.getAttribute("id");
        this.name = toolElement.getAttribute("name");
        this.version = toolElement.getAttribute("version");

        final NodeList descriptionElements = toolElement.getElementsByTagName("description");
        this.description = descriptionElements.getLength() >= 1 ? descriptionElements.item(0).getTextContent() : null;

        logger.info("tool id: " + id);
        logger.trace("tool name: " + name);
        logger.trace("tool version: " + version);
        logger.trace("description: " + description);

        this.parameters = new ArrayList<>();
        final NodeList inputsElements = toolElement.getElementsByTagName("inputs");
        if (inputsElements.getLength() >= 1) {
            final NodeList parameterElements = ((Element) inputsElements.item(0)).getElementsByTagName("param");
            for (int parameterIndex = 0; parameterIndex < parameterElements.getLength(); parameterIndex++)
                this.parameters.add(new GalaxyToolParameterMetadata((Element) parameterElements.item(parameterIndex)));
        }
    }

    /**
     * Get the id.
     *
     * @return the id.
     */
    public String getId() {
        return id;
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
     * Get the version.
     *
     * @return the version.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Get the description.
     *
     * @return the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the parameters.
     *
     * @return the parameters.
     */
    public List<GalaxyToolParameterMetadata> getParameters() {
        return parameters;
    }
}