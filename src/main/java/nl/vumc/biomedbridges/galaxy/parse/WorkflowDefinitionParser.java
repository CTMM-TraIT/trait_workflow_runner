/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.biomedbridges.galaxy.parse;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.vumc.biomedbridges.galaxy.metadata.GalaxyWorkflowMetadata;
import nl.vumc.biomedbridges.galaxy.metadata.GalaxyWorkflowStep;
import nl.vumc.biomedbridges.galaxy.metadata.ToolMetadata;
import nl.vumc.biomedbridges.galaxy.metadata.ToolReference;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * The Galaxy workflow definition parser that reads the metadata from a .ga json file.
 *
 * todo: combine this class with GalaxyWorkflow.parseJson and ToolDefinitionParser.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 * @author <a href="mailto:y.hoogstrate@erasmusmc.nl">Youri Hoogstrate</a>
 */
public class WorkflowDefinitionParser {
    /**
     * Project directory.
     */
    private static final String PROJECT_DIRECTORY = "C:\\Freek\\VUmc\\BioMedBridges\\WorkflowRunner\\";

    /**
     * Galaxy configuration data directory for testing.
     */
    private static final String DATA_DIRECTORY = PROJECT_DIRECTORY + "data\\Galaxy configuration\\";

    /**
     * Main method.
     *
     * @param arguments unused command-line arguments.
     */
    // CHECKSTYLE_OFF: UncommentedMain
    public static void main(final String[] arguments) {
        final Map<String, GalaxyWorkflowMetadata> workflowsMap;
        workflowsMap = new WorkflowDefinitionParser().readWorkflowsFromDirectories(DATA_DIRECTORY + "workflows\\");
        System.out.println();
        System.out.println("workflowsMap: " + workflowsMap);
        System.out.println();
        final List<ToolReference> toolReferences = new ArrayList<>();
        for (final GalaxyWorkflowMetadata workflowMetadata : workflowsMap.values())
            toolReferences.addAll(workflowMetadata.getToolReferences());
        System.out.println("toolReferences: " + toolReferences);
        System.out.println();
        System.out.println();
        final String configurationFilePath = DATA_DIRECTORY + "tool_conf.xml";
        final ToolDefinitionParser toolDefinitionParser = new ToolDefinitionParser();
        final List<String> toolDefinitionPaths = toolDefinitionParser.parseToolsConfiguration(configurationFilePath);
        System.out.println("toolDefinitionPaths: " + toolDefinitionPaths);
        final List<ToolMetadata> toolsMetadata = toolDefinitionParser.parseToolsMetadata(toolDefinitionPaths, toolReferences);
        System.out.println("toolsMetadata: " + toolsMetadata);
    }
    // CHECKSTYLE_ON: UncommentedMain

    private Map<String, GalaxyWorkflowMetadata> readWorkflowsFromDirectories(final String workflowsDirectoryPath) {
        final Map<String, GalaxyWorkflowMetadata> workflowsMap = new HashMap<>();
        final File[] subDirectories = new File(workflowsDirectoryPath).listFiles();
        if (subDirectories != null)
            for (final File subDirectory : subDirectories) {
                final File[] workflowFileNames = subDirectory.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(final File dir, final String name) {
                        return name.endsWith(".ga");
                    }
                });
                if (workflowFileNames != null)
                    for (final File workflowFile : workflowFileNames)
                        readWorkflow(workflowsMap, workflowFile);
            }
        return workflowsMap;
    }

    private void readWorkflow(final Map<String, GalaxyWorkflowMetadata> workflowsMap, final File workflowFile) {
        final String filePath = workflowFile.getAbsolutePath();
        final GalaxyWorkflowMetadata workflowMetadata = new WorkflowDefinitionParser().parseWorkflowDefinition(filePath);
        workflowsMap.put(workflowMetadata.getName(), workflowMetadata);
        System.out.println("workflowMetadata: " + workflowMetadata);
        for (final GalaxyWorkflowStep step : workflowMetadata.getSteps())
            if (step.getToolId() != null)
                System.out.println("step[" + step.getId() + "].getToolId(): " + step.getToolId());
    }

    private GalaxyWorkflowMetadata parseWorkflowDefinition(final String filePath) {
        GalaxyWorkflowMetadata result;
        try {
            final String jsonContent = Joiner.on("\n").join(Files.readAllLines(Paths.get(filePath), Charsets.UTF_8));
            final JSONObject workflowJson = (JSONObject) new JSONParser().parse(jsonContent);
            System.out.println("workflowJson: " + workflowJson);
            System.out.println();
            result = new GalaxyWorkflowMetadata(workflowJson);
            System.out.println();
        } catch (final IOException | ParseException e) {
            result = null;
            e.printStackTrace();
        }
        return result;
    }
}
