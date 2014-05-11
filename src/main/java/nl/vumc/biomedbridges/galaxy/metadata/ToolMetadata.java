package nl.vumc.biomedbridges.galaxy.metadata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

/**
 * The Galaxy tool metadata.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 * @author <a href="mailto:y.hoogstrate@erasmusmc.nl">Youri Hoogstrate</a>
 */
public class ToolMetadata {
    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(ToolMetadata.class);

    public ToolMetadata(final Element toolElement) {
        logger.trace("tool id: " + toolElement.getAttribute("id"));
        logger.trace("tool name: " + toolElement.getAttribute("name"));
        logger.trace("tool version: " + toolElement.getAttribute("version"));
        final NodeList descriptionElements = toolElement.getElementsByTagName("description");
        if (descriptionElements.getLength() >= 1)
            logger.trace("description: " + descriptionElements.item(0).getTextContent());
        final NodeList inputsElements = toolElement.getElementsByTagName("inputs");
        if (inputsElements.getLength() >= 1) {
            final NodeList paramElements = ((Element) inputsElements.item(0)).getElementsByTagName("param");
            for (int paramIndex = 0; paramIndex < paramElements.getLength(); paramIndex++) {
                final Element paramElement = (Element) paramElements.item(paramIndex);
                final NamedNodeMap paramAttributes = paramElement.getAttributes();
                logger.trace("");
                for (int attributeIndex = 0; attributeIndex < paramAttributes.getLength(); attributeIndex++) {
                    final String attributeName = paramAttributes.item(attributeIndex).getNodeName();
                    final String attributeValue = paramAttributes.item(attributeIndex).getNodeValue();
                    logger.trace("parameter " + attributeName + ": " + attributeValue);
                }
            }
        }
    }
}
