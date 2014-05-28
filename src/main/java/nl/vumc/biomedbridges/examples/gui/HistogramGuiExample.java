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

//    /**
//     * Create the GUI and show it. For thread safety, this method should be invoked from the event-dispatching thread.
//     */
//    private void springLayoutTest() {
//        // Create and set up the window.
//        final JFrame frame = new JFrame("SpringDemo3");
//        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
// 
//        // Set up the content pane.
//        final Container contentPane = frame.getContentPane();
//        final SpringLayout layout = new SpringLayout();
//        contentPane.setLayout(layout);
// 
//        // Create and add the components.
//        final JLabel label = new JLabel("Label: ");
//        final JTextField textField = new JTextField("Text field", 15);
//        contentPane.add(label);
//        contentPane.add(textField);
// 
//        // Adjust constraints for the label so it's at (5, 5).
//        layout.putConstraint(SpringLayout.WEST, label, 5, SpringLayout.WEST, contentPane);
//        layout.putConstraint(SpringLayout.NORTH, label, 5, SpringLayout.NORTH, contentPane);
// 
//        // Adjust constraints for the text field so it's at (<label's right edge> + 5, 5).
//        layout.putConstraint(SpringLayout.WEST, textField, 5, SpringLayout.EAST, label);
//        layout.putConstraint(SpringLayout.NORTH, textField, 5, SpringLayout.NORTH, contentPane);
// 
////        // Adjust constraints for the content pane: its right edge should be 5 pixels beyond the text field's right edge,
////        // and its bottom edge should be 5 pixels beyond the bottom edge of the tallest component (which we'll assume is 
////        // textField).
////        layout.putConstraint(SpringLayout.EAST, contentPane, 5, SpringLayout.EAST, textField);
////        layout.putConstraint(SpringLayout.SOUTH, contentPane, 5, SpringLayout.SOUTH, textField);
// 
//        // Display the window.
//        frame.pack();
//        frame.setLocationRelativeTo(null);
//        frame.setVisible(true);
//    }
    
    private void runGuiExample() {
        final String workflowName = "Histogram";
        final JFrame frame = new JFrame(workflowName + " gui example");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        final SpringLayout guiLayout = new SpringLayout();
        final JPanel guiPanel = new JPanel(guiLayout);
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
        frame.setVisible(true);
    }

    private JPanel addStepPanel(final GalaxyWorkflowMetadata workflowMetadata, int stepIndex, final JPanel guiPanel, 
            final SpringLayout guiLayout, Component previousComponent) {
        final JPanel stepPanel = new JPanel();
        //stepPanel.setMinimumSize(new Dimension(600, 100));
        final BoxLayout stepLayout = new BoxLayout(stepPanel, BoxLayout.PAGE_AXIS);
        //final SpringLayout stepLayout = new SpringLayout();
        stepPanel.setLayout(stepLayout);
        final GalaxyWorkflowStep step = workflowMetadata.getSteps().get(stepIndex);
        final String toolVersion = step.getToolVersion();
        final String stepText = "Step " + (stepIndex + 1) + ": " + step.getName() 
                + (toolVersion != null ? " (version " + toolVersion + ")" : "");
        //stepPanel.add(new JLabel(stepText));
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

//    /**
//     * Create the GUI and show it. For thread safety, this method should be invoked from the event-dispatching thread.
//     */
//    private void createAndShowGUI() {
//        // Create and set up the window.
//        final JFrame frame = new JFrame("SpringDemo3");
//        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
// 
//        // Set up the content pane.
//        final Container contentPane = frame.getContentPane();
//        final SpringLayout layout = new SpringLayout();
//        contentPane.setLayout(layout);
// 
//        // Create and add the components.
//        final JLabel label = new JLabel("Label: ");
//        final JTextField textField = new JTextField("Text field", 15);
//        contentPane.add(label);
//        contentPane.add(textField);
// 
//        // Adjust constraints for the label so it's at (5, 5).
//        layout.putConstraint(SpringLayout.WEST, label, 5, SpringLayout.WEST, contentPane);
//        layout.putConstraint(SpringLayout.NORTH, label, 5, SpringLayout.NORTH, contentPane);
// 
//        // Adjust constraints for the text field so it's at (<label's right edge> + 5, 5).
//        layout.putConstraint(SpringLayout.WEST, textField, 5, SpringLayout.EAST, label);
//        layout.putConstraint(SpringLayout.NORTH, textField, 5, SpringLayout.NORTH, contentPane);
// 
//        // Adjust constraints for the content pane: its right edge should be 5 pixels beyond the text field's right edge,
//        // and its bottom edge should be 5 pixels beyond the bottom edge of the tallest component (which we'll assume is 
//        // textField).
//        layout.putConstraint(SpringLayout.EAST, contentPane, 5, SpringLayout.EAST, textField);
//        layout.putConstraint(SpringLayout.SOUTH, contentPane, 5, SpringLayout.SOUTH, textField);
// 
//        // Display the window.
//        frame.pack();
//        frame.setLocationRelativeTo(null);
//        frame.setVisible(true);
//    }
}
