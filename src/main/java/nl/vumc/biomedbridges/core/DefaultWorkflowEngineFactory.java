/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.core;

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
    public WorkflowEngine getWorkflowEngine(final String workflowType, final HistoryUtils historyUtils) {
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
    public WorkflowEngine getWorkflowEngine(final String workflowType, final Object configurationData,
                                            final HistoryUtils historyUtils) {
        final WorkflowEngine workflowEngine;
        switch (workflowType) {
            case DEMONSTRATION_TYPE:
                workflowEngine = new DemonstrationWorkflowEngine();
                break;
            case GALAXY_TYPE:
                if (configurationData instanceof GalaxyConfiguration) {
                    final GalaxyConfiguration galaxyConfiguration = (GalaxyConfiguration) configurationData;
                    workflowEngine = new GalaxyWorkflowEngine(galaxyConfiguration.determineGalaxyInstance(null),
                                                              galaxyConfiguration.getGalaxyHistoryName(), historyUtils);
                } else
                    workflowEngine = null;
                break;
            case MOLGENIS_TYPE:
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
