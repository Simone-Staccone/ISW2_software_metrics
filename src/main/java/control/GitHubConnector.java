package control;


import model.Commit;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.IO;
import utils.JavaFileParser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//Solve maximum limit problem

public class GitHubConnector {
    private GitHubConnector() throws IllegalAccessException {
        throw new IllegalAccessException("Can't initialize this class");
    }

    public static List<Commit> getCommits(String project){
        JSONArray resultSet = IO.readJsonArray("https://api.github.com/repos/apache/" + project + "/commits");
        List<Commit> commits = new ArrayList<>();


        for(int i = 0;i< Objects.requireNonNull(resultSet).length();i++){
            try {
                JSONObject commit = resultSet.getJSONObject(i);


                commits.add(new Commit(
                        commit.getString("node_id"),
                        commit.getJSONObject("commit").getJSONObject("author").getString("name"),
                        new SimpleDateFormat(ConstantNames.FORMATTING_STRING).parse(commit.getJSONObject("commit").getJSONObject("author").getString("date").substring(0,ConstantNames.FORMATTING_STRING.length()))
                ));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return commits;
    }



    public static List<String> getClasses(String project){
        JSONArray resultSet = IO.readJsonArray("https://api.github.com/repos/apache/" + project + "/contents");
        List<String> classes = new ArrayList<>();


        for(int i = 0;i< Objects.requireNonNull(resultSet).length();i++){
            JSONObject component = JavaFileParser.find(resultSet.getJSONObject(i));
            if(component != null)
                classes.add(component.getString("name"));
        }
        return classes;
    }

}
