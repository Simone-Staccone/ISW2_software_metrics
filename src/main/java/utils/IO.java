package utils;

import model.ProjectClass;
import org.json.JSONObject;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;


public class IO {
    private static final String CSV_SEPARATOR = ",";
    private static final String CSV_SEPARATOR_2 = ";";
    private final String datasetUrlString;
    private final String project;

    public IO(String project) {
        this.datasetUrlString = "C:\\Users\\simon\\ISW2Projects\\Falessi\\src\\main\\data\\" + project + "\\DataSet.arff";
        this.project = project;
        IO.clean(datasetUrlString);
        IO.appendOnFile(this.datasetUrlString,"@relation " + project);
        IO.appendOnFile(this.datasetUrlString,"@attribute LOC numeric");
        IO.appendOnFile(this.datasetUrlString,"@attribute LOC_ADDED numeric");
        IO.appendOnFile(this.datasetUrlString,"@attribute LOC_DELETED numeric");
        IO.appendOnFile(this.datasetUrlString,"@attribute N_AUTH numeric");
        IO.appendOnFile(this.datasetUrlString,"@attribute NR numeric");
        IO.appendOnFile(this.datasetUrlString,"@attribute MAX_LOC_ADDED numeric");
        IO.appendOnFile(this.datasetUrlString,"@attribute AVG_LOC_ADDED numeric");
        IO.appendOnFile(this.datasetUrlString,"@attribute CHURN numeric");
        IO.appendOnFile(this.datasetUrlString,"@attribute MAX_CHURN numeric");
        IO.appendOnFile(this.datasetUrlString,"@attribute AVG_CHURN numeric");
        IO.appendOnFile(this.datasetUrlString,"@attribute FAN_OUT numeric");
        IO.appendOnFile(this.datasetUrlString,"@attribute METHOD_COMPLEXITY numeric");
        IO.appendOnFile(this.datasetUrlString,"@attribute IS_BUGGY {'Yes', 'No'}");
        IO.appendOnFile(this.datasetUrlString,"@data");
    }

    public IO(String project, boolean csv) throws Exception {
        if(!csv){
            throw new Exception();
        }
        this.datasetUrlString = "C:\\Users\\simon\\ISW2Projects\\Falessi\\src\\main\\data\\" + project + "\\WekaReport.csv";
        this.project = project;
        IO.clean(datasetUrlString);
        IO.appendOnFile(this.datasetUrlString,"classifier;iteration;precision;recall;RC;kappa;");
    }



    public static void createDirectory(String dir) throws IOException {
        File directory = new File(dir);
        if(!directory.exists()){
            System.out.println(directory.mkdirs() + " " + dir);
        }
    }

    public String getProjectName(){
        return this.project;
    }

    public static JSONObject readJsonObject(String url)  {
        try (InputStream is = new URI(url).toURL().openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            is.close();
            return new JSONObject(jsonText);
        } catch (IOException | URISyntaxException e) {
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
        String dir = "src" +  File.separator + "main" + File.separator + Initializer.getLogFileName();
        appendOnFile(dir,whatToWrite);
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
            File directory = new File(dir);
            if(!directory.exists()){
                if(!directory.mkdir()) {
                    i.printStackTrace();
                }else {
                    appendOnFile(dir,className);
                }
            }
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
            String buggy = projectClass.isBug() ? "Yes" : "No";

            IO.appendOnFile(this.datasetUrlString,
                            projectClass.getLoc() +
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
                            "," + buggy
            );
        }


    }

    public void serializeDataSetOnCsv(int iteration, String classifier, double precision, double recall, double rc, double kappa) {
        IO.appendOnFile(this.datasetUrlString,
                classifier +
                        CSV_SEPARATOR_2 + iteration +
                        CSV_SEPARATOR_2 + precision +
                        CSV_SEPARATOR_2 + recall +
                        CSV_SEPARATOR_2 + rc +
                        CSV_SEPARATOR_2 + kappa
        );

    }
}
