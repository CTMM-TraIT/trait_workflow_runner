package v2;

/**
 * Created by youri on 3/6/14.
 */
public class WorkflowTemplate {
    protected boolean disabled;

    public WorkflowTemplate() {
        disable();
    }


    public void enable() {
        disabled = true;
    }

    public void disable() {
        disabled = false;
    }

}
