package control;


import model.Releases;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.treewalk.TreeWalk;
import utils.IO;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.*;

public class GitHubConnector {
    private GitHubConnector() throws IllegalAccessException {
        throw new IllegalAccessException("Can't initialize this class");
    }

    public static List<RevCommit> getCommits(String project,Releases releases) throws IOException, GitAPIException {
        List<RevCommit> revCommits = new ArrayList<>();
        RepositoryBuilder repositoryBuilder = new RepositoryBuilder();
        String url = "C:\\Users\\simon\\ISW2Projects\\Falessi\\src\\main\\data\\" + project.toLowerCase() + File.separator + "dataSet.csv";

        Repository repo = repositoryBuilder
                .findGitDir(new File("C:\\Users\\simon\\ISW2Projects\\projects\\" + project.toLowerCase() + File.separator))
                .setMustExist(true)
                .build();

        Git git = new Git(repo);


        IO.appendOnLog("Starting getting commits for project: " + project.toLowerCase() + "...");


        List<Ref> branchesList = git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call();


        //Branches loop
        for(Ref branch : branchesList) {
            Iterable<RevCommit> commitsList = git.log().add(repo.resolve(branch.getName())).call(); //Only get commit in present branch

            for(RevCommit commit : commitsList) {
                if(!revCommits.contains(commit)) {
                    revCommits.add(commit);


                    if (commit.getAuthorIdent().getWhen().before(releases.getReleaseList().get(0).getReleaseDate())) {
                        releases.getReleaseList().get(0).addCommit(commit);
                        if(releases.getReleaseList().get(0).getLastCommit() == null){
                            releases.getReleaseList().get(0).setLastCommit(commit);
                        }
                        if (commit.getAuthorIdent().getWhen().before(releases.getReleaseList().get(0).getLastCommit().getAuthorIdent().getWhen())) {
                            releases.getReleaseList().get(0).setLastCommit(commit);
                        }
                    } else {
                        for (int i = 1; i < releases.getReleaseList().size(); i++) {
                            if(releases.getReleaseList().get(i).getLastCommit() == null){
                                releases.getReleaseList().get(i).setLastCommit(commit);
                            }

                            if (commit.getAuthorIdent().getWhen().before(releases.getReleaseList().get(i).getReleaseDate())
                                    && commit.getAuthorIdent().getWhen().after(releases.getReleaseList().get(i - 1).getReleaseDate())) {
                                releases.getReleaseList().get(i).addCommit(commit);
                                if (commit.getAuthorIdent().getWhen().before(releases.getReleaseList().get(i).getLastCommit().getAuthorIdent().getWhen())) {
                                    releases.getReleaseList().get(i).setLastCommit(commit);
                                }
                            }
                        }
                    }
                }
            }
        }




        IO.appendOnLog("Obtained commits for project: " + project.toLowerCase());

        return revCommits;
    }

    public static Map<String, String> getClassesForCommit(RevCommit commit, String project) throws IOException {
        RepositoryBuilder repositoryBuilder = new RepositoryBuilder();
        Repository repository = repositoryBuilder
                .findGitDir(new File("C:\\Users\\simon\\ISW2Projects\\projects\\" + project.toLowerCase() + File.separator))
                .setMustExist(true)
                .build();
        Map<String, String> javaClasses = new HashMap<>();

        RevTree tree = commit.getTree();	//We get the tree of the files and the directories that were belonging to the repository when commit was pushed
        TreeWalk treeWalk = new TreeWalk(repository);	//We use a TreeWalk to iterate over all files in the Tree recursively
        treeWalk.addTree(tree);
        treeWalk.setRecursive(true);

        while(treeWalk.next()) {
            //We are keeping only Java classes that are not involved in tests
            if(treeWalk.getPathString().contains(".java") && !treeWalk.getPathString().contains("/test/")) {
                //We are retrieving (name class, content class) couples
                javaClasses.put(treeWalk.getPathString(), new String(repository.open(treeWalk.getObjectId(0)).getBytes(), StandardCharsets.UTF_8));
            }
        }
        treeWalk.close();

        return javaClasses;

    }


    /*
    public static void buildDataSet(String project, List<RevCommit> commits) throws IOException {
        List<ProjectClass> projectClasses = new ArrayList<>();
        Map<String, String> projectClassesText = getProjectClassesText(project);
        List<String> projectClassesTexts = new ArrayList<>();
        List<String> projectClassesNames = new ArrayList<>(projectClassesText.keySet());
        List<Integer> LOC = new ArrayList<>();
        ComputeMetrics computer = new ComputeMetrics();
        List<Integer> nAuthors = new ArrayList<>();


        for(String javaClass : projectClassesText.values()) {
            String[] lines = javaClass.split("\r\n|\r|\n");
            projectClassesTexts.add(javaClass);
            LOC.add(computer.computeLOC(lines));
            nAuthors.add(computer.computeNAuth(javaClass,commits));
        }

        for(int i=0;i<LOC.size();i++){
            projectClasses.add(
                    new ProjectClass(
                            projectClassesNames.get(i),
                            projectClassesTexts.get(i),
                           new Release(0,"",new Date(),commits.get(0),  1),
                            LOC.get(i),
                            nAuthors.get(i)
                            ));
        }
        String url = "C:\\Users\\simon\\ISW2Projects\\Falessi\\src\\main\\data\\" + project.toLowerCase() + File.separator + "dataSet.csv";

        IO.clean(url);

        IO.appendOnFile(url, Initializer.getCategoriesNames().toString());
        for(ProjectClass projectClass:projectClasses){
            IO.appendOnFile(url,
                    "1," +
                            projectClass.getName() +
                            "," +
                            projectClass.getLoc() +
                            "," +
                            "," +
                            "," +
                            projectClass.getnAuth());
        }

    }*/

}
