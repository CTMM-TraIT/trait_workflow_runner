/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.biomedbridges.galaxy.parse;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * The parser for the Galaxy tool definitions.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class ToolDefinitionParser {
    /**
     * Hidden constructor. The main method below will be used for now.
     */
    private ToolDefinitionParser() {
    }

    /**
     * Main method.
     *
     * @param arguments unused command-line arguments.
     */
    // CHECKSTYLE_OFF: UncommentedMain
    public static void main(final String[] arguments) {
        final String filePath = "C:\\Freek\\VUmc\\BioMedBridges\\WorkflowRunner\\etc\\histogram\\histogram2.xml";
        new ToolDefinitionParser().parseToolDefinition(filePath);
    }
    // CHECKSTYLE_ON: UncommentedMain

    /**
     * Parse the definition of a tool.
     *
     * @param filePath the file path with the xml tool definition.
     */
    private void parseToolDefinition(final String filePath) {
        System.out.println("filePath: " + filePath);
        try {
            final DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            final Element toolElement = documentBuilder.parse(new File(filePath)).getDocumentElement();
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
        } catch (final SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }
    }
}
