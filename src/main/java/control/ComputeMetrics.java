package control;

import org.eclipse.jgit.revwalk.RevCommit;

import java.util.ArrayList;
import java.util.List;

public class ComputeMetrics {
    public ComputeMetrics(){

    }

    public static int computeLOC(String[] lines){
        int count = 0;

        for(String s :lines){
            if(s.contains("/*") || s.contains("import") || s.equals("") || s.contains("* ")){  //We count comment, import or blank lines to get removed
                count++;
            }
        }

        return lines.length-count;
    }

    public int computeNAuth(String javaClass, Iterable<RevCommit> commits) {
        List<String> classAuthors = new ArrayList<>();

        for(RevCommit commit : commits) {
            //System.out.println(commit.getAuthorIdent().getName() + " " + commit.getAuthorIdent().getWhen());
            break;
            /*if(!classAuthors.contains(commit.getAuthorIdent().getName())) {
                classAuthors.add(commit.getAuthorIdent().getName());
            }*/
        }
        //System.out.println(classAuthors);


        return classAuthors.size();
    }
}
