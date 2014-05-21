/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

package nl.vumc.biomedbridges.examples;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Unit test for the HistogramExample class.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class HistogramExampleTest {
    /**
     * Test the histogram example.
     *
     * todo: test fails when running with Maven (mvn test):
     * "java.lang.AssertionError: null
             at org.junit.Assert.fail(Assert.java:86)
             at org.junit.Assert.assertTrue(Assert.java:41)
             at org.junit.Assert.assertTrue(Assert.java:52)
             at nl.vumc.biomedbridges.examples.HistogramExampleTest.testHistogramExample(HistogramExampleTest.java:23)".
     */
    @Test
    public void testHistogramExample() {
        assertTrue(new HistogramExample().runExample());
    }
}
