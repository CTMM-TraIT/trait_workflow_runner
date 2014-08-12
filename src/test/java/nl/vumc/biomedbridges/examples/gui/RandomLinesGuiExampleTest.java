/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.examples.gui;

import java.awt.Component;
import java.awt.Container;
import java.lang.reflect.InvocationTargetException;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import nl.vumc.biomedbridges.core.Constants;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for the RandomLinesGuiExample class. This test focuses on the run workflow functionality.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class RandomLinesGuiExampleTest {
    /**
     * Test the runGuiExample method.
     */
    @Test
    public void testRunGuiExample() throws InvocationTargetException, InterruptedException, BadLocationException {
        final RandomLinesGuiExample guiExample = new RandomLinesGuiExample();
        final JFrame frame = guiExample.createGuiExample(Constants.WORKFLOW_RANDOM_LINES_TWICE, false);
        checkInitialGuiState(frame);
        checkRunningWorkflow(guiExample, frame);
        frame.dispose();
    }

    /**
     * Check some high level properties of the created GUI.
     *
     * @param frame the frame that contains the GUI.
     */
    private void checkInitialGuiState(final JFrame frame) {
        Assert.assertNotNull(frame);
        assertFalse(frame.isVisible());
        final Component[] level1components = frame.getContentPane().getComponents();
        assertEquals(1, level1components.length);
        assertTrue(level1components[0] instanceof JPanel);
        final JPanel guiPanel = (JPanel) level1components[0];
        final Component[] level2components = guiPanel.getComponents();
        assertEquals(7, level2components.length);
        assertTrue(level2components[0] instanceof JLabel);
        assertEquals("Running workflow \"RandomLinesTwice\"", ((JLabel) level2components[0]).getText());
        assertTrue(level2components[1] instanceof JLabel);
        assertEquals("Random Lines", ((JLabel) level2components[1]).getText());
        assertTrue(level2components[2] instanceof JSeparator);
        assertTrue(level2components[3] instanceof JPanel);
        final JPanel step1Panel = (JPanel) level2components[3];
        assertTrue(step1Panel.getBorder() instanceof TitledBorder);
        assertEquals("Step 1: Input dataset", ((TitledBorder) step1Panel.getBorder()).getTitle());
        assertTrue(level2components[4] instanceof JPanel);
        final JPanel step2Panel = (JPanel) level2components[4];
        assertTrue(step2Panel.getBorder() instanceof TitledBorder);
        assertEquals("Step 2: Select random lines (version 2.0.1)", ((TitledBorder) step2Panel.getBorder()).getTitle());
    }

    /**
     * Run the workflow and check the results.
     *
     * @param guiExample the GUI example object.
     * @param frame the frame that contains the GUI.
     * @throws BadLocationException if retrieving the results from the GUI failed.
     */
    private void checkRunningWorkflow(final RandomLinesGuiExample guiExample, final JFrame frame)
            throws BadLocationException {
        final JButton button = findButton(frame);
        if (button != null) {
            assertEquals("Run workflow", button.getText());
            assertEquals(1, button.getActionListeners().length);

            guiExample.runWorkflow(true);

            final JTextPane textPane = findTextPane(frame);
            assertNotNull(textPane);

            final String expectedRegularExpression = "Running workflow \"RandomLinesTwice\"...\n" +
                                                     "\n" +
                                                     "\n" +
                                                     "The workflow ran successfully in " +
                                                     ".*" +
                                                     " and produced the following output:\n" +
                                                     "\n" +
                                                     "======\n" +
                                                     "7\n" +
                                                     "6\n" +
                                                     "======\n";

            final StyledDocument document = textPane.getStyledDocument();
            final String results = document.getText(0, document.getLength());
            assertTrue(Pattern.compile(expectedRegularExpression).matcher(results).matches());
        }
    }

    /**
     * Search for a button component in the GUI hierarchy.
     *
     * @param container the container to search in.
     * @return the first button component encountered or null.
     */
    private JButton findButton(final Container container) {
        JButton result = null;
        int componentIndex = 0;
        while (result == null && componentIndex < container.getComponentCount()) {
            final Component component = container.getComponent(componentIndex);
            if (component instanceof JButton)
                result = (JButton) component;
            else if (component instanceof Container)
                result = findButton((Container) component);
            componentIndex++;
        }
        return result;
    }

    /**
     * Search for a text pane component in the GUI hierarchy.
     *
     * @param container the container to search in.
     * @return the first text pane component encountered or null.
     */
    private JTextPane findTextPane(final Container container) {
        JTextPane result = null;
        int componentIndex = 0;
        while (result == null && componentIndex < container.getComponentCount()) {
            final Component component = container.getComponent(componentIndex);
            if (component instanceof JTextPane)
                result = (JTextPane) component;
            else if (component instanceof Container)
                result = findTextPane((Container) component);
            componentIndex++;
        }
        return result;
    }
}
