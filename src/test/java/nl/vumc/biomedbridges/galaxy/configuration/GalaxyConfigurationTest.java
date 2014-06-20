/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.galaxy.configuration;

import java.io.File;
import java.nio.file.Paths;

import org.junit.Assert;
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
     * Test the getGalaxyHistoryName method: check that the default history name is null.
     */
    @Test
    public void testGetGalaxyHistoryNameDefault() {
        assertNull(new GalaxyConfiguration().getGalaxyHistoryName());
    }

    /**
     * Test the constructor and the getGalaxyHistoryName method: check that a history name set in a properties
     * file is used.
     */
    @Test
    public void testGetGalaxyHistoryNameFromProperties() {
        final String propertiesFilePath = CONFIGURATION_DIRECTORY + "HistoryName.properties";
        final GalaxyConfiguration galaxyConfiguration = new GalaxyConfiguration(propertiesFilePath);
        assertNull(galaxyConfiguration.getGalaxyInstanceUrl());
        assertNull(galaxyConfiguration.getGalaxyApiKey());
        Assert.assertEquals("GalaxyConfigurationTest-1", galaxyConfiguration.getGalaxyHistoryName());
    }

    /**
     * Test the setPropertiesFilePath method: passing the empty string results in null for all properties.
     */
    @Test
    public void testSetPropertiesFilePathToEmptyString() {
        final GalaxyConfiguration galaxyConfiguration = new GalaxyConfiguration("");
        galaxyConfiguration.setPropertiesFilePath("");
        assertNull(galaxyConfiguration.getGalaxyInstanceUrl());
        assertNull(galaxyConfiguration.getGalaxyApiKey());
        assertNull(galaxyConfiguration.getGalaxyHistoryName());
    }

    /**
     * Test the setPropertiesFilePath method: an empty properties file results in null for all properties.
     */
    @Test
    public void testSetPropertiesFilePathToEmptyFile() {
        final GalaxyConfiguration galaxyConfiguration = new GalaxyConfiguration("");
        galaxyConfiguration.setPropertiesFilePath(CONFIGURATION_DIRECTORY + "Empty.properties");
        assertNull(galaxyConfiguration.getGalaxyInstanceUrl());
        assertNull(galaxyConfiguration.getGalaxyApiKey());
        assertNull(galaxyConfiguration.getGalaxyHistoryName());
    }

    /**
     * Test the setPropertiesFilePath method: a properties file with all properties set results in the right value for
     * all properties.
     */
    @Test
    public void testSetPropertiesFilePathToAllPropertiesFile() {
        final GalaxyConfiguration galaxyConfiguration = new GalaxyConfiguration("");
        galaxyConfiguration.setPropertiesFilePath(CONFIGURATION_DIRECTORY + "All.properties");
        Assert.assertEquals("aaaaaa", galaxyConfiguration.getGalaxyInstanceUrl());
        Assert.assertEquals("bbbbbb", galaxyConfiguration.getGalaxyApiKey());
        Assert.assertEquals("GalaxyConfigurationTest-1", galaxyConfiguration.getGalaxyHistoryName());
    }

    /**
     * Test the buildConfiguration method by specifying the galaxy instance only.
     */
    @Test
    public void testBuildConfigurationGalaxyInstanceOnly() {
        final GalaxyConfiguration galaxyConfiguration = new GalaxyConfiguration("");
        final String message = galaxyConfiguration.setPropertiesFilePath(CONFIGURATION_DIRECTORY + "All.properties");
        assertNull(message);
        final String expectedConfigurationString = "test.galaxy.instance=a6|" +
                                                   "test.galaxy.key=bbbbbb|" +
                                                   "galaxy.history.name=GalaxyConfigurationTest-1";
        Assert.assertEquals(expectedConfigurationString, galaxyConfiguration.buildConfiguration("a6"));
    }

    /**
     * Test the buildConfiguration method by specifying all parameters.
     */
    @Test
    public void testBuildConfigurationAllParameters() {
        final GalaxyConfiguration galaxyConfiguration = new GalaxyConfiguration("");
        galaxyConfiguration.setPropertiesFilePath(CONFIGURATION_DIRECTORY + "All.properties");
        final String expectedConfigurationString = "test.galaxy.instance=a6|" +
                                                   "test.galaxy.key=b6|" +
                                                   "galaxy.history.name=c6";
        Assert.assertEquals(expectedConfigurationString, galaxyConfiguration.buildConfiguration("a6", "b6", "c6"));
    }
}
