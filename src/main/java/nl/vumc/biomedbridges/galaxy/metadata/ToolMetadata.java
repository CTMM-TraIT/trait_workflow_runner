package nl.vumc.biomedbridges.galaxy.metadata;

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
    public ToolMetadata(final Element toolElement) {
        System.out.println("tool id: " + toolElement.getAttribute("id"));
        System.out.println("tool name: " + toolElement.getAttribute("name"));
        System.out.println("tool version: " + toolElement.getAttribute("version"));
        final NodeList descriptionElements = toolElement.getElementsByTagName("description");
        if (descriptionElements.getLength() >= 1)
            System.out.println("description: " + descriptionElements.item(0).getTextContent());
        final NodeList inputsElements = toolElement.getElementsByTagName("inputs");
        if (inputsElements.getLength() >= 1) {
            final NodeList paramElements = ((Element) inputsElements.item(0)).getElementsByTagName("param");
            for (int paramIndex = 0; paramIndex < paramElements.getLength(); paramIndex++) {
                final Element paramElement = (Element) paramElements.item(paramIndex);
                final NamedNodeMap paramAttributes = paramElement.getAttributes();
                System.out.println();
                for (int attributeIndex = 0; attributeIndex < paramAttributes.getLength(); attributeIndex++) {
                    final String attributeName = paramAttributes.item(attributeIndex).getNodeName();
                    final String attributeValue = paramAttributes.item(attributeIndex).getNodeValue();
                    System.out.println("parameter " + attributeName + ": " + attributeValue);
                }
            }
        }
    }
}
