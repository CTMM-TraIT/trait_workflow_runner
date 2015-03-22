/**
 * Copyright 2015 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.examples;

import org.junit.Test;

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
     * Since this test takes several minutes to run, you might want to use @Ignore if you want to do a run of all the
     * fast unit tests.
     */
    @Test
    public void testCheckAllExamples() {
        final AllExamplesCheck allExamplesCheck = new AllExamplesCheck();
        final String report = allExamplesCheck.checkAllExamples(AllExamplesCheck.CI_GALAXY_SERVER_URLS);
        boolean allOk = true;
        for (final String serverReport : report.split("\\|")) {
            final String serverReportTrimmed = serverReport.trim();
            System.out.println("Server report: " + serverReportTrimmed);
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
}
