/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.galaxy.configuration;

import java.io.File;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNull;

/**
 * The unit tests for the GalaxyConfiguration class.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class GalaxyConfigurationTest {
    /**
     * The resources directory for the configuration package.
     */
    private static final String CONFIGURATION_DIRECTORY = Paths.get(
            "src", "test", "resources", "nl", "vumc", "biomedbridges", "galaxy", "configuration"
    ) + File.separator;

    /**
     * Reset all static fields of the class to be tested before each unit test to prevent interference.
     */
    @Before
    public void setUp() {
        GalaxyConfiguration.resetStaticFields();
    }

    /**
     * Test the getGalaxyHistoryName method: check that the default history name is null.
     */
    @Test
    public void testGetGalaxyHistoryNameDefault() {
        assertNull(GalaxyConfiguration.getGalaxyHistoryName());
    }

    /**
     * Test the setPropertiesFilePath and getGalaxyHistoryName methods: check that a history name set in a properties
     * file is used.
     */
    @Test
    public void testGetGalaxyHistoryNameFromProperties() {
        GalaxyConfiguration.setPropertiesFilePath(CONFIGURATION_DIRECTORY + "HistoryName.properties");
        assertNull(GalaxyConfiguration.getGalaxyInstanceUrl());
        assertNull(GalaxyConfiguration.getGalaxyApiKey());
        Assert.assertEquals("GalaxyConfigurationTest-1", GalaxyConfiguration.getGalaxyHistoryName());
    }

    /**
     * Test the setPropertiesFilePath method: passing the empty string results in null for all properties.
     */
    @Test
    public void testSetPropertiesFilePathToEmptyString() {
        GalaxyConfiguration.setPropertiesFilePath("");
        assertNull(GalaxyConfiguration.getGalaxyInstanceUrl());
        assertNull(GalaxyConfiguration.getGalaxyApiKey());
        assertNull(GalaxyConfiguration.getGalaxyHistoryName());
    }

    /**
     * Test the setPropertiesFilePath method: an empty properties file results in null for all properties.
     */
    @Test
    public void testSetPropertiesFilePathToEmptyFile() {
        GalaxyConfiguration.setPropertiesFilePath(CONFIGURATION_DIRECTORY + "Empty.properties");
        assertNull(GalaxyConfiguration.getGalaxyInstanceUrl());
        assertNull(GalaxyConfiguration.getGalaxyApiKey());
        assertNull(GalaxyConfiguration.getGalaxyHistoryName());
    }

    /**
     * Test the setPropertiesFilePath method: a properties file with all properties set results in the right value for
     * all properties.
     */
    @Test
    public void testSetPropertiesFilePathToAllPropertiesFile() {
        GalaxyConfiguration.setPropertiesFilePath(CONFIGURATION_DIRECTORY + "All.properties");
        Assert.assertEquals("aaaaaa", GalaxyConfiguration.getGalaxyInstanceUrl());
        Assert.assertEquals("bbbbbb", GalaxyConfiguration.getGalaxyApiKey());
        Assert.assertEquals("GalaxyConfigurationTest-1", GalaxyConfiguration.getGalaxyHistoryName());
    }

    /**
     * Test the buildConfiguration method by specifying the galaxy instance only.
     */
    @Test
    public void testBuildConfigurationGalaxyInstanceOnly() {
        final String message = GalaxyConfiguration.setPropertiesFilePath(CONFIGURATION_DIRECTORY + "All.properties");
        assertNull(message);
        final String expectedConfigurationString = "test.galaxy.instance=a6|" +
                                                   "test.galaxy.key=bbbbbb|" +
                                                   "galaxy.history.name=GalaxyConfigurationTest-1";
        Assert.assertEquals(expectedConfigurationString, GalaxyConfiguration.buildConfiguration("a6"));
    }

    /**
     * Test the buildConfiguration method by specifying all parameters.
     */
    @Test
    public void testBuildConfigurationAllParameters() {
        GalaxyConfiguration.setPropertiesFilePath(CONFIGURATION_DIRECTORY + "All.properties");
        final String expectedConfigurationString = "test.galaxy.instance=a6|" +
                                                   "test.galaxy.key=b6|" +
                                                   "galaxy.history.name=c6";
        Assert.assertEquals(expectedConfigurationString, GalaxyConfiguration.buildConfiguration("a6", "b6", "c6"));
    }
}
