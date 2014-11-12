/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import nl.vumc.biomedbridges.core.Constants;
import nl.vumc.biomedbridges.core.FileUtils;
import nl.vumc.biomedbridges.galaxy.WorkflowUtils;
import nl.vumc.biomedbridges.utilities.JsonUtilities;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Test private and protected constructors for several classes.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class HiddenConstructorsTest {
    /**
     * Test whether the constructor is hidden and that it can be called (using reflection).
     */
    @Test
    public void testHiddenConstructors() {
        testPrivateConstructor(Constants.class);
        testPrivateConstructor(FileUtils.class);
        testPrivateConstructor(JsonUtilities.class);
        testPrivateConstructor(WorkflowUtils.class);
    }

    /**
     * Test whether the constructor is private and that it can be called without parameters (using reflection).
     *
     * @param hiddenConstructorClass the class to test.
     */
    private void testPrivateConstructor(final Class hiddenConstructorClass) {
        testConstructor(hiddenConstructorClass, Modifier.PRIVATE, null, null);
    }

    /**
     * Test whether the constructor has the specified visibility and that it can be called with the specified parameters
     * (using reflection).
     *
     * @param hiddenConstructorClass the class to test.
     * @param visibility             the expected constructor visibility.
     * @param parameters             the parameters for the constructor.
     */
    private void testConstructor(final Class<?> hiddenConstructorClass, final int visibility,
                                 final Class[] parameterTypes, final Object[] parameters) {
        try {
            final Constructor constructor = hiddenConstructorClass.getDeclaredConstructor(parameterTypes);
            assertEquals(visibility, visibility & constructor.getModifiers());
            constructor.setAccessible(true);
            assertEquals(hiddenConstructorClass, constructor.newInstance(parameters).getClass());
        } catch (final Exception e) {
            fail(e.getMessage());
        }
    }
}
