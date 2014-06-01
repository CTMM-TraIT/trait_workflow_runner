/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.biomedbridges.examples.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import nl.vumc.biomedbridges.galaxy.metadata.GalaxyStepInput;
import nl.vumc.biomedbridges.galaxy.metadata.GalaxyStepInputConnection;
import nl.vumc.biomedbridges.galaxy.metadata.GalaxyToolParameterMetadata;
import nl.vumc.biomedbridges.galaxy.metadata.GalaxyWorkflowEngineMetadata;
import nl.vumc.biomedbridges.galaxy.metadata.GalaxyWorkflowMetadata;
import nl.vumc.biomedbridges.galaxy.metadata.GalaxyWorkflowStep;

/**
 * This class contains a simple example of the workflow running functionality: the metadata for the histogram workflow
 * is used to build a simple read-only Swing GUI that looks a bit like the web interface of this workflow in Galaxy.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class HistogramGuiExample {
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
    private static final Font TITLE_FONT = new Font(FONT_NAME, Font.BOLD, 18);

    /**
     * The header level 2 font.
     */
    private static final Font HEADER_2_FONT = new Font(FONT_NAME, Font.BOLD, 12);

    /**
     * The default font.
     */
    private static final Font DEFAULT_FONT = new Font(FONT_NAME, Font.PLAIN, 12);

    /**
     * Main method.
     *
     * @param arguments unused command-line arguments.
     */
    // CHECKSTYLE_OFF: UncommentedMain
    public static void main(final String[] arguments) {
        // Schedule a job for the event-dispatching thread: creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new HistogramGuiExample().runGuiExample();
            }
        });
    }
    // CHECKSTYLE_ON: UncommentedMain

    /**
     * Run the GUI example with a visible frame.
     *
     * @return the new frame.
     */
    protected JFrame runGuiExample() {
        return runGuiExample(true);
    }

    /**
     * Run the GUI example with a visible or invisible frame.
     *
     * @param makeVisible whether the frame should be visible or invisible.
     * @return the new frame.
     */
    protected JFrame runGuiExample(final boolean makeVisible) {
        final String workflowName = "Histogram";
        final JFrame frame = new JFrame(workflowName + " gui example");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        final SpringLayout guiLayout = new SpringLayout();
        final JPanel guiPanel = new JPanel(guiLayout);
        guiPanel.setName("guiPanel");
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(guiPanel, BorderLayout.CENTER);
        final JLabel titleLabel = new JLabel("Running workflow \"" + workflowName + "\"");
        titleLabel.setName("titleLabel(" + titleLabel.getText() + ")");
        titleLabel.setFont(TITLE_FONT);
        guiPanel.add(titleLabel);
        guiLayout.putConstraint(SpringLayout.WEST, titleLabel, 5, SpringLayout.WEST, guiPanel);
        guiLayout.putConstraint(SpringLayout.NORTH, titleLabel, 5, SpringLayout.NORTH, guiPanel);
        Component previousComponent = titleLabel;
        final GalaxyWorkflowMetadata workflowMetadata = new GalaxyWorkflowEngineMetadata().getWorkflow(workflowName);
        for (int stepIndex = 0; stepIndex < workflowMetadata.getSteps().size(); stepIndex++)
            previousComponent = addStepPanel(workflowMetadata, stepIndex, guiPanel, guiLayout, previousComponent);

        // Center and show.
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setVisible(makeVisible);
        adjustStepPanelSizes(guiPanel);

        System.out.println();
        printComponentSizes(guiPanel);

        return frame;
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
    private JPanel addStepPanel(final GalaxyWorkflowMetadata workflowMetadata, int stepIndex, final JPanel guiPanel,
            final SpringLayout guiLayout, Component previousComponent) {
        final JPanel stepPanel = new JPanel();
        stepPanel.setName("stepPanel(" + (stepIndex + 1) + ")");
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
        if (step.getToolMetadata() != null)
            for (final GalaxyToolParameterMetadata parameter : step.getToolMetadata().getParameters())
                previousStepComponent = addStepRow(stepPanel, parameter.getLabel(), parameter.getValue(), step,
                                                   parameter, stepLayout, previousStepComponent);
        guiPanel.add(stepPanel);
        guiLayout.putConstraint(SpringLayout.NORTH, stepPanel, 5, SpringLayout.SOUTH, previousComponent);
        guiLayout.putConstraint(SpringLayout.WEST, stepPanel, 5, SpringLayout.WEST, guiPanel);
        guiLayout.putConstraint(SpringLayout.EAST, stepPanel, 20, SpringLayout.EAST, guiPanel);
        return stepPanel;
    }

    /**
     * Add a new row to a Galaxy step panel for an input or a parameter.
     *
     * @param stepPanel the step panel to add the new row to.
     * @param title the title of the input or the parameter.
     * @param text the text of the input or the parameter.
     * @param step the Galaxy step.
     * @param parameter the parameter metadata.
     * @return the last component that was added to the step panel.
     */
    private Component addStepRow(final JPanel stepPanel, final String title, final String text,
                                 final GalaxyWorkflowStep step, final GalaxyToolParameterMetadata parameter,
                                 final SpringLayout stepLayout, final Component previousStepComponent) {
        final JLabel titleLabel = new JLabel(title);
        titleLabel.setName("titleLabel(" + titleLabel.getText() + ")");
        titleLabel.setFont(HEADER_2_FONT);
        stepPanel.add(titleLabel);
        final Component anchorComponent = previousStepComponent == null ? stepPanel : previousStepComponent;
        final String anchorEdge = previousStepComponent == null ? SpringLayout.NORTH : SpringLayout.SOUTH;
        stepLayout.putConstraint(SpringLayout.NORTH, titleLabel, 10, anchorEdge, anchorComponent);
        stepLayout.putConstraint(SpringLayout.WEST, titleLabel, 5, SpringLayout.WEST, stepPanel);
        stepLayout.putConstraint(SpringLayout.EAST, titleLabel, 5, SpringLayout.EAST, stepPanel);
        final String finalText;
        if (parameter != null && step.getInputConnections().containsKey(parameter.getName())) {
            final GalaxyStepInputConnection inputConnection = step.getInputConnections().get(parameter.getName());
            finalText = String.format("Output dataset '%s' from step %d", inputConnection.getOutputName(),
                                 inputConnection.getId() + 1);
        } else
            finalText = text != null && !"".equals(text) ? text : "------";
        final JLabel textLabel = new JLabel(finalText);
        textLabel.setName("textLabel(" + textLabel.getText() + ")");
        textLabel.setFont(DEFAULT_FONT);
        stepPanel.add(textLabel);
        stepLayout.putConstraint(SpringLayout.NORTH, textLabel, 5, SpringLayout.SOUTH, titleLabel);
        stepLayout.putConstraint(SpringLayout.WEST, textLabel, 5, SpringLayout.WEST, stepPanel);
        stepLayout.putConstraint(SpringLayout.EAST, textLabel, 5, SpringLayout.EAST, stepPanel);
        return textLabel;
    }

    /**
     * Adjust the preferred sizes of the step panels.
     *
     * @param guiPanel the GUI panel that contains the step panels.
     */
    private void adjustStepPanelSizes(final JPanel guiPanel) {
        System.out.println();
        for (final Component component : guiPanel.getComponents())
            if (component instanceof JPanel) {
                final JPanel stepPanel = (JPanel) component;
                int bottomY = 30;
                for (final Component stepComponent : stepPanel.getComponents())
                    bottomY = Math.max(bottomY, stepComponent.getY() + stepComponent.getHeight());
                System.out.println("bottomY: " + bottomY);
                stepPanel.setPreferredSize(new Dimension(FRAME_WIDTH - 40, bottomY + 10));
                stepPanel.revalidate();
            }
    }

    /**
     * Print component sizes recursively for debugging.
     *
     * @param component the current component.
     */
    private void printComponentSizes(final Component component) {
        System.out.println("component " + component.getName() + " - bounds: " + component.getBounds());
        if (component instanceof Container)
            for (final Component subComponent : ((Container) component).getComponents())
                printComponentSizes(subComponent);
    }
}
