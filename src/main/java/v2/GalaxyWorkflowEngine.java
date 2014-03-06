package v2;

import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs;

/**
 * Created by youri on 3/6/14.
 * This class should be:
 * 1) the controller of the Workflow Engine
 * 2) able to handle the configuration
 */
public class GalaxyWorkflowEngine {
    GalaxyConfiguration config;

    public GalaxyWorkflowOutput executeWorkflow() {
        GalaxyWorkflowOutput output = new GalaxyWorkflowOutput();

        return output;
    }


    public GalaxyWorkflowEngine(GalaxyConfiguration arg_config) {
        config = arg_config;
    }

    public WorkflowInputs prepareInput() {
        //return prepareConcatenationWorkflow(galaxyInstance, workflowsClient, historyId,historiesClient, workflow.getName());
        return null;
    }
}
