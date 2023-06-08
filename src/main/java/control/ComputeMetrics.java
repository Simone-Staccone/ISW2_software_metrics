package control;

import model.ProjectClass;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.ArrayList;
import java.util.List;

public class ComputeMetrics {
    private ComputeMetrics(){

    }

    public static int computeLOC(String content){
        String[] lines = content.split("\r\n|\r|\n");
        return lines.length;
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
        int delLoc = 0;
        int maxChurn = 0;
        double avgChurn = 0;

        for(int i=0; i<projectClass.getAddedLinesList().size(); i++) {

            int currentLOC = projectClass.getAddedLinesList().get(i);
            int currentDelete = projectClass.getDeletedLinesList().get(i);
            int currentDiff = Math.abs(projectClass.getAddedLinesList().get(i) - projectClass.getDeletedLinesList().get(i));

            sumLOC = sumLOC + currentLOC;
            delLoc = delLoc + currentDelete;
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
                if(!classAuthors.contains(commit.getAuthorIdent().getName()))
                    classAuthors.add(commit.getAuthorIdent().getName());
            }
            projectClass.setnAuth(classAuthors.size());

        }

    }
}
