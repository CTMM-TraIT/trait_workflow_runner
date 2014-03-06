package v2;

/**
 * Created by youri on 3/6/14.
 */
public class WorkflowOutput {
    private int Status = -1;// -1 = unset; 0 = waiting; 1 = success; 2 = error

    public void setStatus(int arg_Status) {
        // if Status == -1: allow arg_Status 0
        // elif Status == 0: allow arg_Status in [1,2]
        // else: error
    }

    public int getStatus() {
        return Status;
    }
}
