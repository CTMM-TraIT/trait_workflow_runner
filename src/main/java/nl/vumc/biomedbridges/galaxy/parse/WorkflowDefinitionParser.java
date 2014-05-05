/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.biomedbridges.galaxy.parse;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import nl.vumc.biomedbridges.galaxy.metadata.GalaxyWorkflowMetadata;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * The Galaxy workflow definition parser that reads the metadata from a .ga json file.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 * @author <a href="mailto:y.hoogstrate@erasmusmc.nl">Youri Hoogstrate</a>
 */
public class WorkflowDefinitionParser {
    // todo: combine with GalaxyWorkflow.parseJson and ToolDefinitionParser.

    public static void main(final String[] args) {
        final String filePath = "C:\\Freek\\VUmc\\BioMedBridges\\WorkflowRunner\\etc\\histogram\\Histogram.ga";
        new WorkflowDefinitionParser().parseWorkflowDefinition(filePath);
    }

    private void parseWorkflowDefinition(final String filePath) {
        try {
            final String jsonContent = Joiner.on("\n").join(Files.readAllLines(Paths.get(filePath), Charsets.UTF_8));
            final JSONObject workflowJson = (JSONObject) new JSONParser().parse(jsonContent);
            System.out.println("workflowJson: " + workflowJson);
            new GalaxyWorkflowMetadata(workflowJson);
        } catch (final IOException | ParseException e) {
            e.printStackTrace();
        }
    }
}
