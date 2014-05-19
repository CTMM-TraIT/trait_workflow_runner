/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.biomedbridges.galaxy.metadata.parsers;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import nl.vumc.biomedbridges.galaxy.metadata.GalaxyWorkflowMetadata;
import nl.vumc.biomedbridges.galaxy.metadata.GalaxyWorkflowStep;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Galaxy workflow definition parser that reads the metadata from a .ga json file.
 *
 * todo: combine this class with GalaxyWorkflow.parseJson and GalaxyToolMetadataParser.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 * @author <a href="mailto:y.hoogstrate@erasmusmc.nl">Youri Hoogstrate</a>
 */
public class GalaxyWorkflowMetadataParser {
    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(GalaxyWorkflowMetadataParser.class);

    /**
     * Read all workflow definition (.ga) files that are in subdirectories below the workflows directory.
     *
     * @param workflowsDirectoryPath the workflows directory.
     * @return the map with workflow names to workflow metadata objects.
     */
    public Map<String, GalaxyWorkflowMetadata> readWorkflowsFromDirectories(final String workflowsDirectoryPath) {
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

    /**
     * Read a workflow definition (.ga) file.
     *
     * @param workflowsMap the workflows metadata map to which this workflow will be added.
     * @param workflowFile the workflow definition (.ga) file.
     */
    private void readWorkflow(final Map<String, GalaxyWorkflowMetadata> workflowsMap, final File workflowFile) {
        final String filePath = workflowFile.getAbsolutePath();
        final GalaxyWorkflowMetadata workflowMetadata = parseWorkflowDefinition(filePath);
        workflowsMap.put(workflowMetadata.getName(), workflowMetadata);
        logger.trace("workflowMetadata: " + workflowMetadata);
        for (final GalaxyWorkflowStep step : workflowMetadata.getSteps())
            if (step.getToolId() != null)
                logger.trace("step[" + step.getId() + "].getToolId(): " + step.getToolId());
    }

    /**
     * Parse the workflow definition (.ga) file. The file is in json format.
     *
     * @param filePath the file path to the workflow definition (.ga) file.
     * @return the workflow metadata object.
     */
    private GalaxyWorkflowMetadata parseWorkflowDefinition(final String filePath) {
        GalaxyWorkflowMetadata result;
        try {
            final String jsonContent = Joiner.on("\n").join(Files.readAllLines(Paths.get(filePath), Charsets.UTF_8));
            final JSONObject workflowJson = (JSONObject) new JSONParser().parse(jsonContent);
            logger.trace("workflowJson: " + workflowJson);
            logger.trace("");
            result = new GalaxyWorkflowMetadata(workflowJson);
            logger.trace("");
        } catch (final IOException | ParseException e) {
            result = null;
            e.printStackTrace();
        }
        return result;
    }
}
