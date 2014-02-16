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
 * See the Config.getBlendPropertiesFile method in the blend4j library for more details.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class Configuration {
    private static Properties properties = Config.loadBlendProperties();

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
}
