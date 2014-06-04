/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.examples.gui;

import nl.vumc.biomedbridges.core.Constants;

/**
 * This class contains a simple example of the workflow running functionality: the metadata for the random lines twice
 * workflow is used to build a simple read-only Swing GUI that looks a bit like the web interface of this workflow in
 * Galaxy.
 * All the interesting code is in the BaseGuiExample class.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class RandomLinesGuiExample extends BaseGuiExample {
    /**
     * Main method.
     *
     * @param arguments unused command-line arguments.
     */
    // CHECKSTYLE_OFF: UncommentedMain
    public static void main(final String[] arguments) {
        new RandomLinesGuiExample().createGuiExample(Constants.WORKFLOW_RANDOM_LINES_TWICE);
    }
    // CHECKSTYLE_ON: UncommentedMain
}
