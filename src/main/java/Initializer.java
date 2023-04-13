import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.*;


public class Initializer {
    private static String PROJECT_NAME = null;
    private static List<String> CATEGORIES = null;
    private static Initializer instance = null;

    private Initializer() {}

    public static Initializer getInstance() {
        if(instance==null) {
            instance = new Initializer();
            instance.init();
        }
        return instance;
    }

    public static String getProjectName(){
        return PROJECT_NAME;
    }

    public static List<String> getCategoriesNames(){
        return CATEGORIES;
    }

    private void init() {
        String path = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "config" + File.separator + "config.json";

        try {
            File file = new File(path);
            if (!file.exists()){
                    throw new IOException("Impossible to find configuration file file");
            }
            String myJson = new Scanner(file).useDelimiter("\\Z").next();
            JSONObject config = new JSONObject(myJson);
            JSONArray names = config.names();
            for(int i = 0;i<config.names().length();i++){
                config.getString("Project_Name");
                config.getJSONArray("Categories");
            }
            CATEGORIES = convertJSONArrayListString(config,names.getString(0));
            PROJECT_NAME = config.getString(names.getString(1));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<String> convertJSONArrayListString(JSONObject obj, String field){
        JSONArray temp = obj.getJSONArray(field);
        List<String> list = new ArrayList<>();
        for(int i = 0; i < temp.length(); i++){
            list.add(temp.getString(i));
        }
        return list;
    }
}
