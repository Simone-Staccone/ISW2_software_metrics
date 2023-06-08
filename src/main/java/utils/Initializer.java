package utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.*;


public class Initializer {
    private static List<String> projectNames = null;
    private static String outputFileNameTail = null;
    private static String apiUrl = null;
    private static String searchUrlFirstHalf = null;
    private static String searchUrlSecondHalf = null;
    protected static List<String> categories = null;
    private static Initializer instance = null;
    private static String logFileName = null;

    private Initializer() {}

    public static Initializer getInstance() {
        if(instance==null) {
            instance = new Initializer();
            instance.init();
        }
        return instance;
    }

    public List<String> getProjectNames(){
        return projectNames;
    }

    public static String getApiUrl() {
        return apiUrl;
    }

    public static String getOutputFileNameTail() {
        return outputFileNameTail;
    }

    public static String getSearchUrlFirstHalf() {
        return searchUrlFirstHalf;
    }

    public static String getSearchUrlSecondHalf() {
        return searchUrlSecondHalf;
    }

    public static String getLogFileName() {
        return logFileName;
    }

    private void init() {
        String path = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "config" + File.separator + "config.json";
        IO.clean();


        try {
            File file = new File(path);
            if (!file.exists()){
                    throw new IOException("Impossible to find configuration file file");
            }
            String myJson = new Scanner(file).useDelimiter("\\Z").next();
            JSONObject config = new JSONObject(myJson);
            JSONArray names = config.names();

            apiUrl = config.getString(names.getString(0));
            categories = convertJSONArrayListString(config,names.getString(1));
            outputFileNameTail = config.getString(names.getString(2));
            searchUrlSecondHalf = config.getString(names.getString(3));
            logFileName = config.getString(names.getString(4));
            searchUrlFirstHalf = config.getString(names.getString(5));
            projectNames = convertJSONArrayListString(config,names.getString(6));


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
