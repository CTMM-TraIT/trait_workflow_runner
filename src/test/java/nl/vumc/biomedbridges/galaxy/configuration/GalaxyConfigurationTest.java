/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.biomedbridges.galaxy.configuration;

import org.junit.Assert;
import org.junit.Test;

/**
 * The unit tests for the GalaxyConfiguration class.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class GalaxyConfigurationTest {
    // To be tested:

    // public static String buildConfiguration(final String galaxyInstanceUrl)

    // public static String buildConfiguration(final String galaxyInstanceUrl, final String galaxyApiKey,
    //                                         final String galaxyHistoryName)

    // public static String getGalaxyHistoryName()

    @Test
    public void testGetGalaxyHistoryName() {
        // todo: test setting the history name as well (using a properties file).
        Assert.assertNull(GalaxyConfiguration.getGalaxyHistoryName());
    }

    @Test
    public void testSetPropertiesFilePath() {
        // todo: test with an invalid, existing file too.
        GalaxyConfiguration.setPropertiesFilePath("");
        // todo: add more asserts.
        Assert.assertNull(GalaxyConfiguration.getGalaxyHistoryName());
    }
}
