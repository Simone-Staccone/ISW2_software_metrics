package control;

import model.ProjectClass;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
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

    public void computeAuthorsNumber(List<ProjectClass> projectClasses) {
        for(ProjectClass projectClass : projectClasses) {
            List<String> classAuthors = new ArrayList<>();

            for(RevCommit commit : projectClass.getCommits()) {
                if(!classAuthors.contains(commit.getAuthorIdent().getName())) {
                    classAuthors.add(commit.getAuthorIdent().getName());
                }

            }
            projectClass.setnAuth(classAuthors.size());

        }

    }


    public static void computeLOCMetrics(RevCommit firstCommit, RevCommit lastCommit, String url){
        int linesAdded = 0;
        int linesDeleted = 0;
        int filesChanged;
        try {
            RepositoryBuilder repositoryBuilder = new RepositoryBuilder();
            Repository repository = repositoryBuilder
                    .findGitDir(new File(url))
                    .setMustExist(true)
                    .build();



            RevWalk rw = new RevWalk(repository);
            DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
            df.setRepository(repository);
            df.setDiffComparator(RawTextComparator.DEFAULT);
            df.setDetectRenames(true);
            List<DiffEntry> diffs;
            diffs = df.scan(lastCommit.getTree(), firstCommit.getTree());
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
        System.out.println(linesAdded + " " + linesDeleted + " " + filesChanged);
    }
}
