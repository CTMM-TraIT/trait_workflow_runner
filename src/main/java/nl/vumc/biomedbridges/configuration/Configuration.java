/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.biomedbridges.configuration;

import com.github.jmchilton.blend4j.Config;

import java.io.File;
import java.util.Properties;

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
public class Configuration {
    private static Properties properties;

    public static String getGalaxyInstanceUrl() {
        return getProperty("test.galaxy.instance");
    }

    public static String getGalaxyApiKey() {
        return getProperty("test.galaxy.key");
    }

    private static String getProperty(final String key) {
        initializeIfNeeded();
        String value = null;
        if (properties.containsKey(key))
            value = properties.getProperty(key);
        return value;
    }

    private static void initializeIfNeeded() {
        if (properties == null) {
            properties = Config.loadBlendProperties();
            checkConfiguration();
        }
    }

    /**
     * todo 1: Make it throw the exception as done in:
     * todo 1: http://www.java2novice.com/java_exception_handling_examples/create_custom_exception/
     * todo 2: We can log a warning here (file not found, property not found, etc.) and handle it on a higher level.
     */
    private static void checkConfiguration() {
        if (getGalaxyInstanceUrl() == null || getGalaxyApiKey() == null) {
            final String propertiesFilePath = System.getProperty("user.home") + ".blend.properties";
            if (!new File(propertiesFilePath).exists()) {
                System.err.println("The configuration file '" + propertiesFilePath + "' was not found.");
                System.err.println("Please make sure a configuration file is available with the following properties:");
            } else {
                System.err.println("The configuration file '" + propertiesFilePath + "' was not read successfully.");
                System.err.println("Please ensure the following properties are available:");
            }
            System.err.println("test.galaxy.instance=https://usegalaxy.org/");
            System.err.println("test.galaxy.key=32hex-characters12345d225f38a4e3");
        }
    }
}
