package nl.vumc.biomedbridges;



import com.github.jmchilton.blend4j.galaxy.beans.Workflow;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by youri on 2/26/14.
 */
public class WorkflowJSON extends Workflow {
    private URL jsonURL;

    public void setJsonURL(URL jsonURL) {
        this.jsonURL = jsonURL;
    }

    public String getJson() {
        String json = "";

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(this.jsonURL.openStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null)
            {
                json = json + inputLine;
            }
            in.close();
        } catch (final Exception e) {
            e.printStackTrace();
        }

        return json;
    }
}
