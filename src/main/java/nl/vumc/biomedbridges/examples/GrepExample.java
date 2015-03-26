/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.examples;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import nl.vumc.biomedbridges.core.Constants;
import nl.vumc.biomedbridges.core.DefaultWorkflowFactory;
import nl.vumc.biomedbridges.core.FileUtils;
import nl.vumc.biomedbridges.core.Workflow;
import nl.vumc.biomedbridges.core.WorkflowFactory;
import nl.vumc.biomedbridges.core.WorkflowType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains a simple example of the workflow that searches for a pattern (grep) in the input file.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class GrepExample extends AbstractBaseExample {
    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(GrepExample.class);

    /**
     * Construct the grep example.
     *
     * @param workflowFactory the workflow factory to use.
     */
    public GrepExample(final WorkflowFactory workflowFactory) {
        super(null, workflowFactory, logger);
    }

    /**
     * Main method.
     *
     * @param arguments unused command-line arguments.
     */
    // CHECKSTYLE_OFF: UncommentedMain
    public static void main(final String[] arguments) {
        final File inputFile = FileUtils.createTemporaryFile("8\t21", "9\t34", "10\t55", "11\t89", "12\t144");
        final String pattern = "5[0-9]";
        final GrepExample grepExample = new GrepExample(new DefaultWorkflowFactory());
        grepExample.run(Constants.THE_HYVE_GALAXY_URL, Constants.GREP_WORKFLOW, inputFile, pattern);
    }
    // CHECKSTYLE_ON: UncommentedMain

    /**
     * Run this example workflow and return the result.
     *
     * @param galaxyInstanceUrl the URL of the Galaxy instance to use.
     * @param workflowName      the name of the workflow to run.
     * @param inputFile         the file to search.
     * @param pattern           the regular expression to search for.
     * @return whether the workflow ran successfully.
     */
    public boolean run(final String galaxyInstanceUrl, final String workflowName, final File inputFile,
                       final String pattern) {
        final Workflow workflow = workflowFactory.getWorkflow(WorkflowType.GALAXY, galaxyInstanceUrl, workflowName);

        try {
            workflow.addInput("input", inputFile);
            // todo: step number can vary from server to server; a more robust way to specify parameters would be nice.
            workflow.setParameter(1, "pattern", pattern);

            workflow.run();

            checkWorkflowSingleOutput(workflow, "matching_lines", internalGrep(inputFile, pattern));
        } catch (final InterruptedException | IOException e) {
            logger.error("Exception while running workflow {}.", workflow.getName(), e);
        }

        return finishExample(workflow);
    }

    /**
     * Simple internal implementation of grep to determine the expected output of the workflow.
     *
     * @param inputFile the file to search.
     * @param pattern   the regular expression to search for.
     * @return the matching lines.
     * @throws IOException when reading from the file fails.
     */
    protected static List<String> internalGrep(final File inputFile, final String pattern) throws IOException {
        final Predicate<String> grepPredicate = line -> Pattern.compile(pattern).matcher(line).find();
        return Files.readAllLines(inputFile.toPath()).stream().filter(grepPredicate).collect(Collectors.toList());
    }
}
