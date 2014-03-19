/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.biomedbridges.v2.galaxy;

import com.github.jmchilton.blend4j.galaxy.WorkflowsClient;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import java.io.IOException;

import nl.vumc.biomedbridges.v2.core.DefaultWorkflow;
import nl.vumc.biomedbridges.v2.core.Workflow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    }

    /**
     * Ensure the workflow is present on the Galaxy server. If it is not found, it will be created.
     *
     * @param workflowsClient the workflows client used to interact with the Galaxy workflows on the server.
     */
    public void ensureWorkflowIsOnServer(final WorkflowsClient workflowsClient) {
        boolean found = false;
        for (final com.github.jmchilton.blend4j.galaxy.beans.Workflow blend4jWorkflow : workflowsClient.getWorkflows())
            if (blend4jWorkflow.getName().equals(getName())) {
                found = true;
                break;
            }
        if (!found)
            workflowsClient.importWorkflow(readWorkflowJson(getName() + ".ga"));
    }

    /**
     * Read the json design of a workflow from a file in the classpath.
     *
     * todo: use an absolute file path instead of the classpath.
     *
     * @param workflowFileName the workflow filename.
     * @return the json design of the workflow.
     */
    private String readWorkflowJson(final String workflowFileName) {
        try {
            return Resources.asCharSource(GalaxyWorkflow.class.getResource(workflowFileName), Charsets.UTF_8).read();
        } catch (final IOException e) {
            logger.error("Exception while retrieving json design in workflow file {}.", workflowFileName, e);
            throw new RuntimeException(e);
        }
    }
}
