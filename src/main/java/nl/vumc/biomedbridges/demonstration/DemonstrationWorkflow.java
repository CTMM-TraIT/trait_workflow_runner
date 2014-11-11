/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.demonstration;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import nl.vumc.biomedbridges.core.BaseWorkflow;
import nl.vumc.biomedbridges.core.Constants;
import nl.vumc.biomedbridges.core.FileUtils;
import nl.vumc.biomedbridges.core.Workflow;
import nl.vumc.biomedbridges.examples.RandomLinesExample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a very simple implementation of the Workflow interface.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class DemonstrationWorkflow extends BaseWorkflow implements Workflow {
    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(DemonstrationWorkflow.class);

    /**
     * Create a demonstration workflow.
     *
     * @param name the workflow name.
     */
    protected DemonstrationWorkflow(final String name) {
        super(name);
    }

    @Override
    public boolean run() throws IOException, InterruptedException {
        boolean result = false;
        if (Constants.CONCATENATE_WORKFLOW.equals(getName()))
            result = runConcatenateWorkflow();
        else if (Constants.WORKFLOW_RANDOM_LINES_TWICE.equals(getName()))
            result = runRandomLinesWorkflow();
        return result;
    }

    /**
     * Run the concatenate workflow.
     *
     * @return whether the workflow ran successfully.
     * @throws IOException if reading the workflow results fails.
     */
    private boolean runConcatenateWorkflow() throws IOException {
        final Object input1 = getInput("WorkflowInput1");
        final Object input2 = getInput("WorkflowInput2");
        final boolean result = input1 instanceof File && input2 instanceof File;
        if (result) {
            final String inputString1 = Joiner.on("").join(Files.readLines((File) input1, Charsets.UTF_8));
            final String inputString2 = Joiner.on("").join(Files.readLines((File) input2, Charsets.UTF_8));
            logger.info("input 1: {}", inputString1);
            logger.info("input 2: {}", inputString2);
            addOutput("output", FileUtils.createOutputFile(this, inputString1, inputString2));
            logger.info("output: {} {}", inputString1, inputString2);
        } else
            logger.error("Input parameters are not of the expected type (two input files where expected).");
        return result;
    }

    /**
     * Run the random lines twice workflow.
     *
     * @return whether the workflow ran successfully.
     * @throws IOException if reading the workflow results fails.
     */
    private boolean runRandomLinesWorkflow() throws IOException {
        final String inputName = "Input Dataset";
        final Object input = getInput(inputName);
        final boolean result = input instanceof File;
        if (result) {
            final int stepId2 = 2;
            final int stepId3 = 3;
            final String numberOfLinesParameter = "num_lines";
            final int initialLineCount = (int) getParameters().get(stepId2).get(numberOfLinesParameter);
            final int definitiveLineCount = (int) getParameters().get(stepId3).get(numberOfLinesParameter);
            final Random randomGenerator = new Random(123456);
            final List<String> lines = Files.readLines((File) input, Charsets.UTF_8);
            final List<String> selectedLines1 = selectRandomLines(lines, initialLineCount, randomGenerator);
            final List<String> selectedLines2 = selectRandomLines(selectedLines1, definitiveLineCount, randomGenerator);
            final String[] selectedLines2Array = selectedLines2.toArray(new String[selectedLines2.size()]);
            final File outputFile = FileUtils.createOutputFile(this, selectedLines2Array);
            addOutput(RandomLinesExample.OUTPUT_NAME, outputFile);
            logger.info("output: {}", selectedLines2);
        } else
            logger.error("Expected input file was not found.");
        return result;
    }

    /**
     * Select a number of random lines from the list of lines. If the number of lines to be selected is greater than or
     * equal to the total number of lines in the list, the entire list is returned.
     *
     * @param lines the list of lines.
     * @param lineCount the number of lines to select.
     * @param randomGenerator the random generator to use.
     * @return the randomly selected lines.
     */
    private List<String> selectRandomLines(final List<String> lines, final int lineCount, final Random randomGenerator) {
        final List<String> selectedLines = new ArrayList<>();
        final List<Integer> selectedIndices = new ArrayList<>();
        if (lineCount >= lines.size())
            selectedLines.addAll(lines);
        else
            while (selectedIndices.size() < lineCount) {
                final int selectedIndex = randomGenerator.nextInt(lines.size());
                if (!selectedIndices.contains(selectedIndex)) {
                    selectedIndices.add(selectedIndex);
                    selectedLines.add(lines.get(selectedIndex));
                }
            }
        return selectedLines;
    }
}
