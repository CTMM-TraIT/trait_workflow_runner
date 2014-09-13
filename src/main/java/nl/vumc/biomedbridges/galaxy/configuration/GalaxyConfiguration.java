/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.galaxy.configuration;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.GalaxyInstanceFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import nl.vumc.biomedbridges.core.Constants;

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
    private Properties properties;

    /**
     * The path of the properties file.
     */
    private String propertiesFilePath;

    /**
     * The Galaxy server URL.
     */
    private String galaxyInstanceUrl;

    /**
     * The Galaxy API key.
     */
    private String apiKey;

    /**
     * The name of the history to run the workflow in.
     */
    private String historyName;

    /**
     * Whether debugging information should be printed.
     */
    private boolean debug;

    /**
     * Construct a Galaxy configuration object, using the default properties file.
     */
    public GalaxyConfiguration() {
        this(null);
    }

    /**
     * Construct a Galaxy configuration object.
     *
     * @param propertiesFilePath the path of the properties file or null to postpone reading the properties.
     */
    public GalaxyConfiguration(final String propertiesFilePath) {
        if (propertiesFilePath != null) {
            this.propertiesFilePath = propertiesFilePath;
            loadProperties();
            this.galaxyInstanceUrl = getGalaxyInstanceUrl();
            this.apiKey = getGalaxyApiKey();
            this.historyName = getGalaxyHistoryName();
        } else
            this.propertiesFilePath = System.getProperty("user.home") + File.separator + ".blend.properties";
    }

    /**
     * Build a configuration string from a Galaxy instance URL, an API key, and a history name.
     *
     * @param galaxyInstanceUrl the Galaxy instance URL.
     * @param galaxyApiKey      the Galaxy API key or null to retrieve the key from the properties file.
     * @param galaxyHistoryName the Galaxy history name.
     * @return the configuration string.
     */
    public String buildConfiguration(final String galaxyInstanceUrl, final String galaxyApiKey,
                                     final String galaxyHistoryName) {
        this.galaxyInstanceUrl = galaxyInstanceUrl;
        this.apiKey = (galaxyApiKey != null) ? galaxyApiKey : getGalaxyApiKey();
        this.historyName = galaxyHistoryName;
        return GALAXY_INSTANCE_PROPERTY_KEY + KEY_VALUE_SEPARATOR + galaxyInstanceUrl + PROPERTY_SEPARATOR
               + API_KEY_PROPERTY_KEY + KEY_VALUE_SEPARATOR + galaxyApiKey + PROPERTY_SEPARATOR
               + HISTORY_NAME_PROPERTY_KEY + KEY_VALUE_SEPARATOR + galaxyHistoryName;
    }

    /**
     * Build a configuration string from a Galaxy instance URL, the default API key and the default history name.
     *
     * @param galaxyInstanceUrl the Galaxy instance URL.
     * @return the configuration string.
     */
    public String buildConfiguration(final String galaxyInstanceUrl) {
        this.galaxyInstanceUrl = galaxyInstanceUrl;
        return buildConfiguration(galaxyInstanceUrl, getGalaxyApiKey(), getGalaxyHistoryName());
    }

    /**
     * Set whether debugging information should be printed.
     *
     * @param debug whether debugging information should be printed.
     */
    public void setDebug(final boolean debug) {
        this.debug = debug;
    }

    /**
     * Create and configure a Galaxy instance object.
     *
     * todo: create a getGalaxyInstance method that handles the configurationData == null case? And cache the object?
     *
     * @param configurationData the configuration string or null to use the settings from the properties file.
     * @return a Galaxy instance object or null.
     */
    public GalaxyInstance determineGalaxyInstance(final String configurationData) {
        String message = null;
        if (configurationData != null) {
            final String instancePrefix = GALAXY_INSTANCE_PROPERTY_KEY + KEY_VALUE_SEPARATOR;
            final String apiKeyPrefix = API_KEY_PROPERTY_KEY + KEY_VALUE_SEPARATOR;
            final String historyNamePrefix = HISTORY_NAME_PROPERTY_KEY + KEY_VALUE_SEPARATOR;
            if (configurationData.contains(PROPERTY_SEPARATOR)
                && configurationData.contains(instancePrefix)
                && configurationData.contains(apiKeyPrefix))
                message = processConfigurationProperties(configurationData, instancePrefix, apiKeyPrefix, historyNamePrefix);
            else
                message = String.format("Expected properties were not found in configuration data %s.", configurationData);
            if (message != null)
                logger.error(message + " Please specify: {}[Galaxy server URL]{}{}[API key]", instancePrefix,
                             PROPERTY_SEPARATOR, apiKeyPrefix);
        }
        return message == null ? GalaxyInstanceFactory.get(galaxyInstanceUrl, apiKey, debug) : null;
    }

    /**
     * Process all configuration properties.
     *
     * @param configurationData the configuration data.
     * @param instancePrefix    the Galaxy instance property prefix.
     * @param apiKeyPrefix      the api key property prefix.
     * @param historyNamePrefix the history name property prefix.
     * @return the logging message or null if there is nothing to log.
     */
    private String processConfigurationProperties(final String configurationData, final String instancePrefix,
                                                  final String apiKeyPrefix, final String historyNamePrefix) {
        String message = null;
        boolean instanceFound = false;
        boolean apiKeyFound = false;
        for (final String propertyDefinition : configurationData.split("\\|"))
            if (propertyDefinition.startsWith(instancePrefix)) {
                galaxyInstanceUrl = propertyDefinition.substring(propertyDefinition.indexOf('=') + 1);
                instanceFound = true;
                logger.trace("Read property Galaxy instance URL: {}.", galaxyInstanceUrl);
            } else if (propertyDefinition.startsWith(apiKeyPrefix)) {
                apiKey = propertyDefinition.substring(propertyDefinition.indexOf('=') + 1);
                apiKeyFound = true;
                logger.trace("Read property Galaxy API key: {}.", apiKey);
            } else if (propertyDefinition.startsWith(historyNamePrefix)) {
                historyName = propertyDefinition.substring(propertyDefinition.indexOf('=') + 1);
                logger.trace("Read property Galaxy history name: {}.", historyName);
            }
        if (!instanceFound || !apiKeyFound)
            message = String.format("Not all expected properties (Galaxy instance and API key) were found in"
                                    + " configuration data %s.", configurationData);
        return message;
    }

    /**
     * Set the path of the properties file and load the properties.
     *
     * @param propertiesFilePath the path of the properties file.
     * @return null or an error message.
     */
    public String setPropertiesFilePath(final String propertiesFilePath) {
        this.propertiesFilePath = propertiesFilePath;
        return loadProperties();
    }

    /**
     * Get the Galaxy instance URL.
     *
     * @return the Galaxy instance URL.
     */
    public String getGalaxyInstanceUrl() {
        return galaxyInstanceUrl != null ? galaxyInstanceUrl : getProperty(GALAXY_INSTANCE_PROPERTY_KEY);
    }

    /**
     * Get the Galaxy API key.
     *
     * @return the Galaxy API key.
     */
    public String getGalaxyApiKey() {
        final String galaxyApiKey;
        if (apiKey != null)
            galaxyApiKey = apiKey;
        else {
            final String serverSpecificKey = getProperty(API_KEY_PROPERTY_KEY + "." + getGalaxyInstanceUrl());
            galaxyApiKey = (serverSpecificKey != null) ? serverSpecificKey : getProperty(API_KEY_PROPERTY_KEY);
        }
        return galaxyApiKey;
    }

    /**
     * Get the Galaxy history name.
     *
     * @return the Galaxy history name.
     */
    public String getGalaxyHistoryName() {
        return historyName != null ? historyName : getProperty(HISTORY_NAME_PROPERTY_KEY);
    }

    /**
     * Get a blend(4j) property. Initialize and load the properties when the first property is retrieved.
     *
     * @param key the property key.
     * @return the property value.
     */
    private String getProperty(final String key) {
        if (properties == null)
            loadProperties();
        String value = null;
        if (properties.containsKey(key))
            value = properties.getProperty(key);
        return value;
    }

    /**
     * Load and check the properties.
     *
     * @return null or an error message.
     */
    private String loadProperties() {
        String message = null;
        properties = new Properties();
        final File propertiesFile = new File(propertiesFilePath);
        if (!propertiesFile.exists())
            message = String.format("The properties file %s is not found.", propertiesFile.getAbsolutePath());
        try (final InputStream inputStream = new FileInputStream(propertiesFile)) {
            properties.load(inputStream);
        } catch (final IOException e) {
            logger.error("Error loading properties from file {}.", propertiesFile.getAbsolutePath(), e);
            message = String.format("Error loading properties from file %s.", propertiesFile.getAbsolutePath());
        }
        checkConfiguration();
        return message;
    }

    /**
     * Check whether the properties return a non-null value and log errors if this is not the case.
     */
    private void checkConfiguration() {
        if (getGalaxyInstanceUrl() == null || getGalaxyApiKey() == null) {
            if (!new File(propertiesFilePath).exists()) {
                logger.error("The configuration file '{}' was not found.", propertiesFilePath);
                logger.error("Please make sure a configuration file is available with the following properties:");
            } else {
                logger.error("The configuration file '{}' was not read successfully.", propertiesFilePath);
                logger.error("Please ensure the following properties are available:");
            }
            logger.error("{}={} [or another Galaxy server]", GALAXY_INSTANCE_PROPERTY_KEY, Constants.GALAXY_INSTANCE_URL);
            logger.error(API_KEY_PROPERTY_KEY + "=[32hex-characters]");
            logger.error("");
            logger.error("Optional property:");
            logger.error(HISTORY_NAME_PROPERTY_KEY + "=Some History Name");
        }
    }
}
