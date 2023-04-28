package utils;

import org.json.JSONObject;

import java.util.Objects;

public class JavaFileParser {
    public static JSONObject find(JSONObject fileObject) {
        if(fileObject.getString("name").contains(".java")){
            return fileObject;
        }else if(!fileObject.getString("name").contains(".")){
            return JavaFileParser.find(Objects.requireNonNull(IO.readJsonObject(fileObject.getString("url"))));
        }
        return null;
    }
}
