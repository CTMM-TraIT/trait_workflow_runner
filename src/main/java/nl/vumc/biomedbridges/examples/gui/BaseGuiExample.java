/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.examples.gui;

import com.google.inject.Guice;
import com.google.inject.Injector;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyledDocument;

import nl.vumc.biomedbridges.core.Constants;
import nl.vumc.biomedbridges.core.DefaultGuiceModule;
import nl.vumc.biomedbridges.core.WorkflowType;
import nl.vumc.biomedbridges.examples.RandomLinesExample;
import nl.vumc.biomedbridges.galaxy.metadata.GalaxyStepInput;
import nl.vumc.biomedbridges.galaxy.metadata.GalaxyStepInputConnection;
import nl.vumc.biomedbridges.galaxy.metadata.GalaxyToolConditional;
import nl.vumc.biomedbridges.galaxy.metadata.GalaxyToolOption;
import nl.vumc.biomedbridges.galaxy.metadata.GalaxyToolParameterMetadata;
import nl.vumc.biomedbridges.galaxy.metadata.GalaxyToolWhen;
import nl.vumc.biomedbridges.galaxy.metadata.GalaxyWorkflowEngineMetadata;
import nl.vumc.biomedbridges.galaxy.metadata.GalaxyWorkflowMetadata;
import nl.vumc.biomedbridges.galaxy.metadata.GalaxyWorkflowStep;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains all the base code for using the metadata for a Galaxy workflow to build a simple read-only Swing 
 * GUI that looks a bit like the web interface of this workflow in Galaxy.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class BaseGuiExample {
    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(RandomLinesExample.class);

    /**
     * The frame width.
     */
    private static final int FRAME_WIDTH = 800;

    /**
     * The frame height.
     */
    private static final int FRAME_HEIGHT = 660;

    /**
     * The font name for the GUI components.
     */
    private static final String GUI_FONT_NAME = "SansSerif";

    /**
     * The title font.
     */
    private static final Font TITLE_FONT = new Font(GUI_FONT_NAME, Font.BOLD, 24);

    /**
     * The header level 2 font.
     */
    private static final Font HEADER_2_FONT = new Font(GUI_FONT_NAME, Font.BOLD, 14);

    /**
     * The default GUI component font.
     */
    private static final Font DEFAULT_GUI_FONT = new Font(GUI_FONT_NAME, Font.PLAIN, 14);

    /**
     * The font name for the workflow results.
     */
    private static final String RESULTS_FONT_NAME = "Monospaced";

    /**
     * The results font.
     */
    private static final Font RESULTS_FONT = new Font(RESULTS_FONT_NAME, Font.PLAIN, 14);

    /**
     * Small distance between components.
     */
    private static final int SMALL_PAD = 5;

    /**
     * Two unit distance between components.
     */
    private static final int DOUBLE_PAD = 2 * SMALL_PAD;

    /**
     * Four unit distance between components.
     */
    private static final int QUAD_PAD = 4 * SMALL_PAD;

    /**
     * The text shown for null or empty values.
     */
    private static final String EMPTY_VALUE = "------";

    /**
     * The frame of the Swing GUI.
     */
    private JFrame frame;

    /**
     * The main panel for the GUI.
     */
    private JPanel guiPanel;

    /**
     * The layout manager for the GUI panel.
     */
    private SpringLayout guiLayout;

    /**
     * The button to run the workflow.
     */
    private JButton runWorkflowButton;

    /**
     * The list of the step panels.
     */
    private List<JPanel> stepPanels;

    /**
     * The map of parameter names to text fields.
     */
    private Map<String, JTextField> parameterTextFieldsMap;

    /**
     * Schedule a job for the event-dispatching thread: creating and showing this application's GUI.
     *
     * @param workflowName the name of the workflow to show.
     * @return the new frame.
     */
    protected JFrame createGuiExample(final String workflowName) {
        return createGuiExample(workflowName, true);
    }

    /**
     * Schedule a job for the event-dispatching thread: creating and (possibly) showing this application's GUI.
     *
     * @param workflowName the name of the workflow to show.
     * @param makeVisible whether the frame should be visible or invisible.
     * @return the new frame.
     */
    protected JFrame createGuiExample(final String workflowName, final boolean makeVisible) {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    constructGuiExample(workflowName, makeVisible);
                }
            });
        } catch (final InvocationTargetException | InterruptedException e) {
            e.printStackTrace();
        }
        return frame;
    }

    /**
     * Run the GUI example with a visible or invisible frame.
     *
     * @param workflowName the name of the workflow to show.
     * @param makeVisible whether the frame should be visible or invisible.
     * @return the new frame.
     */
    private JFrame constructGuiExample(final String workflowName, final boolean makeVisible) {
        // Create the GUI.
        frame = new JFrame(workflowName + " gui example");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        guiLayout = new SpringLayout();
        guiPanel = new JPanel(guiLayout);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(guiPanel, BorderLayout.CENTER);

        final GalaxyWorkflowMetadata workflowMetadata = new GalaxyWorkflowEngineMetadata().getWorkflow(workflowName);
        final Component previousComponent = addTitleAndAnnotation(workflowName, workflowMetadata);
        addStepPanelsAndButton(workflowName, workflowMetadata, previousComponent);

        // Center the frame and show it.
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setVisible(makeVisible);
        adjustStepPanelSizes(true);

        return frame;
    }

    /**
     * Add the step panels and possibly the run workflow button.
     *
     * @param workflowName the name of the workflow to show.
     * @param workflowMetadata the workflow metadata.
     * @param initialPreviousComponent the previous component that was added to the GUI panel (used for layout).
     */
    private void addStepPanelsAndButton(final String workflowName, final GalaxyWorkflowMetadata workflowMetadata,
                                        final Component initialPreviousComponent) {
        Component previousComponent = initialPreviousComponent;

        stepPanels = new ArrayList<>();
        parameterTextFieldsMap = new HashMap<>();
        for (int stepIndex = 0; stepIndex < workflowMetadata.getSteps().size(); stepIndex++)
            previousComponent = addStepPanel(workflowMetadata, stepIndex, guiPanel, guiLayout, previousComponent);

        if (workflowName.equals(Constants.WORKFLOW_RANDOM_LINES_TWICE)) {
            runWorkflowButton = new JButton("Run workflow");
            runWorkflowButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    runWorkflow(false);
                }
            });
            guiPanel.add(runWorkflowButton);
            guiLayout.putConstraint(SpringLayout.NORTH, runWorkflowButton, QUAD_PAD, SpringLayout.SOUTH, previousComponent);
            guiLayout.putConstraint(SpringLayout.WEST, runWorkflowButton, SMALL_PAD, SpringLayout.WEST, guiPanel);
            guiLayout.putConstraint(SpringLayout.EAST, runWorkflowButton, -SMALL_PAD, SpringLayout.EAST, guiPanel);
        }
    }

    /**
     * Add the title, the annotation (if available), and the separator line.
     *
     * @param workflowName the name of the workflow to show.
     * @param workflowMetadata the workflow metadata.
     * @return the last component added to the GUI panel (the separator line).
     */
    private Component addTitleAndAnnotation(final String workflowName, final GalaxyWorkflowMetadata workflowMetadata) {
        final String titleText = String.format("Running workflow \"%s\"", workflowName);
        final JLabel titleLabel = addLabel(guiPanel, guiLayout, titleText, TITLE_FONT, null);

        final String annotation = workflowMetadata.getAnnotation();
        final JLabel annotationLabel = annotation != null
                                       ? addLabel(guiPanel, guiLayout, annotation, DEFAULT_GUI_FONT, titleLabel, 1)
                                       : null;

        final JLabel previousLabel = annotationLabel != null ? annotationLabel : titleLabel;
        final JSeparator separatorLine = new JSeparator(SwingConstants.HORIZONTAL);
        guiPanel.add(separatorLine);
        guiLayout.putConstraint(SpringLayout.NORTH, separatorLine, QUAD_PAD, SpringLayout.SOUTH, previousLabel);
        guiLayout.putConstraint(SpringLayout.WEST, separatorLine, SMALL_PAD, SpringLayout.WEST, guiPanel);
        guiLayout.putConstraint(SpringLayout.EAST, separatorLine, -SMALL_PAD, SpringLayout.EAST, guiPanel);
        return separatorLine;
    }

    /**
     * Add a label to a panel.
     *
     * @param panel the panel to add the new label to.
     * @param springLayout the spring layout to add GUI constraints to.
     * @param text the text for the label.
     * @param font the font for the label.
     * @param previousComponent the previous component that was added to the GUI panel (used for layout).
     * @return the new label.
     */
    private JLabel addLabel(final JPanel panel, final SpringLayout springLayout, final String text, final Font font,
                            final Component previousComponent) {
        return addLabel(panel, springLayout, text, font, previousComponent, 0);
    }

    /**
     * Add a label to a panel.
     *
     * @param panel the panel to add the new label to.
     * @param springLayout the spring layout to add GUI constraints to.
     * @param text the text for the label.
     * @param font the font for the label.
     * @param previousComponent the previous component that was added to the GUI panel (used for layout).
     * @param extraPadWest optional extra padding for the west constraint.
     * @return the new label.
     */
    private JLabel addLabel(final JPanel panel, final SpringLayout springLayout, final String text, final Font font,
                            final Component previousComponent, final int extraPadWest) {
        final JLabel label = new JLabel(text);
        label.setFont(font);
        panel.add(label);
        final Component anchorComponent = previousComponent == null ? panel : previousComponent;
        final String anchorEdge = previousComponent == null ? SpringLayout.NORTH : SpringLayout.SOUTH;
        springLayout.putConstraint(SpringLayout.NORTH, label, SMALL_PAD, anchorEdge, anchorComponent);
        springLayout.putConstraint(SpringLayout.WEST, label, SMALL_PAD + extraPadWest, SpringLayout.WEST, panel);
        return label;
    }

    /**
     * Add a new panel for a workflow step.
     *
     * @param workflowMetadata the workflow metadata.
     * @param stepIndex the step index.
     * @param guiPanel the GUI panel to add the new panel to.
     * @param guiLayout the GUI layout to add GUI constrains to.
     * @param previousComponent the previous component that was added to the GUI panel (used for layout).
     * @return the new panel.
     */
    private JPanel addStepPanel(final GalaxyWorkflowMetadata workflowMetadata, final int stepIndex,
                                final JPanel guiPanel, final SpringLayout guiLayout, final Component previousComponent) {
        final JPanel stepPanel = new JPanel();
        stepPanels.add(stepPanel);
        final GalaxyWorkflowStep step = workflowMetadata.getSteps().get(stepIndex);
        final String toolVersion = step.getToolVersion();
        final String stepText = "Step " + (stepIndex + 1) + ": " + step.getName()
                                + (toolVersion != null ? " (version " + toolVersion + ")" : "");
        final SpringLayout stepLayout = new SpringLayout();
        stepPanel.setLayout(stepLayout);
        stepPanel.setBorder(new TitledBorder(stepText));
        Component previousStepComponent = null;
        for (final GalaxyStepInput stepInput : step.getInputs())
            previousStepComponent = addStepRow(stepPanel, stepInput.getName(), stepInput.getDescription(), step, null,
                                               stepLayout, previousStepComponent);
        if (step.getToolMetadata() != null) {
            for (final GalaxyToolParameterMetadata parameter : step.getToolMetadata().getParameters())
                previousStepComponent = addStepRow(stepPanel, parameter.getLabel(), parameter.getValue(), step,
                                                   parameter, stepLayout, previousStepComponent);
            addConditionalParameters(stepPanel, step, stepLayout, previousStepComponent);
        }
        guiPanel.add(stepPanel);
        guiLayout.putConstraint(SpringLayout.NORTH, stepPanel, SMALL_PAD, SpringLayout.SOUTH, previousComponent);
        guiLayout.putConstraint(SpringLayout.WEST, stepPanel, SMALL_PAD, SpringLayout.WEST, guiPanel);
        guiLayout.putConstraint(SpringLayout.EAST, stepPanel, -SMALL_PAD, SpringLayout.EAST, guiPanel);
        return stepPanel;
    }

    /**
     * Add a new row with a title and text to a Galaxy step panel for an input or a parameter.
     *
     * @param stepPanel the step panel to add the new row to.
     * @param title the title of the input or the parameter.
     * @param text the text of the input or the parameter.
     * @param step the Galaxy step.
     * @param parameter the parameter metadata.
     * @param stepLayout the step layout to add GUI constrains to.
     * @param previousStepComponent the previous step component that was added to the GUI panel (used for layout).
     * @return the last component that was added to the step panel.
     */
    private Component addStepRow(final JPanel stepPanel, final String title, final String text,
                                 final GalaxyWorkflowStep step, final GalaxyToolParameterMetadata parameter,
                                 final SpringLayout stepLayout, final Component previousStepComponent) {
        final JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(HEADER_2_FONT);
        stepPanel.add(titleLabel);
        final Component anchorComponent = previousStepComponent == null ? stepPanel : previousStepComponent;
        final String anchorEdge = previousStepComponent == null ? SpringLayout.NORTH : SpringLayout.SOUTH;
        stepLayout.putConstraint(SpringLayout.NORTH, titleLabel, DOUBLE_PAD, anchorEdge, anchorComponent);
        stepLayout.putConstraint(SpringLayout.WEST, titleLabel, SMALL_PAD, SpringLayout.WEST, stepPanel);
        stepLayout.putConstraint(SpringLayout.EAST, titleLabel, -SMALL_PAD, SpringLayout.EAST, stepPanel);
        final Component component;
        // todo: quick test to attempt editing some parameters (of the random lines twice workflow).
        if ("Randomly select".equals(title)) {
            final String parameterKey = step.getId() + "-" + parameter.getName();
            final JTextField textField = new JTextField(getFinalText(text, step, parameter));
            parameterTextFieldsMap.put(parameterKey, textField);
            component = textField;
        } else
            component = new JLabel(getFinalText(text, step, parameter));
        component.setFont(DEFAULT_GUI_FONT);
        stepPanel.add(component);
        stepLayout.putConstraint(SpringLayout.NORTH, component, SMALL_PAD, SpringLayout.SOUTH, titleLabel);
        stepLayout.putConstraint(SpringLayout.WEST, component, SMALL_PAD, SpringLayout.WEST, stepPanel);
        stepLayout.putConstraint(SpringLayout.EAST, component, -SMALL_PAD, SpringLayout.EAST, stepPanel);
        return component;
    }

    /**
     * Determine the text to show for an input or parameter.
     *
     * @param text the text of the input or the parameter.
     * @param step the Galaxy step.
     * @param parameter the parameter metadata.
     * @return the text to show for an input or parameter.
     */
    private String getFinalText(final String text, final GalaxyWorkflowStep step,
                                final GalaxyToolParameterMetadata parameter) {
        String finalText = text != null && !"".equals(text) ? text : EMPTY_VALUE;
        if (parameter != null) {
            final Map<String, Object> toolState = step.getToolState();
            final String parameterName = parameter.getName();
            final GalaxyStepInputConnection inputConnection = inputConnectionForParameter(parameterName, step);
            if (inputConnection != null)
                finalText = String.format("Output dataset '%s' from step %d", inputConnection.getOutputName(),
                                          inputConnection.getId() + 1);
            else if (toolState.containsKey(parameterName) && toolState.get(parameterName) != null)
                finalText = getFinalTextFromParameter(toolState, parameterName);
        }
        return finalText;
    }

    /**
     * Determine the text to show for a parameter.
     *
     * @param toolState the tool state from a Galaxy step.
     * @param parameterName the name of the parameter.
     * @return the text to show for a parameter.
     */
    private String getFinalTextFromParameter(final Map<String, Object> toolState, final String parameterName) {
        final Object value = toolState.get(parameterName);
        String finalText = value != null && !"".equals(value) ? value.toString() : EMPTY_VALUE;
        final List<String> booleanStrings = Arrays.asList("true", "false");
        if (value instanceof Map) {
            final Map valueMap = (Map) value;
            final String valueKey = "value";
            if (valueMap.containsKey(valueKey)) {
                final boolean unvalidated = "UnvalidatedValue".equals(valueMap.get("__class__"));
                finalText = valueMap.get(valueKey) + (unvalidated ? " (value not yet validated)" : "");
            }
        } else
            finalText = booleanStrings.contains(finalText.toLowerCase())
                        ? Character.toUpperCase(finalText.charAt(0)) + finalText.substring(1)
                        : finalText;
        return finalText;
    }

    /**
     * Determine whether a parameter is connected to an input connection. The end of the input connection name is
     * matched with the parameter name, since these input connection names can have a "queries_[0-9]+\|" prefix.
     *
     * @param parameterName the parameter name.
     * @param step the Galaxy step.
     * @return the related input connection or null.
     */
    private GalaxyStepInputConnection inputConnectionForParameter(final String parameterName,
                                                                  final GalaxyWorkflowStep step) {
        GalaxyStepInputConnection inputConnection = null;
        for (final Map.Entry<String, GalaxyStepInputConnection> inputConnectionEntry : step.getInputConnections().entrySet())
            if (inputConnectionEntry.getKey().endsWith(parameterName)) {
                inputConnection = inputConnectionEntry.getValue();
                break;
            }
        return inputConnection;
    }

    /**
     * Add conditional parameters (if there are any).
     *
     * @param stepPanel the step panel to add the new row to.
     * @param step the Galaxy step.
     * @param stepLayout the step layout to add GUI constrains to.
     * @param previousStepComponent the previous step component that was added to the GUI panel (used for layout).
     */
    private void addConditionalParameters(final JPanel stepPanel, final GalaxyWorkflowStep step,
                                          final SpringLayout stepLayout, final Component previousStepComponent) {
        Component previousComponent = previousStepComponent;
        for (final GalaxyToolConditional conditional : step.getToolMetadata().getConditionals()) {
            final GalaxyToolParameterMetadata parameter = conditional.getSelectorParameter();
            String text = EMPTY_VALUE;
            String selectedOptionValue = null;
            for (final GalaxyToolOption option : conditional.getOptions())
                if (option.isSelected()) {
                    text = option.getText();
                    selectedOptionValue = option.getValue();
                    break;
                }
            previousComponent = addStepRow(stepPanel, parameter.getLabel(), text, step, parameter, stepLayout,
                                           previousComponent);
            for (final GalaxyToolWhen when : conditional.getWhens())
                if (when.getValue().equals(selectedOptionValue))
                    for (final GalaxyToolParameterMetadata whenParameter : when.getParameters())
                        previousComponent = addStepRow(stepPanel, whenParameter.getLabel(),
                                                       whenParameter.getValue(), step, whenParameter,
                                                       stepLayout, previousComponent);
        }
    }

    /**
     * Adjust the preferred sizes of the step panels.
     *
     * @param visible whether the step panels should be visible.
     */
    private void adjustStepPanelSizes(final boolean visible) {
        final int minimumBottomY = 30;
        final int widthDecrement = 40;
        final int bottomIncrement = 10;
        for (final JPanel stepPanel : stepPanels) {
            stepPanel.setVisible(visible);
            int bottomY = minimumBottomY;
            if (visible)
                for (final Component stepComponent : stepPanel.getComponents())
                    bottomY = Math.max(bottomY, stepComponent.getY() + stepComponent.getHeight());
            final int height = visible ? bottomY + bottomIncrement : 0;
            stepPanel.setPreferredSize(new Dimension(FRAME_WIDTH - widthDecrement, height));
            stepPanel.revalidate();
        }
        guiPanel.revalidate();
    }

    /**
     * Run the workflow.
     *
     * @param waitForWorkflowToFinish whether the method should wait for the workflow to finish.
     */
    // todo: currently only works for the RandomLinesTwice workflow.
    protected void runWorkflow(final boolean waitForWorkflowToFinish) {
        // Create the thread to run the workflow.
        final Thread runWorkflowThread = new Thread(new Runnable() {
            @Override
            public void run() {
                runRandomLinesWorkflow(Guice.createInjector(new DefaultGuiceModule()), adaptGuiForRunningWorkflow());
            }
        });
        // Run the thread.
        runWorkflowThread.start();
        // If requested, wait for the thread to finish.
        if (waitForWorkflowToFinish)
            try {
                runWorkflowThread.join();
            } catch (final InterruptedException e) {
                logger.error("Exception while running a workflow", e);
            }
    }

    /**
     * Run the "random lines twice" workflow.
     *
     * @param injector the Guice injector to build the RandomLinesExample object.
     * @param resultsDocument the results document connected to the results text pane.
     */
    private void runRandomLinesWorkflow(final Injector injector, final StyledDocument resultsDocument) {
        final int initialLineCount = Integer.parseInt(parameterTextFieldsMap.get("1-num_lines").getText());
        final int definitiveLineCount = Integer.parseInt(parameterTextFieldsMap.get("2-num_lines").getText());
        //final WorkflowType workflowType = WorkflowType.GALAXY;
        final WorkflowType workflowType = WorkflowType.DEMONSTRATION;

        final RandomLinesExample randomLinesExample = injector.getInstance(RandomLinesExample.class);
        final List<String> outputLines = randomLinesExample.runExample(workflowType, initialLineCount,
                                                                       definitiveLineCount);

        if (outputLines != null) {
            final List<String> resultLines = new ArrayList<>();
            final String message = "The workflow ran successfully in %1.1f seconds and produced the following output:";
            resultLines.add(String.format(message, randomLinesExample.getDurationSeconds()));
            resultLines.add("");
            final String outputSeparator = "======";
            resultLines.add(outputSeparator);
            resultLines.addAll(outputLines);
            resultLines.add(outputSeparator);
            addLinesToResults(resultsDocument, resultLines.toArray(new String[resultLines.size()]));
        } else {
            final String message = "The workflow failed after running for %1.1f seconds.";
            addLinesToResults(resultsDocument, String.format(message, randomLinesExample.getDurationSeconds()));
        }
    }

    /**
     * Adapt the GUI for the running workflow by hiding the step panels and showing the results text pane.
     *
     * @return the results document connected to the results text pane.
     */
    private StyledDocument adaptGuiForRunningWorkflow() {
        // Add results text pane.
        final JTextPane resultsTextPane = new JTextPane();
        resultsTextPane.setEditable(false);
        resultsTextPane.setFont(RESULTS_FONT);
        final StyledDocument resultsDocument = new DefaultStyledDocument();
        final String initialText = String.format("Running workflow \"%s\"...", Constants.WORKFLOW_RANDOM_LINES_TWICE);
        addLinesToResults(resultsDocument, initialText, "", "");
        resultsTextPane.setDocument(resultsDocument);
        final JScrollPane resultsScrollPane = new JScrollPane(resultsTextPane);
        resultsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        resultsScrollPane.setBorder(new TitledBorder("Results"));
        guiPanel.add(resultsScrollPane);
        // Adjust step panel sizes.
        guiLayout.putConstraint(SpringLayout.NORTH, resultsScrollPane, QUAD_PAD, SpringLayout.SOUTH, runWorkflowButton);
        guiLayout.putConstraint(SpringLayout.WEST, resultsScrollPane, SMALL_PAD, SpringLayout.WEST, guiPanel);
        guiLayout.putConstraint(SpringLayout.EAST, resultsScrollPane, -SMALL_PAD, SpringLayout.EAST, guiPanel);
        guiLayout.putConstraint(SpringLayout.SOUTH, resultsScrollPane, -SMALL_PAD, SpringLayout.SOUTH, guiPanel);
        adjustStepPanelSizes(false);
        return resultsDocument;
    }

    /**
     * Add the lines to the results text pane.
     *
     * @param resultsDocument the results document connected to the results text pane.
     * @param lines the lines to add.
     */
    private void addLinesToResults(final StyledDocument resultsDocument, final String... lines) {
        try {
            for (final String line : lines)
                resultsDocument.insertString(resultsDocument.getLength(), line + "\n", null);
        } catch (final BadLocationException e) {
            logger.error("Exception while adding text to a PlainDocument object.", e);
        }
    }
}
