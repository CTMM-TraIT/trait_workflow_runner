/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.demonstration;

import java.io.IOException;

import nl.vumc.biomedbridges.core.Workflow;
import nl.vumc.biomedbridges.core.WorkflowEngine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a very simple implementation of the WorkflowEngine interface.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 *
 * This class was created during the BioMedBridges annual general meeting on March 12th, 2014. The original
 * demonstration of the Workflow Runner was impossible because several Galaxy engines were down:
 *
 * https://usegalaxy.org/
 * Galaxy could not be reached
 * The filesystem which serves Galaxy datasets is currently unavailable due to a power interruption in the data center
 * at TACC. Galaxy will be offline until this filesystem is available again. It is estimated that it will return to
 * production service on Monday, March 10.
 *
 * http://galaxy.nbic.nl/workflow/import_workflow
 * Internal Server Error
 * Galaxy was unable to successfully complete your request
 * An error occurred.
 * This may be an intermittent problem due to load or other unpredictable factors, reloading the page may address the
 * problem.
 * The error has been logged to our team.
 */
public class DemonstrationWorkflowEngine implements WorkflowEngine {
    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(DemonstrationWorkflowEngine.class);

    @Override
    public Workflow getWorkflow(final String workflowName) {
        return new DemonstrationWorkflow(workflowName);
    }

    @Override
    public boolean runWorkflow(final Workflow workflow) {
        boolean result = false;
        final String workflowName = workflow.getName();
        logger.info("Running workflow " + workflow.getName() + "...");
        try {
            logger.info("DemonstrationWorkflowEngine.runWorkflow");
            result = workflow.run();
        } catch (final InterruptedException | IOException e) {
            logger.error("Exception while running workflow {}.", workflowName, e);
        }
        return result;
    }
}
