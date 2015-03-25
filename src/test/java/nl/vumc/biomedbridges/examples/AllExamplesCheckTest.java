/**
 * Copyright 2015 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.examples;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.Collections;

import nl.vumc.biomedbridges.core.Constants;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Integration test for the AllExamplesCheck class.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class AllExamplesCheckTest {
    /**
     * Test whether all examples run successfully on all Galaxy servers available for continuous integration.
     *
     * This unit test uses the API keys from a .blend.properties file. On Travis CI, this is done using the encrypted
     * src/configuration/travis-blend4j-properties/.blend.properties.enc file (see .travis.yml). More information can be
     * found in the encode-.blend.properties-for-travis.txt file.
     *
     * Since this test takes several minutes to run, you might want to use @Ignore if you want to do a quick run of all
     * the other unit tests.
     */
    @Test
    public void testCheckAllExamples() {
        final AllExamplesCheck allExamplesCheck = new AllExamplesCheck();
        final String report = allExamplesCheck.checkExamples(AllExamplesCheck.CI_GALAXY_SERVER_URLS,
                                                             AllExamplesCheck.ALL_EXAMPLE_CLASSES,
                                                             AllExamplesCheck.SKIP_EXAMPLES);
        boolean allOk = true;
        for (final String serverReport : report.split("\\|")) {
            final String serverReportTrimmed = serverReport.trim();
            final int beginIndexSuccessRate = serverReportTrimmed.indexOf(' ') + 1;
            final int indexSeparator = serverReportTrimmed.indexOf('/', beginIndexSuccessRate);
            final int endIndexSuccessRate = serverReportTrimmed.indexOf(' ', indexSeparator);
            assertNotEquals("Begin index space in server report: " + serverReportTrimmed, -1, beginIndexSuccessRate);
            assertNotEquals("Separator index slash in server report: " + serverReportTrimmed, -1, indexSeparator);
            assertNotEquals("End index space in server report: " + serverReportTrimmed, -1, endIndexSuccessRate);
            final String successfulExampleCount = serverReportTrimmed.substring(beginIndexSuccessRate, indexSeparator);
            final String attemptCount = serverReportTrimmed.substring(indexSeparator + 1, endIndexSuccessRate);
            allOk &= successfulExampleCount.equals(attemptCount);
        }
        assertTrue(report, allOk);
    }

    /**
     * Test whether an invalid (non existing) server is handled correctly.
     */
    @Test
    public void testInvalidServer() {
        final AllExamplesCheck allExamplesCheck = new AllExamplesCheck();
        final String invalidServer = "http://this-server-probably-does-not-exist.nl/";
        final String report = allExamplesCheck.checkExamples(Collections.singletonList(invalidServer),
                                                             AllExamplesCheck.ALL_EXAMPLE_CLASSES,
                                                             AllExamplesCheck.SKIP_EXAMPLES);
        assertEquals(invalidServer + ": 0/1 []", report);
    }

    /**
     * Test whether a failing example is handled correctly.
     */
    @Test
    public void testFailingExample() {
        final AllExamplesCheck allExamplesCheck = new AllExamplesCheck();
        final String report = allExamplesCheck.checkExamples(Collections.singletonList(Constants.CENTRAL_GALAXY_URL),
                                                             Collections.singletonList(DummyExample.class),
                                                             null);
        assertEquals(Constants.CENTRAL_GALAXY_URL + ": 0/1 [failures: " + DummyExample.class.getSimpleName() + "]",
                     report);
    }

    /**
     * Test whether a skipped example is handled correctly.
     */
    @Test
    public void testSkippedExample() {
        final AllExamplesCheck allExamplesCheck = new AllExamplesCheck();
        final String server = Constants.CENTRAL_GALAXY_URL;
        final String report = allExamplesCheck.checkExamples(Collections.singletonList(server),
                                                             Collections.singletonList(DummyExample.class),
                                                             ImmutableMap.of(server, ImmutableList.of(DummyExample.class)));
        assertEquals(server + ": 0/0 []", report);
    }
}
