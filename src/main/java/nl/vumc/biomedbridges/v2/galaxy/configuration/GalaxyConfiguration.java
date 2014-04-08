/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.biomedbridges.v2.galaxy.configuration;

import com.github.jmchilton.blend4j.Config;

import java.io.File;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration class which gives access to the Galaxy instance URL and API key to be used. These properties are read
 * from the .blend.properties file in the user's home directory (System.getProperty("user.home")).
 * <p/>
 * See the Config.getBlendPropertiesFile method in the blend4j library for more details:
 * https://github.com/jmchilton/blend4j/blob/master/src/main/java/com/github/jmchilton/blend4j/Config.java
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 * @author <a href="mailto:y.hoogstrate@erasmusmc.nl">Youri Hoogstrate</a>
 */
public class GalaxyConfiguration {
    /**
     * The property key for the Galaxy instance in the configure string or .blend.properties file.
     */
    public static final String GALAXY_INSTANCE_PROPERTY_KEY = "test.galaxy.instance";

    /**
     * The property key for the API key in the configure string or .blend.properties file.
     */
    public static final String API_KEY_PROPERTY_KEY = "test.galaxy.key";

    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(GalaxyConfiguration.class);

    /**
     * The blend(4j) properties. These are loaded when the first property is retrieved.
     */
    private static Properties properties;

    /**
     * Hidden constructor. Only the static methods of this class are meant to be used.
     */
    private GalaxyConfiguration() {
    }

    /**
     * Build a configuration string from a Galaxy instance URL and the default API key.
     *
     * @param galaxyInstanceUrl the Galaxy instance URL.
     * @return the configuration string.
     */
    public static String buildConfiguration(final String galaxyInstanceUrl) {
        return buildConfiguration(galaxyInstanceUrl, GalaxyConfiguration.getGalaxyApiKey());
    }

    /**
     * Build a configuration string from a Galaxy instance URL and an API key.
     *
     * @param galaxyInstanceUrl the Galaxy instance URL.
     * @param galaxyApiKey      the Galaxy API key.
     * @return the configuration string.
     */
    public static String buildConfiguration(final String galaxyInstanceUrl, final String galaxyApiKey) {
        return GalaxyConfiguration.GALAXY_INSTANCE_PROPERTY_KEY + "=" + galaxyInstanceUrl + "|"
               + GalaxyConfiguration.API_KEY_PROPERTY_KEY + "=" + galaxyApiKey;
    }

    /**
     * Get the Galaxy instance URL.
     *
     * @return the Galaxy instance URL.
     */
    public static String getGalaxyInstanceUrl() {
        return getProperty(GALAXY_INSTANCE_PROPERTY_KEY);
    }

    /**
     * Get the Galaxy API key.
     *
     * @return the Galaxy API key.
     */
    public static String getGalaxyApiKey() {
        return getProperty(API_KEY_PROPERTY_KEY);
    }

    /**
     * Get a blend(4j) property. Initialize and load the properties when the first property is retrieved.
     *
     * @param key the property key.
     * @return the property value.
     */
    private static String getProperty(final String key) {
        initializeIfNeeded();
        String value = null;
        if (properties.containsKey(key))
            value = properties.getProperty(key);
        return value;
    }

    /**
     * Load and check the properties if they have not been retrieved yet.
     */
    private static void initializeIfNeeded() {
        if (properties == null) {
            properties = Config.loadBlendProperties();
            checkConfiguration();
        }
    }

    /**
     * Check whether the properties return a non-null value and log errors if this is not the case.
     */
    private static void checkConfiguration() {
        if (getGalaxyInstanceUrl() == null || getGalaxyApiKey() == null) {
            final String propertiesFilePath = System.getProperty("user.home") + ".blend.properties";
            if (!new File(propertiesFilePath).exists()) {
                logger.error("The configuration file '{}' was not found.", propertiesFilePath);
                logger.error("Please make sure a configuration file is available with the following properties:");
            } else {
                logger.error("The configuration file '{}' was not read successfully.", propertiesFilePath);
                logger.error("Please ensure the following properties are available:");
            }
            logger.error(GALAXY_INSTANCE_PROPERTY_KEY + "=https://usegalaxy.org/");
            logger.error(API_KEY_PROPERTY_KEY + "=[32hex-characters]12345d2238a4e3");
        }
    }
}
