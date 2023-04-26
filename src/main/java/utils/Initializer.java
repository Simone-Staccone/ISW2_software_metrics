package utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.*;


public class Initializer {
    private static List<String> PROJECT_NAMES = null;
    private static String OUTPUT_FILE_NAME_TAIL = null;
    private static String API_URL = null;
    private static String SEARCH_URL_FIRST_HALF = null;
    private static String SEARCH_URL_SECOND_HALF = null;
    private static List<String> CATEGORIES = null;
    private static Initializer instance = null;
    private static String LOG_FILE_NAME = null;

    private Initializer() {}

    public static Initializer getInstance() {
        if(instance==null) {
            instance = new Initializer();
            instance.init();
        }
        return instance;
    }

    public static List<String> getProjectNames(){
        return PROJECT_NAMES;
    }

    public static List<String> getCategoriesNames(){
        return CATEGORIES;
    }

    public static String getApiUrl() {
        return API_URL;
    }

    public static String getOutputFileNameTail() {
        return OUTPUT_FILE_NAME_TAIL;
    }

    public static String getSearchUrlFirstHalf() {
        return SEARCH_URL_FIRST_HALF;
    }

    public static String getSearchUrlSecondHalf() {
        return SEARCH_URL_SECOND_HALF;
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

            API_URL = config.getString(names.getString(0));
            CATEGORIES = convertJSONArrayListString(config,names.getString(1));
            OUTPUT_FILE_NAME_TAIL = config.getString(names.getString(2));
            SEARCH_URL_SECOND_HALF = config.getString(names.getString(3));
            LOG_FILE_NAME = config.getString(names.getString(4));
            SEARCH_URL_FIRST_HALF = config.getString(names.getString(5));
            PROJECT_NAMES = convertJSONArrayListString(config,names.getString(6));


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
