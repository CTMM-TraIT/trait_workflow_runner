package v2;

import org.codehaus.jettison.json.JSONObject;

import java.net.URL;

/**
 * Created by youri on 3/6/14.
 */

public class GalaxyWorkflowTemplate extends com.github.jmchilton.blend4j.galaxy.beans.WorkflowDetails {
    public class GalaxyWorkflowTemplateChild extends WorkflowTemplate {
        GalaxyWorkflowInputTemplate input;
        GalaxyWorkflowOutputTemplate output;
        JSONObject json_file;

        public boolean isAvailable(GalaxyWorkflowEngine engine) {
            return false;
        }

        public void makeAvailable(GalaxyWorkflowEngine engine) {
            if (disabled == false) {
                //engine.importWorkflow(json_file);
            }
        }

        public void setJSONbyURL(URL json_URL) {


            // if valid JSON:
            enable();
        }


        //public void setJSONbyName(String name, GalaxyWorkflowEngine engine) {
        // find workflow with same name on the server and if succes:
        //  enable();
        //}
    }
}