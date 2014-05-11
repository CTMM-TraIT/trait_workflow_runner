/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.biomedbridges.galaxy.metadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import nl.vumc.biomedbridges.galaxy.metadata.parsers.GalaxyToolMetadataParser;
import nl.vumc.biomedbridges.galaxy.metadata.parsers.GalaxyWorkflowMetadataParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The access point for Galaxy workflow and tool metadata.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 * @author <a href="mailto:y.hoogstrate@erasmusmc.nl">Youri Hoogstrate</a>
 */
public class GalaxyWorkflowEngineMetadata {
    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(GalaxyWorkflowEngineMetadata.class);

    /**
     * Project directory.
     */
    private static final String PROJECT_DIRECTORY = "C:\\Freek\\VUmc\\BioMedBridges\\WorkflowRunner\\";

    /**
     * Galaxy configuration data directory for testing.
     */
    private static final String DATA_DIRECTORY = PROJECT_DIRECTORY + "data\\Galaxy configuration\\";

    /**
     * Mapping of workflow names to workflow metadata objects.
     */
    private Map<String, GalaxyWorkflowMetadata> workflowsMap;

    /**
     * Get the metadata of all available Galaxy workflows.
     *
     * @return the metadata of all available Galaxy workflows.
     */
    public Collection<GalaxyWorkflowMetadata> getWorkflows() {
        if (workflowsMap == null)
            initializeWorkflowMetadata();
        return workflowsMap != null ? workflowsMap.values() : null;
    }

    /**
     * Get the metadata of a Galaxy workflow.
     *
     * @return the metadata of a Galaxy workflow.
     */
    public GalaxyWorkflowMetadata getWorkflow(final String name) {
        if (workflowsMap == null)
            initializeWorkflowMetadata();
        return workflowsMap != null ? workflowsMap.get(name) : null;
    }

    /**
     * Initialize the workflow metadata by parsing the available workflow definitions, the tools configuration file, and
     * the tool definitions.
     */
    private void initializeWorkflowMetadata() {
        workflowsMap = new GalaxyWorkflowMetadataParser().readWorkflowsFromDirectories(DATA_DIRECTORY + "workflows\\");
        logger.trace("");
        logger.info("workflowsMap: " + workflowsMap);
        logger.trace("");
        final List<GalaxyToolReference> toolReferences = new ArrayList<>();
        for (final GalaxyWorkflowMetadata workflowMetadata : workflowsMap.values())
            toolReferences.addAll(workflowMetadata.getToolReferences());
        logger.info("toolReferences: " + toolReferences);
        logger.trace("");
        logger.trace("");
        final String toolsDirectory = DATA_DIRECTORY + "tools\\";
        final String configurationFilePath = toolsDirectory + "tool_conf.xml";
        final GalaxyToolMetadataParser toolMetadataParser = new GalaxyToolMetadataParser();
        final List<String> toolDefinitionPaths = toolMetadataParser.parseToolsConfiguration(configurationFilePath,
                                                                                            toolsDirectory);
        logger.trace("toolDefinitionPaths: " + toolDefinitionPaths);
        final List<GalaxyToolMetadata> toolsMetadata = toolMetadataParser.parseToolsMetadata(toolDefinitionPaths,
                                                                                             toolReferences);
        logger.info("toolsMetadata: " + toolsMetadata);
        for (final GalaxyWorkflowMetadata workflowMetadata : workflowsMap.values())
            workflowMetadata.addToolsMetadata(toolsMetadata);
    }
}
