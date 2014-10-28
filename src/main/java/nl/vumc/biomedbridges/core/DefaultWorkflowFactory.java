/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.core;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.beans.History;

import nl.vumc.biomedbridges.demonstration.DemonstrationWorkflowEngine;
import nl.vumc.biomedbridges.galaxy.GalaxyWorkflowEngine;
import nl.vumc.biomedbridges.galaxy.HistoryUtils;
import nl.vumc.biomedbridges.galaxy.configuration.GalaxyConfiguration;
import nl.vumc.biomedbridges.molgenis.MolgenisWorkflowEngine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory class for creating workflows based on workflow type. Using dependency injection, the DefaultGuiceModule class
 * binds the WorkflowFactory interface to this class.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class DefaultWorkflowFactory implements WorkflowFactory {
    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(DefaultWorkflowFactory.class);

    /**
     * Message for an unexpected workflow type.
     */
    private static final String UNEXPECTED_TYPE_MESSAGE = "Unexpected workflow type: {}.";

    /**
     * Create a default workflow engine factory. Instances of this class can be created directly or by dependency
     * injection (Guice).
     */
    public DefaultWorkflowFactory() {
    }

    @Override
    public Workflow getWorkflow(final WorkflowType workflowType, final Object configurationData,
                                final String workflowName) {
        final Workflow workflow;
        switch (workflowType) {
            case DEMONSTRATION:
                workflow = new DemonstrationWorkflowEngine().getWorkflow(workflowName);
                break;
            case GALAXY:
                if (configurationData instanceof GalaxyConfiguration) {
                    final GalaxyConfiguration galaxyConfiguration = (GalaxyConfiguration) configurationData;
                    final GalaxyInstance galaxyInstance = galaxyConfiguration.determineGalaxyInstance(null);
                    final History history = new History(galaxyConfiguration.getGalaxyHistoryName());
                    final String historyId = galaxyInstance.getHistoriesClient().create(history).getId();
                    final GalaxyWorkflowEngine workflowEngine = new GalaxyWorkflowEngine(galaxyInstance, historyId,
                                                                                         new HistoryUtils());
                    workflow = workflowEngine.getWorkflow(workflowName);
                } else
                    workflow = null;
                break;
            case MOLGENIS:
                workflow = new MolgenisWorkflowEngine().getWorkflow(workflowName);
                break;
            default:
                logger.error(UNEXPECTED_TYPE_MESSAGE, workflowType);
                workflow = null;
                break;
        }
        return workflow;
    }
}
