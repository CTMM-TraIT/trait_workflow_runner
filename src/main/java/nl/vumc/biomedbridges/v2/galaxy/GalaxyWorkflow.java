/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.biomedbridges.v2.galaxy;

import com.github.jmchilton.blend4j.galaxy.WorkflowsClient;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import nl.vumc.biomedbridges.v2.core.DefaultWorkflow;
import nl.vumc.biomedbridges.v2.core.Workflow;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;


/**
 * The workflow implementation for Galaxy.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class GalaxyWorkflow extends DefaultWorkflow implements Workflow {
    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(GalaxyWorkflow.class);

    /**
     * Construct a Galaxy workflow.
     *
     * @param name the name of the workflow.
     */
    public GalaxyWorkflow(final String name) {
        super(name);
        parseJson();
    }

    /**
     * Ensure the workflow is present on the Galaxy server. If it is not found, it will be created.
     *
     * @param workflowsClient the workflows client used to interact with the Galaxy workflows on the server.
     */
    public void ensureWorkflowIsOnServer(final WorkflowsClient workflowsClient) {
        boolean found = false;
        for (final com.github.jmchilton.blend4j.galaxy.beans.Workflow blend4jWorkflow : workflowsClient.getWorkflows())
            if (blend4jWorkflow.getName().equals(getName())
                || blend4jWorkflow.getName().equals(getName() + " (imported from API)")) {
                found = true;
                break;
            }
        if (!found)
            workflowsClient.importWorkflow(getJsonContent());
    }

    /**
     * Give the filename of the Galaxy workflow description
     *
     * todo: use an absolute file path instead of the classpath.
     *
     * @return the GA file's filename
     */
    private String getJsonFilename(){
        return getName() + ".ga";
    }

    /**
     * Get the content of the GA-file
     *
     * @author Youri Hoogstrate
     */
    private String getJsonContent() {
        try {
            return Resources.asCharSource(GalaxyWorkflow.class.getResource(getJsonFilename()), Charsets.UTF_8).read();
        } catch (final IOException e) {
            logger.error("Exception while retrieving json design in workflow file {}.", getJsonFilename(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Parses the JSON / GA-file of a Galaxy workflow
     *
     * @author Youri Hoogstrate
     */
    public void parseJson() {
        JSONParser parser=new JSONParser();
        try{
            Object obj = parser.parse(getJsonContent());
            JSONObject array = (JSONObject)obj;

            JSONObject steps = (JSONObject)array.get("steps");
            System.out.println("This workflow contains ["+steps.size()+"] steps:\n");

            Iterator iterx = steps.keySet().iterator();
            JSONObject step;
            JSONArray inputs,outputs;
            while (iterx.hasNext()) {
                step = (JSONObject)steps.get(iterx.next());

                addJsonInputs((JSONArray) step.get("inputs"));
                addJsonOutputs((JSONArray) step.get("outputs"));
            }

        }catch(ParseException pe){
            System.out.println("position: " + pe.getPosition());
            System.out.println(pe);
        }

        //http://www.tutorialspoint.com/json/json_java_example.htm
    }


    /**
     * Parse an "inputs" section of the JSON file
     *
     * @author Youri Hoogstrate
     */
    public void addJsonInputs(JSONArray jsonInputs) {
        JSONObject input;

        Iterator iterator = jsonInputs.iterator();
        while (iterator.hasNext()) {
            input = ( JSONObject) iterator.next();

            // Store it somewhere...
/*
            System.out.println("input:");
            System.out.println("id=" + input.get("id")+"\n");
            System.out.println("name=" + input.get("name")+"\n");
            */
        }
    }


    /**
     * Parse an "outputs" section of the JSON file
     *
     * @author Youri Hoogstrate
     */
    public void addJsonOutputs(JSONArray jsonOutputs) {
        JSONObject output;

        Iterator iterator = jsonOutputs.iterator();
        while (iterator.hasNext()) {
            output = ( JSONObject) iterator.next();

            // Store it somewhere...
/*
            System.out.println("output:");
            System.out.println("name=" + output.get("name"));
            System.out.println("type=" + output.get("type")+"\n");
            */
        }
    }
}
