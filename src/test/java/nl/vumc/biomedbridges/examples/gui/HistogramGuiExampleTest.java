/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.biomedbridges.examples.gui;

import java.awt.Component;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import nl.vumc.biomedbridges.core.Constants;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for the HistogramGuiExample class.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class HistogramGuiExampleTest {
    /**
     * Test the runGuiExample method.
     */
    @Test
    public void testRunGuiExample() throws InvocationTargetException, InterruptedException {
        final JFrame frame = new HistogramGuiExample().createGuiExample(Constants.WORKFLOW_HISTOGRAM, false);

        // Check some high level properties of the created GUI.
        Assert.assertNotNull(frame);
        assertFalse(frame.isVisible());
        final Component[] level1components = frame.getContentPane().getComponents();
        assertEquals(1, level1components.length);
        assertTrue(level1components[0] instanceof JPanel);
        final JPanel guiPanel = (JPanel) level1components[0];
        final Component[] level2components = guiPanel.getComponents();
        assertTrue(level2components[0] instanceof JLabel);
        assertEquals("Running workflow \"Histogram\"", ((JLabel) level2components[0]).getText());
        assertTrue(level2components[1] instanceof JPanel);
        final JPanel step1Panel = (JPanel) level2components[1];
        assertTrue(step1Panel.getBorder() instanceof TitledBorder);
        assertEquals("Step 1: Input dataset", ((TitledBorder) step1Panel.getBorder()).getTitle());
        assertTrue(level2components[2] instanceof JPanel);
        final JPanel step2Panel = (JPanel) level2components[2];
        assertTrue(step2Panel.getBorder() instanceof TitledBorder);
        assertEquals("Step 2: Histogram (version 1.0.3)", ((TitledBorder) step2Panel.getBorder()).getTitle());

        // Clean up by disposing the frame.
        frame.dispose();
    }
}
