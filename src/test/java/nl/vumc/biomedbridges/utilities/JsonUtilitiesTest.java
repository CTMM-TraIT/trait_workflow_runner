/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.utilities;

import com.google.common.collect.ImmutableMap;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.json.simple.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for the JsonUtilities class.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class JsonUtilitiesTest {
    /**
     * Test whether the constructor is private and that it can be called (using reflection).
     *
     * @throws NoSuchMethodException     if the constructor is not found.
     * @throws IllegalAccessException    if the Constructor object is enforcing Java language access control and the
     *                                   underlying constructor is inaccessible.
     * @throws InvocationTargetException if the underlying constructor throws an exception.
     * @throws InstantiationException    if the class that declares the underlying constructor represents an abstract
     *                                   class.
     */
    @Test
    public void testPrivateConstructor()
        throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        final Constructor<JsonUtilities> constructor = JsonUtilities.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        assertEquals(JsonUtilities.class, constructor.newInstance().getClass());
    }

    /**
     * Test the getJsonString method.
     */
    @Test
    public void testGetJsonString() {
        final String key = "name";
        final String value = "Concatenate datasets";
        final JSONObject jsonObject = new JSONObject(ImmutableMap.of(key, value));
        assertEquals(value, JsonUtilities.getJsonString(jsonObject, key));
        assertNull(JsonUtilities.getJsonString(jsonObject, "unknown-key"));
    }

    /**
     * Test the getJsonLong method.
     */
    @Test
    public void testGetJsonLong() {
        final String key = "id";
        final Long value = 33550336L;
        final JSONObject jsonObject = new JSONObject(ImmutableMap.of(key, value));
        assertEquals(value, JsonUtilities.getJsonLong(jsonObject, key));
        assertNull(JsonUtilities.getJsonLong(jsonObject, "unknown-key"));
    }
}
