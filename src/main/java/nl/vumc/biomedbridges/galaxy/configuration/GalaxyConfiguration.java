/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.galaxy.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration class which gives access to the Galaxy instance URL and API key to be used. These properties are by
 * default read from the .blend.properties file in the user's home directory (System.getProperty("user.home")). It is
 * also possible to specify a custom properties file.
 * <p/>
 * See the Config.getBlendPropertiesFile method in the blend4j library for more details:
 * https://github.com/jmchilton/blend4j/blob/master/src/main/java/com/github/jmchilton/blend4j/Config.java
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 * @author <a href="mailto:y.hoogstrate@erasmusmc.nl">Youri Hoogstrate</a>
 */
public class GalaxyConfiguration {
    /**
     * The property key for the Galaxy instance in the configuration string or .blend.properties file.
     */
    public static final String GALAXY_INSTANCE_PROPERTY_KEY = "test.galaxy.instance";

    /**
     * The property key for the API key in the configuration string or .blend.properties file.
     */
    public static final String API_KEY_PROPERTY_KEY = "test.galaxy.key";

    /**
     * The property key for the API key in the configuration string or .blend.properties file.
     */
    public static final String HISTORY_NAME_PROPERTY_KEY = "galaxy.history.name";

    /**
     * The separator between key and value of a configuration property.
     */
    public static final String KEY_VALUE_SEPARATOR = "=";

    /**
     * The separator between configuration properties.
     */
    public static final String PROPERTY_SEPARATOR = "|";

    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(GalaxyConfiguration.class);

    /**
     * The blend(4j) properties. These are loaded when the first property is retrieved or when an explicit properties
     * file path is set.
     */
    private static Properties properties;

    /**
     * The path of the properties file.
     */
    private static String propertiesFilePath;

    /**
     * Initialize the static fields.
     */
    static {
        resetStaticFields();
    }

    /**
     * Hidden constructor. Only the static methods of this class are meant to be used.
     */
    private GalaxyConfiguration() {
    }

    /**
     * Reset (or initialize) all static fields.
     */
    protected static void resetStaticFields() {
        properties = null;
        propertiesFilePath = System.getProperty("user.home") + File.separator + ".blend.properties";
    }

    /**
     * Build a configuration string from a Galaxy instance URL, an API key, and a history name.
     *
     * @param galaxyInstanceUrl the Galaxy instance URL.
     * @param galaxyApiKey      the Galaxy API key.
     * @param galaxyHistoryName the Galaxy history name.
     * @return the configuration string.
     */
    public static String buildConfiguration(final String galaxyInstanceUrl, final String galaxyApiKey,
                                            final String galaxyHistoryName) {
        return GALAXY_INSTANCE_PROPERTY_KEY + KEY_VALUE_SEPARATOR + galaxyInstanceUrl + PROPERTY_SEPARATOR
               + API_KEY_PROPERTY_KEY + KEY_VALUE_SEPARATOR + galaxyApiKey + PROPERTY_SEPARATOR + HISTORY_NAME_PROPERTY_KEY
               + KEY_VALUE_SEPARATOR + galaxyHistoryName;
    }

    /**
     * Build a configuration string from a Galaxy instance URL, the default API key and the default history name.
     *
     * @param galaxyInstanceUrl the Galaxy instance URL.
     * @return the configuration string.
     */
    public static String buildConfiguration(final String galaxyInstanceUrl) {
        return buildConfiguration(galaxyInstanceUrl, getGalaxyApiKey(), getGalaxyHistoryName());
    }

    /**
     * Set the path of the properties file and load the properties.
     *
     * @param propertiesFilePath the path of the properties file.
     */
    public static void setPropertiesFilePath(final String propertiesFilePath) {
        GalaxyConfiguration.propertiesFilePath = propertiesFilePath;
        loadProperties();
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
     * Get the Galaxy history name.
     *
     * @return the Galaxy history name.
     */
    public static String getGalaxyHistoryName() {
        return getProperty(HISTORY_NAME_PROPERTY_KEY);
    }

    /**
     * Get a blend(4j) property. Initialize and load the properties when the first property is retrieved.
     *
     * @param key the property key.
     * @return the property value.
     */
    private static String getProperty(final String key) {
        if (properties == null)
            loadProperties();
        String value = null;
        if (properties.containsKey(key))
            value = properties.getProperty(key);
        return value;
    }

    /**
     * Load and check the properties.
     */
    private static void loadProperties() {
        properties = new Properties();
        final File propertiesFile = new File(propertiesFilePath);
        try (final InputStream inputStream = new FileInputStream(propertiesFile)) {
            properties.load(inputStream);
        } catch (final IOException e) {
            logger.error("Error loading properties from file {}.", propertiesFile.getAbsolutePath(), e);
        }
        checkConfiguration();
    }

    /**
     * Check whether the properties return a non-null value and log errors if this is not the case.
     */
    private static void checkConfiguration() {
        if (getGalaxyInstanceUrl() == null || getGalaxyApiKey() == null) {
            if (!new File(propertiesFilePath).exists()) {
                logger.error("The configuration file '{}' was not found.", propertiesFilePath);
                logger.error("Please make sure a configuration file is available with the following properties:");
            } else {
                logger.error("The configuration file '{}' was not read successfully.", propertiesFilePath);
                logger.error("Please ensure the following properties are available:");
            }
            logger.error(GALAXY_INSTANCE_PROPERTY_KEY + "=https://usegalaxy.org/");
            logger.error(API_KEY_PROPERTY_KEY + "=[32hex-characters]12345d2238a4e3");
            logger.error("");
            logger.error("Optional property:");
            logger.error(HISTORY_NAME_PROPERTY_KEY + "=Some History Name");
        }
    }
}
