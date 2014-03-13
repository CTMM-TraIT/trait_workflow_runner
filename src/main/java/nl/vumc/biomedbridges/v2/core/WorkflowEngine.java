package nl.vumc.biomedbridges.v2.core;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Freek
 * Date: 11-3-14
 * Time: 12:03
 */
public interface WorkflowEngine {
    void runWorkflow(final Workflow workflow) throws InterruptedException, IOException;
}
