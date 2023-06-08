package control;

import model.ProjectClass;
import model.Release;
import model.Releases;
import org.eclipse.jgit.revwalk.RevCommit;
import utils.IO;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WalkForward {
    private WalkForward(){

    }

    public static void createFiles(Releases newReleases, String project, Releases releases) throws IOException {
        List<ProjectClass> allClasses = new ArrayList<>();
        List<RevCommit> allCommits = new ArrayList<>();
        int i;

        for (i = 0;i<newReleases.getReleaseList().size();i++) {
            allCommits.addAll(newReleases.getReleaseList().get(i).allCommits);
            allClasses.addAll(newReleases.getReleaseList().get(i).getVersionClasses());
        }

        BugClassDetector.collectClassesWithBug(newReleases,allCommits,project,2);




        Release testRelease = releases.getReleaseList().get(newReleases.getReleaseList().size());


        String trainUrl = "src" + File.separator + "main" + File.separator + "data" + File.separator + project.toLowerCase() + File.separator + "Release_" + (i+1) + File.separator + "train";
        String testUrl = "src" + File.separator + "main" + File.separator + "data" + File.separator + project.toLowerCase() + File.separator + "Release_" + (i+1) + File.separator + "test";

        try {
            IO.createDirectory(trainUrl);
            IO.createDirectory(testUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }

        IO trainWriter = new IO(project.toLowerCase() + File.separator + "Release_" + (i+1) + File.separator + "train");
        IO testWriter = new IO(project.toLowerCase() + File.separator + "Release_" + (i+1) + File.separator + "test");

        trainWriter.serializeDataSet(allClasses);
        testWriter.serializeDataSet(testRelease.getVersionClasses());

    }
}
