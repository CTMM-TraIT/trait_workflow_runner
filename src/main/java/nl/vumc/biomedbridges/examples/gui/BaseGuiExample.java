/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.examples.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import nl.vumc.biomedbridges.galaxy.metadata.GalaxyStepInput;
import nl.vumc.biomedbridges.galaxy.metadata.GalaxyStepInputConnection;
import nl.vumc.biomedbridges.galaxy.metadata.GalaxyToolConditional;
import nl.vumc.biomedbridges.galaxy.metadata.GalaxyToolOption;
import nl.vumc.biomedbridges.galaxy.metadata.GalaxyToolParameterMetadata;
import nl.vumc.biomedbridges.galaxy.metadata.GalaxyToolWhen;
import nl.vumc.biomedbridges.galaxy.metadata.GalaxyWorkflowEngineMetadata;
import nl.vumc.biomedbridges.galaxy.metadata.GalaxyWorkflowMetadata;
import nl.vumc.biomedbridges.galaxy.metadata.GalaxyWorkflowStep;

/**
 * This class contains all the base code for using the metadata for a Galaxy workflow to build a simple read-only Swing 
 * GUI that looks a bit like the web interface of this workflow in Galaxy.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class BaseGuiExample {
    /**
     * The frame width.
     */
    private static final int FRAME_WIDTH = 800;

    /**
     * The frame height.
     */
    private static final int FRAME_HEIGHT = 600;

    /**
     * The font name.
     */
    private static final String FONT_NAME = "sans-serif";

    /**
     * The title font.
     */
    private static final Font TITLE_FONT = new Font(FONT_NAME, Font.BOLD, 24);

    /**
     * The header level 2 font.
     */
    private static final Font HEADER_2_FONT = new Font(FONT_NAME, Font.BOLD, 14);

    /**
     * The default font.
     */
    private static final Font DEFAULT_FONT = new Font(FONT_NAME, Font.PLAIN, 14);

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
        } catch (InvocationTargetException | InterruptedException e) {
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
        final SpringLayout guiLayout = new SpringLayout();
        final JPanel guiPanel = new JPanel(guiLayout);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(guiPanel, BorderLayout.CENTER);
        final GalaxyWorkflowMetadata workflowMetadata = new GalaxyWorkflowEngineMetadata().getWorkflow(workflowName);
        final String titleText = "Running workflow \"" + workflowName + "\"";
        final JLabel titleLabel = addLabel(guiPanel, guiLayout, titleText, TITLE_FONT, null);
        final String annotation = workflowMetadata.getAnnotation();
        final JLabel annotationLabel = annotation != null
                                       ? addLabel(guiPanel, guiLayout, annotation, DEFAULT_FONT, titleLabel)
                                       : null;
        // todo: Fix this cosmetic change of the annotation label constraint.
        if (annotationLabel != null)
            guiLayout.getConstraint(SpringLayout.WEST, annotationLabel).setValue(SMALL_PAD + 1);
        final JLabel previousLabel = annotationLabel != null ? annotationLabel : titleLabel;
        final JSeparator separatorLine = new JSeparator(SwingConstants.HORIZONTAL);
        guiPanel.add(separatorLine);
        guiLayout.putConstraint(SpringLayout.NORTH, separatorLine, QUAD_PAD, SpringLayout.SOUTH, previousLabel);
        guiLayout.putConstraint(SpringLayout.WEST, separatorLine, SMALL_PAD, SpringLayout.WEST, guiPanel);
        guiLayout.putConstraint(SpringLayout.EAST, separatorLine, SMALL_PAD, SpringLayout.EAST, guiPanel);
        Component previousComponent = separatorLine;
        for (int stepIndex = 0; stepIndex < workflowMetadata.getSteps().size(); stepIndex++)
            previousComponent = addStepPanel(workflowMetadata, stepIndex, guiPanel, guiLayout, previousComponent);

        // Center the frame and show it.
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setVisible(makeVisible);
        adjustStepPanelSizes(guiPanel);

        return frame;
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
        final JLabel label = new JLabel(text);
        label.setFont(font);
        panel.add(label);
        final Component anchorComponent = previousComponent == null ? panel : previousComponent;
        final String anchorEdge = previousComponent == null ? SpringLayout.NORTH : SpringLayout.SOUTH;
        springLayout.putConstraint(SpringLayout.NORTH, label, SMALL_PAD, anchorEdge, anchorComponent);
        springLayout.putConstraint(SpringLayout.WEST, label, SMALL_PAD, SpringLayout.WEST, panel);
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
                previousStepComponent = addStepRow(stepPanel, parameter.getLabel(), text, step, parameter, stepLayout,
                                                   previousStepComponent);
                for (final GalaxyToolWhen when : conditional.getWhens())
                    if ((when.getValue().equals(selectedOptionValue)))
                        for (final GalaxyToolParameterMetadata whenParameter : when.getParameters())
                            previousStepComponent = addStepRow(stepPanel, whenParameter.getLabel(),
                                                               whenParameter.getValue(), step, whenParameter,
                                                               stepLayout, previousStepComponent);
            }
        }
        guiPanel.add(stepPanel);
        guiLayout.putConstraint(SpringLayout.NORTH, stepPanel, SMALL_PAD, SpringLayout.SOUTH, previousComponent);
        guiLayout.putConstraint(SpringLayout.WEST, stepPanel, SMALL_PAD, SpringLayout.WEST, guiPanel);
        guiLayout.putConstraint(SpringLayout.EAST, stepPanel, QUAD_PAD, SpringLayout.EAST, guiPanel);
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
        stepLayout.putConstraint(SpringLayout.EAST, titleLabel, SMALL_PAD, SpringLayout.EAST, stepPanel);
        final JLabel textLabel = new JLabel(getFinalText(text, step, parameter));
        textLabel.setFont(DEFAULT_FONT);
        stepPanel.add(textLabel);
        stepLayout.putConstraint(SpringLayout.NORTH, textLabel, SMALL_PAD, SpringLayout.SOUTH, titleLabel);
        stepLayout.putConstraint(SpringLayout.WEST, textLabel, SMALL_PAD, SpringLayout.WEST, stepPanel);
        stepLayout.putConstraint(SpringLayout.EAST, textLabel, SMALL_PAD, SpringLayout.EAST, stepPanel);
        return textLabel;
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
        final String finalText;
        if (parameter != null) {
            final Map<String, Object> toolState = step.getToolState();
            final String parameterName = parameter.getName();
            final GalaxyStepInputConnection inputConnection = inputConnectionForParameter(parameterName, step);
            if (inputConnection != null) {
                finalText = String.format("Output dataset '%s' from step %d", inputConnection.getOutputName(),
                                          inputConnection.getId() + 1);
            } else if (toolState.containsKey(parameterName) && toolState.get(parameterName) != null) {
                final Object value = toolState.get(parameterName);
                if (value instanceof Map) {
                    final Map valueMap = (Map) value;
                    final String valueKey = "value";
                    if (valueMap.containsKey(valueKey)) {
                        final boolean unvalidated = "UnvalidatedValue".equals(valueMap.get("__class__"));
                        finalText = valueMap.get(valueKey) + (unvalidated ? " (value not yet validated)" : "");
                    } else
                        finalText = !"".equals(value) ? value.toString() : EMPTY_VALUE;
                } else {
                    final String valueString = value != null  && !"".equals(value) ? value.toString() : EMPTY_VALUE;
                    finalText = "true".equalsIgnoreCase(valueString) || "false".equalsIgnoreCase(valueString)
                                ? Character.toUpperCase(valueString.charAt(0)) + valueString.substring(1)
                                : valueString;
                }
            } else
                finalText = text != null && !"".equals(text) ? text : EMPTY_VALUE;
        } else
            finalText = text != null && !"".equals(text) ? text : EMPTY_VALUE;
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
     * Adjust the preferred sizes of the step panels.
     *
     * @param guiPanel the GUI panel that contains the step panels.
     */
    private void adjustStepPanelSizes(final JPanel guiPanel) {
        final int minimumBottomY = 30;
        final int widthDecrement = 40;
        final int bottomIncrement = 10;
        for (final Component component : guiPanel.getComponents())
            if (component instanceof JPanel) {
                final JPanel stepPanel = (JPanel) component;
                int bottomY = minimumBottomY;
                for (final Component stepComponent : stepPanel.getComponents())
                    bottomY = Math.max(bottomY, stepComponent.getY() + stepComponent.getHeight());
                stepPanel.setPreferredSize(new Dimension(FRAME_WIDTH - widthDecrement, bottomY + bottomIncrement));
                stepPanel.revalidate();
            }
    }
}
