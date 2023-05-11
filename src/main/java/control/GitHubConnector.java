package control;


import model.ProjectClass;
import model.Release;
import model.Releases;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import utils.DateParser;
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

            for (RevCommit commit : commitsList) {
                if (!revCommits.contains(commit)) {
                    revCommits.add(commit);
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
        Map<String, String> projectClasses = new HashMap<>();

        RevTree tree = commit.getTree();	//We get the tree of the files and the directories that belong to the repository when commit was pushed
        TreeWalk treeWalk = new TreeWalk(repository);	//We use a TreeWalk to iterate over all files in the Tree recursively
        treeWalk.addTree(tree);
        treeWalk.setRecursive(true);

        while(treeWalk.next()) {
            //We are keeping only Java classes that are not involved in tests
            if(treeWalk.getPathString().contains(".java") && !treeWalk.getPathString().contains("/test/")) {
                //We are retrieving (name class, content class) couples
                projectClasses.put(treeWalk.getPathString(), new String(repository.open(treeWalk.getObjectId(0)).getBytes(), StandardCharsets.UTF_8));
            }
        }
        treeWalk.close();

        return projectClasses;

    }

    public static List<String> getModifiedClasses(RevCommit commit, String project) throws IOException {
        FileRepository repository = new FileRepository("C:\\Users\\simon\\ISW2Projects\\projects\\" + project.toLowerCase() + File.separator + ".git");

        List<String> modifiedClasses = new ArrayList<>();    //Here there will be the names of the classes that have been modified by the commit

        try (DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE);

             ObjectReader reader = repository.newObjectReader()) {

            CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
            ObjectId newTree = commit.getTree();
            newTreeIter.reset(reader, newTree);

            RevCommit commitParent = commit.getParent(0);    //It's the previous commit of the commit we are considering
            CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
            ObjectId oldTree = commitParent.getTree();
            oldTreeIter.reset(reader, oldTree);

            diffFormatter.setRepository(repository);
            List<DiffEntry> entries = diffFormatter.scan(oldTreeIter, newTreeIter);

            //Every entry contains info for each file involved in the commit (old path name, new path name, change type (that could be MODIFY, ADD, RENAME, etc.))
            for (DiffEntry entry : entries) {
                //We are keeping only Java classes that are not involved in tests
                if (entry.getNewPath().contains(".java") && !entry.getNewPath().contains("/test/")) {
                    modifiedClasses.add(entry.getNewPath());
                }

            }

        } catch (ArrayIndexOutOfBoundsException e) {
            //commit has no parents: skip this commit, return an empty list and go on

        }

        return modifiedClasses;

    }

    public static int getAddedLines(DiffFormatter diffFormatter, DiffEntry entry) throws IOException {

        int addedLines = 0;
        for(Edit edit : diffFormatter.toFileHeader(entry).toEditList()) {
            addedLines += edit.getEndB() - edit.getBeginB();

        }
        return addedLines;

    }

    public static int getDeletedLines(DiffFormatter diffFormatter, DiffEntry entry) throws IOException {

        int deletedLines = 0;
        for(Edit edit : diffFormatter.toFileHeader(entry).toEditList()) {
            deletedLines += edit.getEndA() - edit.getBeginA();

        }
        return deletedLines;

    }


    public static void computeAddedAndDeletedLinesList(ProjectClass projectClass, String project) throws IOException {
        FileRepository repo = new FileRepository("C:\\Users\\simon\\ISW2Projects\\projects\\" + project.toLowerCase() + File.separator + ".git");
        int sumLocAdded = 0;
        int sumLocDel = 0;

        for(RevCommit comm : projectClass.getCommits()) {
            try(DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE)) {

                RevCommit parentComm = comm.getParent(0);

                diffFormatter.setRepository(repo);
                diffFormatter.setDiffComparator(RawTextComparator.DEFAULT);

                List<DiffEntry> diffs = diffFormatter.scan(parentComm.getTree(), comm.getTree());
                for(DiffEntry entry : diffs) {
                    if(entry.getNewPath().equals(projectClass.getName())) {
                        int locAdded = getAddedLines(diffFormatter, entry);
                        int locDeleted = getDeletedLines(diffFormatter, entry);
                        projectClass.getAddedLinesList().add(locAdded);
                        sumLocAdded = sumLocAdded + locAdded;
                        projectClass.getDeletedLinesList().add(locDeleted);
                        sumLocDel = sumLocDel + locDeleted;
                    }

                }

            } catch(ArrayIndexOutOfBoundsException ignore) {
                //commit has no parents: skip this commit, return an empty list and go on

            }

        }
        projectClass.setLocAdded(sumLocAdded);
        projectClass.setDeletedLoc(sumLocDel);
        ComputeMetrics.computeLocAndChurnMetrics(projectClass);
    }


    public static void computeCommitForClass(Releases releases, String projectName) {
        try {
            for (Release release : releases.getReleaseList()) {
                for (RevCommit commit : release.allCommits) {
                    List<String> classNamesList = GitHubConnector.getModifiedClasses(commit, projectName);
                    for (String className : classNamesList) {
                        for (ProjectClass projectClass : release.getVersionClasses()) {
                            if (projectClass.getName().compareTo(className) == 0) {
                                projectClass.addCommit(commit);
                            }
                        }
                    }
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void splitCommitsIntoReleases(List<RevCommit> commits, Releases releases) {
        for (RevCommit commit : commits) {
            if (releases.getReleaseList().get(0).getLastCommit() == null && commit.getAuthorIdent().getWhen().before(releases.getReleaseList().get(0).getReleaseDate()) ) {
                releases.getReleaseList().get(0).setLastCommit(commit);
            }
            if (commit.getAuthorIdent().getWhen().before(releases.getReleaseList().get(0).getReleaseDate()) ||
                    commit.getAuthorIdent().getWhen().compareTo(releases.getReleaseList().get(0).getReleaseDate()) == 0) {
                releases.getReleaseList().get(0).addCommit(commit);
                if (commit.getAuthorIdent().getWhen().after(releases.getReleaseList().get(0).getLastCommit().getAuthorIdent().getWhen())) {
                    releases.getReleaseList().get(0).setLastCommit(commit);
                }
            }

            for (int i = 1; i < releases.getReleaseList().size(); i++) {
                if (releases.getReleaseList().get(i).getLastCommit() == null && commit.getAuthorIdent().getWhen().before(releases.getReleaseList().get(i).getReleaseDate())) {
                    releases.getReleaseList().get(i).setLastCommit(commit);
                }

                if ( ( commit.getAuthorIdent().getWhen().before(releases.getReleaseList().get(i).getReleaseDate()) && commit.getAuthorIdent().getWhen().after(releases.getReleaseList().get(i - 1).getReleaseDate()) )
                 || commit.getAuthorIdent().getWhen().compareTo(releases.getReleaseList().get(i).getReleaseDate()) == 0) {
                    releases.getReleaseList().get(i).addCommit(commit);
                    if (commit.getAuthorIdent().getWhen().after(releases.getReleaseList().get(i).getLastCommit().getAuthorIdent().getWhen())) {
                        releases.getReleaseList().get(i).setLastCommit(commit);
                    }
                }
            }
        }
    }


    public static void computeLocForClassInRelease(Release release) {
        for (ProjectClass projectClass:release.getVersionClasses()) {
            projectClass.setLoc(ComputeMetrics.computeLOC(projectClass.getContent()));
        }
    }

    public static void setFanOut(ProjectClass projectClass) {
        projectClass.setFanOut(DateParser.countMatches(projectClass.getContent(), "new"));
    }

    public static void setMethodsNumber(ProjectClass projectClass) {
        projectClass.setMethodsNumber(DateParser.countMatches(projectClass.getContent(), "public") + DateParser.countMatches(projectClass.getContent(), "private"));
    }
}
