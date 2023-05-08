package control;

import model.ProjectClass;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ComputeMetrics {
    public ComputeMetrics(){

    }

    public static int computeLOC(String content){
        int count = 0;
        String[] lines = content.split("\r\n|\r|\n");

        for(String s :lines){
            if(s.contains("/*") || s.contains("* ") || s.contains("import") || s.equals("")){ //Do not consider lines with import or comment or empty lines
                count++;
            }
        }

        return lines.length-count;
    }





    public static void computeNR(List<ProjectClass> projectClasses) {

        for(ProjectClass projectClass : projectClasses) {
            projectClass.setNr(projectClass.getCommits().size());

        }

    }


    public static void computeLocAndChurnMetrics(ProjectClass projectClass) {

        int sumLOC = 0;
        int maxLOC = 0;
        double avgLOC = 0;
        int churn = 0;
        int maxChurn = 0;
        double avgChurn = 0;

        for(int i=0; i<projectClass.getAddedLinesList().size(); i++) {

            int currentLOC = projectClass.getAddedLinesList().get(i);
            int currentDiff = Math.abs(projectClass.getAddedLinesList().get(i) - projectClass.getDeletedLinesList().get(i));

            sumLOC = sumLOC + currentLOC;
            churn = churn + currentDiff;

            if(currentLOC > maxLOC) {
                maxLOC = currentLOC;
            }
            if(currentDiff > maxChurn) {
                maxChurn = currentDiff;
            }

        }

        //If a class has 0 revisions, its AvgLocAdded and AvgChurn are 0 (see initialization above).
        if(!projectClass.getAddedLinesList().isEmpty()) {
            avgLOC = 1.0*sumLOC/projectClass.getAddedLinesList().size();
        }
        if(!projectClass.getAddedLinesList().isEmpty()) {
            avgChurn = 1.0*churn/projectClass.getAddedLinesList().size();
        }

        projectClass.setLocAdded(sumLOC);
        projectClass.setMaxLocAdded(maxLOC);
        projectClass.setAvgLocAdded(avgLOC);
        projectClass.setChurn(churn);
        projectClass.setMaxChurn(maxChurn);
        projectClass.setAvgChurn(avgChurn);

    }

    public static void computeAuthorsNumber(List<ProjectClass> projectClasses) {
        for(ProjectClass projectClass : projectClasses) {
            List<String> classAuthors = new ArrayList<>();

            for(RevCommit commit : projectClass.getCommits()) {
                if(!classAuthors.contains(commit.getAuthorIdent().getName())) {
                    classAuthors.add(commit.getAuthorIdent().getName());
                }

            }
            projectClass.setnAuth(classAuthors.size()+1);

        }

    }


    public static void computeLOCMetrics(String url, RevCommit commit, ProjectClass projectClass){
        int linesAdded = 0;
        int linesDeleted = 0;
        int filesChanged;
        try {
            FileRepository repo = new FileRepository(new File(url + "/.git"));
            RevWalk rw = new RevWalk(repo);
            RevCommit parent = rw.parseCommit(commit.getParent(0).getId());
            DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
            df.setRepository(repo);
            df.setDiffComparator(RawTextComparator.DEFAULT);
            df.setDetectRenames(true);
            List<DiffEntry> diffs;
            diffs = df.scan(parent.getTree(), commit.getTree());
            filesChanged = diffs.size();
            for (DiffEntry diff : diffs) {
                for (Edit edit : df.toFileHeader(diff).toEditList()) {
                    linesDeleted += edit.getEndA() - edit.getBeginA();
                    linesAdded += edit.getEndB() - edit.getBeginB();
                }
            }
        } catch (IOException e1) {
            throw new RuntimeException(e1);
        }
        //System.out.println(linesAdded + " " + linesDeleted + " " + filesChanged);
        projectClass.setLocAdded(linesAdded);
        projectClass.setDeletedLoc(linesDeleted);
        projectClass.setModifiedLoc(filesChanged);
    }
}
