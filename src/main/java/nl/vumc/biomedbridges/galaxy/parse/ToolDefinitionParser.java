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

import nl.vumc.biomedbridges.galaxy.metadata.ToolMetadata;
import nl.vumc.biomedbridges.galaxy.metadata.ToolReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * The parser for the Galaxy tool definitions.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class ToolDefinitionParser {
    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(ToolDefinitionParser.class);

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
        logger.trace("toolDefinitionPaths: " + toolDefinitionPaths);
//        // Test parsing a tool definition.
//        final String histogramFilePath = DATA_DIRECTORY + "tools\\plotting\\histogram2.xml";
//        if (arguments.length < 0)
//            new ToolDefinitionParser().parseToolDefinition(histogramFilePath, null);
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
                    final String toolFilePath = DATA_DIRECTORY + "tools\\" + fileAttribute.replaceAll("/", "\\\\");
                    logger.trace("toolFilePath: " + toolFilePath);
                    toolDefinitionFilePaths.add(toolFilePath);
                }
            }
        } catch (final SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }
        return toolDefinitionFilePaths;
    }

    public List<ToolMetadata> parseToolsMetadata(final List<String> toolDefinitionPaths,
                                                 final List<ToolReference> toolReferences) {
        final List<ToolMetadata> toolsMetadata = new ArrayList<>();
        for (final String toolDefinitionPath : toolDefinitionPaths) {
            final ToolMetadata toolMetadata = parseToolDefinition(toolDefinitionPath, toolReferences);
            if (toolMetadata != null)
                toolsMetadata.add(toolMetadata);
        }
        return toolsMetadata;
    }

    /**
     * Parse the definition of a tool.
     *
     * @param filePath the file path with the xml tool definition.
     * @param toolReferences the references to the tools that should be parsed.
     */
    private ToolMetadata parseToolDefinition(final String filePath, final List<ToolReference> toolReferences) {
        logger.trace("filePath: " + filePath);
        ToolMetadata toolMetadata = null;
        try {
            final Element toolElement = parseXmlDocument(filePath);
            if (toolToBeParsed(toolReferences, toolElement))
                toolMetadata = new ToolMetadata(toolElement);
        } catch (final SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }
        return toolMetadata;
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

    private boolean toolToBeParsed(final List<ToolReference> toolReferences, final Element toolElement) {
        boolean toBeParsed = false;
        final String toolId = toolElement.getAttribute("id");
        final String toolVersion = toolElement.getAttribute("version");
        for (final ToolReference toolReference : toolReferences)
            if (toolReference.getId().equals(toolId) && toolReference.getVersion().equals(toolVersion)) {
                toBeParsed = true;
                break;
            }
        return toBeParsed;
    }
}
