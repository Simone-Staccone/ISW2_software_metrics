package control;


import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import utils.IO;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GitHubConnector {

    private GitHubConnector() throws IllegalAccessException {
        throw new IllegalAccessException("Can't initialize this class");
    }

    public static List<RevCommit> getCommits(String project) throws IOException, GitAPIException {
        List<RevCommit> revCommits = new ArrayList<>();
        RepositoryBuilder repositoryBuilder = new RepositoryBuilder();

        String url = "C:\\Users\\simon\\ISW2Projects\\Falessi\\src\\main\\data\\" + project.toLowerCase() + File.separator + "data.csv";


        Repository repo = repositoryBuilder
                .findGitDir(new File("C:\\Users\\simon\\ISW2Projects\\projects\\" + project.toLowerCase() + File.separator))
                .setMustExist(true)
                .build();
        Git git = new Git(repo);


        IO.appendOnLog("Starting getting commits for project: " + project.toLowerCase());


        Iterable<RevCommit> commitsList = git.log().call();



        for (RevCommit commit : commitsList) {
            if (!revCommits.contains(commit)) {
                revCommits.add(commit);
            }
        }
        IO.clean(url);
        //System.out.println(classesNames);

        IO.appendOnLog("Obtained commits for project: " + project.toLowerCase() +  " ...");

        return revCommits;
    }

    public static List<String> getProjectClassesNames(String project) throws IOException {
        RepositoryBuilder repositoryBuilder = new RepositoryBuilder();
        Repository repository = repositoryBuilder
                .findGitDir(new File("C:\\Users\\simon\\ISW2Projects\\projects\\" + project.toLowerCase() + File.separator))
                .setMustExist(true)
                .build();
        Map<String, String> projectClassesText = new HashMap<>();
        // find the HEAD
        ObjectId lastCommitId = repository.resolve(Constants.HEAD);

        // a RevWalk allows to walk over commits based on some filtering that is defined
        try (RevWalk revWalk = new RevWalk(repository)) {
            RevCommit commit = revWalk.parseCommit(lastCommitId);
            // and using commit's tree find the path
            RevTree tree = commit.getTree();
            System.out.println("Having tree: " + tree);

            // now try to find a specific file
            try (TreeWalk treeWalk = new TreeWalk(repository)) {
                treeWalk.addTree(tree);
                treeWalk.setRecursive(true);
                while(treeWalk.next()) {
                    //We are keeping only Java classes that are not involved in tests
                    if(treeWalk.getPathString().contains(".java") && !treeWalk.getPathString().contains("/test/")) {
                        //We are retrieving (name class, content class) couples
                        projectClassesText.put(treeWalk.getPathString(), new String(repository.open(treeWalk.getObjectId(0)).getBytes(), StandardCharsets.UTF_8));
                    }
                }

            }

            revWalk.dispose();
        }

        List<String> projectClassesNames = new ArrayList<>(projectClassesText.keySet());
        String url = "C:\\Users\\simon\\ISW2Projects\\Falessi\\src\\main\\data\\" + project.toLowerCase() + File.separator + "data.csv";

        for(String name:projectClassesNames){
            IO.appendOnFile(url,name);
        }
        return projectClassesNames;
    }
}
