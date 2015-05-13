/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.biomedbridges.core;

/**
 * This class contains several general constants.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class Constants {
    /**
     * The central Galaxy server (instance URL) to use by default.
     *
     * Note: the API key is read from the .blend.properties file to keep the API key out of the GitHub repository. See
     * the GalaxyConfiguration class for more information on how to use this configuration file. Please change the
     * Galaxy server and the API key together to keep them in sync.
     */
    public static final String CENTRAL_GALAXY_URL = "https://usegalaxy.org/";

    /**
     * The acceptation Galaxy server at Vancis (instance URL).
     *
     * Note: the API key is read from the .blend.properties file to keep the API key out of the GitHub repository. See
     * the GalaxyConfiguration class for more information on how to use this configuration file. Please change the
     * Galaxy server and the API key together to keep them in sync.
     */
    public static final String VANCIS_PRO_GALAXY_URL = "https://galaxy.ctmm-trait.nl/";

    /**
     * The acceptation Galaxy server at Vancis (instance URL).
     *
     * Note: the API key is read from the .blend.properties file to keep the API key out of the GitHub repository. See
     * the GalaxyConfiguration class for more information on how to use this configuration file. Please change the
     * Galaxy server and the API key together to keep them in sync.
     */
    public static final String VANCIS_ACC_GALAXY_URL = "https://galaxy-acc.ctmm-trait.nl/";

    /**
     * The Galaxy server at The Hyve (instance URL).
     *
     * Note: the API key is read from the .blend.properties file to keep the API key out of the GitHub repository. See
     * the GalaxyConfiguration class for more information on how to use this configuration file. Please change the
     * Galaxy server and the API key together to keep them in sync.
     */
    public static final String THE_HYVE_GALAXY_URL = "http://galaxy.thehyve.net/";

//    /**
//     * The SURFsara CTMM TraIT Galaxy server (instance URL).
//     *
//     * Note: the API key is read from the .blend.properties file to keep the API key out of the GitHub repository. See
//     * the GalaxyConfiguration class for more information on how to use this configuration file. Please change the
//     * Galaxy server and the API key together to keep them in sync.
//     */
//    public static final String SURF_SARA_GALAXY_INSTANCE_URL = "http://galaxy.trait-ctmm.cloudlet.sara.nl/";

    /**
     * The local Galaxy server (instance URL).
     *
     * Note: the API key is read from the .blend.properties file to keep the API key out of the GitHub repository. See
     * the GalaxyConfiguration class for more information on how to use this configuration file. Please change the
     * Galaxy server and the API key together to keep them in sync.
     */
    public static final String LOCAL_HOST_GALAXY_INSTANCE_URL = "http://localhost:8080/";

    /**
     * The name of the concatenate test workflow.
     */
    public static final String CONCATENATE_WORKFLOW = "TestWorkflowConcatenate";

    /**
     * The name of the grep test workflow.
     */
    public static final String GREP_WORKFLOW = "Grep";

    /**
     * The name of the line, word, and character count workflow.
     */
    public static final String LINE_COUNT_WORKFLOW = "LineCount";

//    /**
//     * The name of the scatterplot test workflow.
//     */
//    public static final String TEST_WORKFLOW_SCATTERPLOT = "TestWorkflowScatterplot";

    /**
     * The name of the histogram workflow.
     */
    public static final String WORKFLOW_HISTOGRAM = "Histogram";

    /**
     * The name of the RandomLinesTwice workflow.
     */
    public static final String WORKFLOW_RANDOM_LINES_TWICE = "RandomLinesTwice";

    /**
     * The name of the RNA-Seq edgeR DGE workflow.
     */
    public static final String WORKFLOW_RNA_SEQ_DGE = "RNA-Seq-edgeR-DGE";

    /**
     * The name of the "remove top and left" workflow.
     */
    public static final String WORKFLOW_REMOVE_TOP_AND_LEFT = "RemoveTopAndLeft";

    /**
     * The number of milliseconds in a second.
     */
    public static final int MILLISECONDS_PER_SECOND = 1000;

    /**
     * Hidden constructor. Only the static fields of this class are meant to be used.
     */
    private Constants() {
    }
}
