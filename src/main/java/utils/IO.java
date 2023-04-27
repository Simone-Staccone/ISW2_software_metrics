package utils;

import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;


public class IO {
    private static final String CSV_SEPARATOR = ";";

    public static JSONObject readJsonFromUrl(String url)  {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            return new JSONObject(jsonText);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static boolean writeOnFile(String projectName, List<List<String>> entries){
        try{
            String dir = "src" +  File.separator + "main" + File.separator + "data" + File.separator + projectName.toLowerCase() + File.separator;
            File directory = new File(dir);
            if(!directory.exists()){
                if(!directory.mkdir()){
                    throw new IOException();
                }
            }
            FileWriter fileWriter = new FileWriter(dir + projectName + Initializer.getOutputFileNameTail());
            fileWriter.append("Index;Version ID;Version Name;Date\n");
            int index = 0;

            for (List<String> ledge : entries) {
                fileWriter.append(String.valueOf(index)).append(CSV_SEPARATOR).append(ledge.get(0)).append(CSV_SEPARATOR).append(ledge.get(1)).append(CSV_SEPARATOR)
                        .append(ledge.get(2)).append("\n");
                index++;
            }
            fileWriter.flush();
            fileWriter.close();
            return true;
        } catch(IOException i){
            i.printStackTrace();
            return false;
        }
    }

    public static void appendOnLog(String whatToWrite){
        try{
            String dir = "src" +  File.separator + "main" + File.separator + Initializer.getLogFileName();
            FileWriter fileWriter = new FileWriter(dir,true);
            fileWriter.append(whatToWrite).append("\n");
            fileWriter.flush();
            fileWriter.close();
        } catch(IOException i){
            i.printStackTrace();
        }
    }


    public static void clean() {
        try{
            String dir = "src" +  File.separator + "main" + File.separator + "log.txt";
            FileWriter fileWriter = new FileWriter(dir);
            fileWriter.append("");
            fileWriter.flush();
            fileWriter.close();
        } catch(IOException i){
            i.printStackTrace();
        }
    }
}
