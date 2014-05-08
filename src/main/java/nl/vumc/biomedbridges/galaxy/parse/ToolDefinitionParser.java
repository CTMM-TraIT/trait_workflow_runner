/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.biomedbridges.galaxy.parse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
     * Project directory.
     */
    private static final String PROJECT_DIRECTORY = "C:\\Freek\\VUmc\\BioMedBridges\\WorkflowRunner\\";

    /**
     * Galaxy configuration data directory for testing.
     */
    private static final String DATA_DIRECTORY = PROJECT_DIRECTORY + "data\\Galaxy configuration\\";

//    /**
//     * Hidden constructor. The main method below will be used for now.
//     */
//    private ToolDefinitionParser() {
//    }

    /**
     * Main method.
     *
     * @param arguments unused command-line arguments.
     */
    // CHECKSTYLE_OFF: UncommentedMain
    public static void main(final String[] arguments) {
        // Test parsing the tools configuration.
        final String configurationFilePath = DATA_DIRECTORY + "tool_conf.xml";
        final List<String> toolDefinitionPaths = new ToolDefinitionParser().parseToolsConfiguration(configurationFilePath);
        System.out.println("toolDefinitionPaths: " + toolDefinitionPaths);
        // Test parsing a tool definition.
        final String histogramFilePath = DATA_DIRECTORY + "tools\\plotting\\histogram2.xml";
        if (arguments.length < 0)
            new ToolDefinitionParser().parseToolDefinition(histogramFilePath);
    }
    // CHECKSTYLE_ON: UncommentedMain

    public List<String> parseToolsConfiguration(final String filePath) {
        final List<String> toolDefinitionFilePaths = new ArrayList<>();
        try {
            final Element toolboxElement = parseXmlDocument(filePath);
            final NodeList sectionElements = toolboxElement.getElementsByTagName("section");
            for (int sectionIndex = 0; sectionIndex < sectionElements.getLength(); sectionIndex++) {
                final Element sectionElement = (Element) sectionElements.item(sectionIndex);
                final NodeList toolElements = sectionElement.getElementsByTagName("tool");
                for (int toolIndex = 0; toolIndex < toolElements.getLength(); toolIndex++) {
                    final Element toolElement = (Element) toolElements.item(toolIndex);
                    final String fileAttribute = toolElement.getAttribute("file");
                    toolDefinitionFilePaths.add(fileAttribute);
                    System.out.println("fileAttribute: " + fileAttribute);
                }
            }
        } catch (final SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }
        return toolDefinitionFilePaths;
    }

    /**
     * Parse the definition of a tool.
     *
     * @param filePath the file path with the xml tool definition.
     */
    private void parseToolDefinition(final String filePath) {
        System.out.println("filePath: " + filePath);
        try {
            final Element toolElement = parseXmlDocument(filePath);
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

    /**
     * Parse the XML document at the file path and return the document element.
     *
     * @param filePath the file path where the XML document is located.
     * @return the document element.
     * @throws ParserConfigurationException when the XML parser (document builder) cannot be configured.
     * @throws SAXException when there is an exception during parsing.
     * @throws IOException when there is an I/O except during reading of the XML file.
     */
    private Element parseXmlDocument(final String filePath)
            throws ParserConfigurationException, SAXException, IOException {
        final DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        return documentBuilder.parse(new File(filePath)).getDocumentElement();
    }
}
