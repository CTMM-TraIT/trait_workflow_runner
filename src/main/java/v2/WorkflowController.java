package v2;

/**
 * Created by youri on 3/6/14.
 * <p/>
 * // Here you can program e.g. the CLI or API calls or functional tests / examples.
 */
public class WorkflowController {
    public void main(String[] args) {
        testGalaxy1();
    }

    public void testGalaxy1() {
        // Please rewrite to make it static:
        GalaxyConfiguration galaxy_config = new GalaxyConfiguration();
        GalaxyWorkflowEngine workflow_engine = new GalaxyWorkflowEngine(galaxy_config);
        GalaxyWorkflowTemplate workflow = new GalaxyWorkflowTemplate();// disabled by default
        //workflow.setJSONbyURL(URL(".... .ga"));

        // inputs = workflow_engine.prepareInputs(workflow,{"key1":string_1, "key2":file_1});
        // outputController = workflow_engine->executeWorkflow(GalaxyWorkflowTemplate, inputs);
        // while(outputController.getStatus() != STATUS_WAITING)
        // {
        //    sleep(outputController.getSleepingTime());
        // }

        // if(outputController.getStatus == STATUS_SUCCESS) {
        //  while(output = outputController.getOutput())
        //  {
        //      System.out.println(output.getText())
        //  }
        // }
    }
}

