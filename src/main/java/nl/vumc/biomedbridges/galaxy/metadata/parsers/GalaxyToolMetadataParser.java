/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.galaxy.metadata.parsers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import nl.vumc.biomedbridges.galaxy.metadata.GalaxyToolMetadata;
import nl.vumc.biomedbridges.galaxy.metadata.GalaxyToolReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * The parser for the Galaxy tools configuration and individual metadata for each tool.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class GalaxyToolMetadataParser {
    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(GalaxyToolMetadataParser.class);

    /**
     * Parse all tool information from the tool configuration file (tool_conf.xml).
     *
     * @param filePath the file path of the tool configuration file.
     * @param toolsDirectory the directory where the files with metadata of the individual tools is located.
     * @return a list of file paths of tool definition files (in xml format).
     */
    public List<String> parseToolsConfiguration(final String filePath, final String toolsDirectory) {
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
                    final String toolFilePath = toolsDirectory + fileAttribute.replaceAll("/", "\\\\");
                    logger.trace("toolFilePath: " + toolFilePath);
                    toolDefinitionFilePaths.add(toolFilePath);
                }
            }
        } catch (final SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }
        return toolDefinitionFilePaths;
    }

    /**
     * Parse the tool definitions that are referenced by the workflows.
     *
     * @param toolDefinitionPaths the list of file paths of tool definition files (in xml format).
     * @param toolReferences the tool references as used by the workflows.
     * @return the list of tool metadata objects.
     */
    public List<GalaxyToolMetadata> parseToolsMetadata(final List<String> toolDefinitionPaths,
                                                       final List<GalaxyToolReference> toolReferences) {
        final List<GalaxyToolMetadata> toolsMetadata = new ArrayList<>();
        for (final String toolDefinitionPath : toolDefinitionPaths) {
            final GalaxyToolMetadata toolMetadata = parseToolDefinition(toolDefinitionPath, toolReferences);
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
     * @return the definition of a tool.
     */
    private GalaxyToolMetadata parseToolDefinition(final String filePath,
                                                   final List<GalaxyToolReference> toolReferences) {
        logger.trace("filePath: " + filePath);
        GalaxyToolMetadata toolMetadata = null;
        try {
            final Element toolElement = parseXmlDocument(filePath);
            if (toolToBeParsed(toolReferences, toolElement))
                toolMetadata = new GalaxyToolMetadata(toolElement);
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

    /**
     * Determine whether the tool specified by the tool element should be parsed: if it is in the list of tools that are
     * referenced by the workflows.
     *
     * @param toolReferences the list of tools that are referenced by the workflows.
     * @param toolElement the tool element specifying the tool that should be parsed or not.
     * @return whether the tool specified by the tool element should be parsed or not.
     */
    private boolean toolToBeParsed(final List<GalaxyToolReference> toolReferences, final Element toolElement) {
        boolean toBeParsed = false;
        final String toolId = toolElement.getAttribute("id");
        final String toolVersion = toolElement.getAttribute("version");
        final boolean versionEmpty = "".equals(toolVersion);
        for (final GalaxyToolReference toolReference : toolReferences)
            if (toolReference.getId().equals(toolId) && (versionEmpty || toolReference.getVersion().equals(toolVersion))) {
                toBeParsed = true;
                break;
            }
        return toBeParsed;
    }
}
