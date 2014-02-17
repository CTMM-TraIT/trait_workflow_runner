/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.biomedbridges.configuration;

import com.github.jmchilton.blend4j.Config;

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
    private static Properties properties = Config.loadBlendProperties();

    public Configuration() {
        checkConfiguration();
    }

    public static String getGalaxyInstanceUrl() {
        return getProperty("test.galaxy.instance");
    }

    public static String getGalaxyApiKey() {
        return getProperty("test.galaxy.key");
    }

    private static String getProperty(final String key) {
        String value = null;
        if (properties.containsKey(key))
            value = properties.getProperty(key);
        return value;
    }

    /**
     * todo 1: Make it throw the exception as done in:
     * todo 1: http://www.java2novice.com/java_exception_handling_examples/create_custom_exception/
     * todo 2: We can log a warning here (file not found, property not found, etc.) and handle it on a higher level.
     */
    private void checkConfiguration() {
        if (getGalaxyInstanceUrl() == null || getGalaxyApiKey() == null) {
            System.err.print("The configuration file '" + System.getProperty("user.home")
                             + ".blend.properties' is incorrect. Ensure the following syntax:"
                             + "\n\ntest.galaxy.instance=www.domain.ext\\n\n"
                             + "test.galaxy.key=hexadec12345d225f38a4e34c2a5e101\\n\n");
            System.exit(1);
        }
    }
}
