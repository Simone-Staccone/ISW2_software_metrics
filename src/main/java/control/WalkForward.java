package control;

import model.ProjectClass;
import model.Release;
import model.Releases;
import utils.IO;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WalkForward {
    private WalkForward(){

    }

    public static void createFiles(Releases releases,String project){
        List<ProjectClass> allClasses = new ArrayList<>(releases.getReleaseList().get(0).getVersionClasses());


        for (int i = 1;i<releases.getReleaseList().size();i++) {
            Release release = releases.getReleaseList().get(i);
            String trainUrl = "src" + File.separator + "main" + File.separator + "data" + File.separator + project.toLowerCase() + File.separator + "Release_" + release.getReleaseNumber() + File.separator + "train";
            String testUrl = "src" + File.separator + "main" + File.separator + "data" + File.separator + project.toLowerCase() + File.separator + "Release_" + release.getReleaseNumber() + File.separator + "test";

            try {
                IO.createDirectory(trainUrl);
                IO.createDirectory(testUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            IO trainWriter = new IO(project.toLowerCase() + File.separator + "Release_" + release.getReleaseNumber() + File.separator + "train");

            IO testWriter = new IO(project.toLowerCase() + File.separator + "Release_" + release.getReleaseNumber() + File.separator + "test");
            


            trainWriter.serializeDataSet(allClasses);
            testWriter.serializeDataSet(release.getVersionClasses());

            allClasses.addAll(release.getVersionClasses());

        }
    }
}
