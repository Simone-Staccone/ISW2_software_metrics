import org.json.JSONException;

import java.util.List;

public class Master {
    public static void main(String[] args) throws JSONException {
        Initializer.getInstance();
        List<String> projects = Initializer.getProjectNames();
        for(String project: projects) {
            JiraConnector.getInfos(project);
        }
    }
}
