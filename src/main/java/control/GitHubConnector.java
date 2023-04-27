package control;

import org.json.JSONObject;
import utils.IO;

public class GitHubConnector {
    private GitHubConnector() throws IllegalAccessException {
        throw new IllegalAccessException("Can't initialize this class");
    }

    public static void getInfos(){
        //JSONObject resultSet = IO.readJsonFromUrl("https://api.github.com/repos/apache/bookkeeper");
        //System.out.println(resultSet);
    }
}
