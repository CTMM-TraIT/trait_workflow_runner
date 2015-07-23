/**
 * Copyright 2015 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.examples;

import com.google.common.collect.ImmutableMap;

import java.util.Collections;
import java.util.List;

import nl.vumc.biomedbridges.core.Constants;

import org.apache.log4j.Level;
import org.apache.log4j.varia.LevelRangeFilter;
import org.junit.Before;
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
     * The object to test.
     */
    private AllExamplesCheck allExamplesCheck;

    /**
     * Setup for each unit test.
     */
    @Before
    public void setUp() {
        allExamplesCheck = new AllExamplesCheck();
    }

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
        final String report = allExamplesCheck.checkExamples(AllExamplesCheck.CI_GALAXY_SERVER_URLS,
                                                             AllExamplesCheck.ALL_EXAMPLE_CLASSES,
                                                             AllExamplesCheck.SKIP_EXAMPLES,
                                                             60, 60);

        // Check the report.
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
        final String report = allExamplesCheck.checkExamples(Collections.singletonList(Constants.CENTRAL_GALAXY_URL),
                                                             AbstractBaseExample.getSingletonList(DummyExample.class),
                                                             null);
        assertEquals(Constants.CENTRAL_GALAXY_URL + ": 0/1 [failures: " + DummyExample.class.getSimpleName() + "]",
                     report);
    }

    /**
     * Test whether a skipped example is handled correctly.
     */
    @Test
    public void testSkippedExample() {
        final String server = Constants.CENTRAL_GALAXY_URL;
        final List<Class<? extends AbstractBaseExample>> dummyExampleClassList
                = AbstractBaseExample.getSingletonList(DummyExample.class);
        final String report = allExamplesCheck.checkExamples(Collections.singletonList(server),
                                                             dummyExampleClassList,
                                                             ImmutableMap.of(server, dummyExampleClassList));
        assertEquals(server + ": 0/0 []", report);
    }

    /**
     * Test the createAppenderFilter method;
     */
    @Test
    public void testCreateAppenderFilter() {
        final LevelRangeFilter appenderFilter = allExamplesCheck.createAppenderFilter();
        assertTrue(appenderFilter.getAcceptOnMatch());
        assertEquals(Level.WARN, appenderFilter.getLevelMin());
        assertEquals(Level.FATAL, appenderFilter.getLevelMax());
    }
}
