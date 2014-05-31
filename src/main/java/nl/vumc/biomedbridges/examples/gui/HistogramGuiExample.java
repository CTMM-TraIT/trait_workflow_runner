package nl.vumc.biomedbridges.examples.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import nl.vumc.biomedbridges.galaxy.metadata.GalaxyStepInput;
import nl.vumc.biomedbridges.galaxy.metadata.GalaxyToolParameterMetadata;
import nl.vumc.biomedbridges.galaxy.metadata.GalaxyWorkflowEngineMetadata;
import nl.vumc.biomedbridges.galaxy.metadata.GalaxyWorkflowMetadata;
import nl.vumc.biomedbridges.galaxy.metadata.GalaxyWorkflowStep;

public class HistogramGuiExample {
    private static final Font TITLE_FONT = new Font("sans-serif", Font.BOLD, 18);
    private static final Font HEADER_2_FONT = new Font("sans-serif", Font.BOLD, 12);
    private static final Font DEFAULT_FONT = new Font("sans-serif", Font.PLAIN, 11);
    
    public static void main(final String[] arguments) {
        // Schedule a job for the event-dispatching thread: creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new HistogramGuiExample().runGuiExample();
                //new HistogramGuiExample().springLayoutTest();
            }
        });
    }

    protected JFrame runGuiExample() {
        return runGuiExample(true);
    }

    protected JFrame runGuiExample(final boolean makeVisible) {
        final String workflowName = "Histogram";
        final JFrame frame = new JFrame(workflowName + " gui example");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        final SpringLayout guiLayout = new SpringLayout();
        final JPanel guiPanel = new JPanel(guiLayout);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(guiPanel, BorderLayout.CENTER);
        final JLabel titleLabel = new JLabel("Running workflow \"" + workflowName + "\"");
        titleLabel.setFont(TITLE_FONT);
        guiPanel.add(titleLabel);
        guiLayout.putConstraint(SpringLayout.WEST, titleLabel, 5, SpringLayout.WEST, guiPanel);
        guiLayout.putConstraint(SpringLayout.NORTH, titleLabel, 5, SpringLayout.NORTH, guiPanel);
        Component previousComponent = titleLabel;
        final GalaxyWorkflowMetadata workflowMetadata = new GalaxyWorkflowEngineMetadata().getWorkflow(workflowName);
        for (int stepIndex = 0; stepIndex < workflowMetadata.getSteps().size(); stepIndex++)
            previousComponent = addStepPanel(workflowMetadata, stepIndex, guiPanel, guiLayout, previousComponent);

        // Center and show.
        //frame.pack();
        frame.setSize(600, 480);
        frame.setLocationRelativeTo(null);
        frame.setVisible(makeVisible);
        return frame;
    }

    private JPanel addStepPanel(final GalaxyWorkflowMetadata workflowMetadata, int stepIndex, final JPanel guiPanel, 
            final SpringLayout guiLayout, Component previousComponent) {
        final JPanel stepPanel = new JPanel();
        final GalaxyWorkflowStep step = workflowMetadata.getSteps().get(stepIndex);
        final String toolVersion = step.getToolVersion();
        final String stepText = "Step " + (stepIndex + 1) + ": " + step.getName()
                                + (toolVersion != null ? " (version " + toolVersion + ")" : "");
        final BoxLayout stepLayout = new BoxLayout(stepPanel, BoxLayout.PAGE_AXIS);
        //final SpringLayout stepLayout = new SpringLayout();
        stepPanel.setLayout(stepLayout);
        stepPanel.setBorder(new TitledBorder(stepText));
        Component previousStepComponent = null;
        for (final GalaxyStepInput stepInput : step.getInputs())
            previousStepComponent = addStepRow(stepPanel, stepInput.getName(), stepInput.getDescription(), stepLayout, 
                    previousStepComponent);
        if (step.getToolMetadata() != null)
            for (final GalaxyToolParameterMetadata parameter : step.getToolMetadata().getParameters())
                previousStepComponent = addStepRow(stepPanel, parameter.getLabel(), parameter.getValue(), stepLayout, 
                    previousStepComponent);
        guiPanel.add(stepPanel);
        guiLayout.putConstraint(SpringLayout.NORTH, stepPanel, 5, SpringLayout.SOUTH, previousComponent);
        guiLayout.putConstraint(SpringLayout.WEST, stepPanel, 5, SpringLayout.WEST, guiPanel);
        guiLayout.putConstraint(SpringLayout.EAST, stepPanel, 20, SpringLayout.EAST, guiPanel);
        return stepPanel;
    }

    private JLabel addStepRow(final JPanel stepPanel, final String header, final String value,
            final BoxLayout/*SpringLayout*/ stepLayout, final Component previousStepComponent) {
        final JLabel nameLabel = new JLabel(header);
        nameLabel.setFont(HEADER_2_FONT);
        stepPanel.add(nameLabel);
//        stepLayout.putConstraint(SpringLayout.NORTH, nameLabel, 5, SpringLayout.SOUTH, previousStepComponent);
//        stepLayout.putConstraint(SpringLayout.WEST, nameLabel, 5, SpringLayout.WEST, stepPanel);
//        stepLayout.putConstraint(SpringLayout.EAST, nameLabel, 5, SpringLayout.EAST, stepPanel);
        final JLabel descriptionLabel = new JLabel(value);
        descriptionLabel.setFont(DEFAULT_FONT);
        stepPanel.add(descriptionLabel);
//        stepLayout.putConstraint(SpringLayout.NORTH, descriptionLabel, 5, SpringLayout.SOUTH, nameLabel);
//        stepLayout.putConstraint(SpringLayout.WEST, descriptionLabel, 5, SpringLayout.WEST, stepPanel);
//        stepLayout.putConstraint(SpringLayout.EAST, descriptionLabel, 5, SpringLayout.EAST, stepPanel);
        return descriptionLabel;
    }
}
