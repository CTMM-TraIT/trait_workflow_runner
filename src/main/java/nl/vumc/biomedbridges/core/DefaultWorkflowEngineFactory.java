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
 * Factory class for creating workflow engines based on workflow type. Using dependency injection, the
 * DefaultGuiceModule class binds the WorkflowEngineFactory interface to this class.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class DefaultWorkflowEngineFactory implements WorkflowEngineFactory {
    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(DefaultWorkflowEngineFactory.class);

    /**
     * Message for an unexpected workflow type.
     */
    private static final String UNEXPECTED_TYPE_MESSAGE = "Unexpected workflow type: {}.";

    /**
     * Create a default workflow engine factory. Instances of this class can be created directly or by dependency
     * injection (Guice).
     */
    public DefaultWorkflowEngineFactory() {
    }

    /**
     * Create a workflow engine based on the workflow (engine) type.
     *
     * @param workflowType the workflow (engine) type.
     * @param historyUtils the history utils object.
     * @return the new workflow engine (or null if the type was not recognized).
     */
    @Override
    public WorkflowEngine getWorkflowEngine(final WorkflowType workflowType, final HistoryUtils historyUtils) {
        return getWorkflowEngine(workflowType, null, historyUtils);
    }

    /**
     * Create a workflow engine based on the workflow (engine) type.
     *
     * @param workflowType      the workflow (engine) type.
     * @param configurationData the configuration data.
     * @param historyUtils      the history utils object.
     * @return the new workflow engine (or null if the type was not recognized).
     */
    @Override
    public WorkflowEngine getWorkflowEngine(final WorkflowType workflowType, final Object configurationData,
                                            final HistoryUtils historyUtils) {
        final WorkflowEngine workflowEngine;
        switch (workflowType) {
            case DEMONSTRATION:
                workflowEngine = new DemonstrationWorkflowEngine();
                break;
            case GALAXY:
                if (configurationData instanceof GalaxyConfiguration) {
                    final GalaxyConfiguration galaxyConfiguration = (GalaxyConfiguration) configurationData;
                    final GalaxyInstance galaxyInstance = galaxyConfiguration.determineGalaxyInstance(null);
                    final History history = new History(galaxyConfiguration.getGalaxyHistoryName());
                    final String historyId = galaxyInstance.getHistoriesClient().create(history).getId();
                    workflowEngine = new GalaxyWorkflowEngine(galaxyInstance, historyId, historyUtils);
                } else
                    workflowEngine = null;
                break;
            case MOLGENIS:
                workflowEngine = new MolgenisWorkflowEngine();
                break;
            default:
                logger.error(UNEXPECTED_TYPE_MESSAGE, workflowType);
                workflowEngine = null;
                break;
        }
//        if (workflowEngine != null && configurationData != null)
//            workflowEngine.configure(configurationData);
        return workflowEngine;
    }
}
