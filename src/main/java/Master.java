import control.Analyzer;
import org.json.JSONException;
import utils.IO;
import utils.Initializer;

import java.util.List;

public class Master {
    public static void main(String[] args) throws JSONException {

        Initializer init  = Initializer.getInstance();
        List<String> projects = init.getProjectNames();
        IO.appendOnLog("STARTING SOFTWARE METRICS ANALYZER\n");

        int proportion = Analyzer.computeProportion(projects);
        Analyzer.analyzeProjects(projects, proportion);


        IO.appendOnLog("SOFTWARE METRICS ANALYZER SUCCESSFULLY STOPPED\n");
    }
}
