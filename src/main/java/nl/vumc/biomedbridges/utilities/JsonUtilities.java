/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.utilities;

import org.json.simple.JSONObject;

/**
 * Some utility methods for handling json data.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 * @author <a href="mailto:y.hoogstrate@erasmusmc.nl">Youri Hoogstrate</a>
 */
public class JsonUtilities {
    /**
     * Hidden constructor. Only the static methods of this class are meant to be used.
     */
    private JsonUtilities() {
    }

    /**
     * Utility method for retrieving a json string.
     *
     * @param jsonObject the json object that has the value.
     * @param key the key of the value.
     * @return the value converted to a string or null.
     */
    public static String getJsonString(final JSONObject jsonObject, final String key) {
        final Object objectValue = jsonObject.get(key);
        return objectValue != null ? objectValue.toString() : null;
    }

    /**
     * Utility method for retrieving a json number.
     *
     * @param jsonObject the json object that has the value.
     * @param key the key of the value.
     * @return the value converted to a long or null.
     */
    public static Long getJsonLong(final JSONObject jsonObject, final String key) {
        final String stringValue = getJsonString(jsonObject, key);
        return stringValue != null ? Long.parseLong(stringValue) : null;
    }
}
