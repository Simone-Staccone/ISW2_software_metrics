package utils;

import model.ProjectClass;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;


public class IO {
    private static final String CSV_SEPARATOR = ",";
    private final String datasetUrlString;
    private final String project;

    public IO(String project) {
        this.datasetUrlString = "C:\\Users\\simon\\ISW2Projects\\Falessi\\src\\main\\data\\" + project + "\\DataSet.csv";
        this.project = project;
        IO.clean(datasetUrlString);
        StringBuilder header = new StringBuilder();
        System.out.println(Initializer.CATEGORIES);
        for(String s: Initializer.CATEGORIES){
            header.append(s).append(CSV_SEPARATOR);
        }
        header.delete(header.length()-1,header.length());

        IO.appendOnFile(datasetUrlString, header.toString());


    }

    public String getUrl(){
        return this.datasetUrlString;
    }

    public String getProjectName(){
        return this.project;
    }

    public static JSONObject readJsonObject(String url)  {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            return new JSONObject(jsonText);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static JSONArray readJsonArray(String url)  {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            return new JSONArray(jsonText);
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

    public boolean writeOnFile( List<List<String>> entries){
        try{
            String dir = "src" +  File.separator + "main" + File.separator + "data" + File.separator + project.toLowerCase() + File.separator;
            File directory = new File(dir);
            if(!directory.exists()){
                if(!directory.mkdir()){
                    throw new IOException();
                }
            }
            FileWriter fileWriter = new FileWriter(dir + project + Initializer.getOutputFileNameTail());
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

    public static void appendOnFile(String dir, String className) {
        try{
            FileWriter fileWriter = new FileWriter(dir,true);
            fileWriter.append(className).append("\n");
            fileWriter.flush();
            fileWriter.close();
        } catch(IOException i){
            i.printStackTrace();
        }
    }

    protected static void clean(String dir) {
        try{
            FileWriter fileWriter = new FileWriter(dir);
            fileWriter.append("");
            fileWriter.flush();
            fileWriter.close();
        } catch(IOException i){
            i.printStackTrace();
        }
    }

    public void serializeDataSet(List<ProjectClass> versionClasses) {


        for (ProjectClass projectClass: versionClasses) {
            IO.appendOnFile(this.datasetUrlString,
                    projectClass.getRelease() +
                            "," + projectClass.getName() +
                            "," + projectClass.getLoc() +
                            "," + projectClass.getLocAdded() +
                            "," + projectClass.getLocDeleted() +
                            "," + projectClass.getnAuth() +
                            "," + projectClass.getNr() +
                            "," + projectClass.getMaxLocAdded() +
                            "," + projectClass.getAvgLocAdded() +
                            "," + projectClass.getChurn() +
                            "," + projectClass.getMaxChurn() +
                            "," + projectClass.getAvgChurn() +
                            "," + projectClass.getFanOut() +
                            "," + projectClass.getMethodNumber() +
                            "," + "yes"
            );
        }


    }
}
