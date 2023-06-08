package control;

import model.ProjectClass;
import model.Release;
import model.Releases;
import model.Ticket;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BugClassDetector {
    private BugClassDetector(){}

    public static void collectClassesWithBug(Releases releases, List<RevCommit> commits, String projectName, int proportionValue) throws IOException {
        JiraConnector jiraConnector = new JiraConnector();
        List<Ticket> ticketList = jiraConnector.getTickets(projectName,proportionValue,releases);

        for (Ticket ticket : ticketList) {

            List<RevCommit> commitsAssociatedToTicket = BugClassDetector.filterCommitsAssociatedToTicket(ticket, commits);

            // for each commit associated to the ticket, we need the modified classes
            for (RevCommit commit : commitsAssociatedToTicket) {

                List<String> modifiedClassesNames = GitHubConnector.getModifiedClasses(commit,projectName);

                // each one of these classes is buggy if it belongs to a release with id s.t. ticket.IV.id <= class.releaseId < ticket.FV.id
                for (String modifiedClass : modifiedClassesNames) {
                    for (Release release :releases.getReleaseList()) {
                        labelClasses(release.getVersionClasses(), modifiedClass, ticket,release.getReleaseDate());
                    }
                }
            }
        }

    }

    private static void labelClasses(List<ProjectClass> classes, String className, Ticket ticket, Date releaseDate) {
        for (ProjectClass projectClass : classes) {
            if (projectClass.getName().equals(className)
                    && ( releaseDate.after(ticket.injectedVersionDate()) || releaseDate.compareTo(ticket.injectedVersionDate()) == 0)
                    && releaseDate.before(ticket.fixedVersionDate()) ){
                projectClass.setBug(true);
            }
        }

    }

    public static List<RevCommit> filterCommitsAssociatedToTicket(Ticket ticket, List<RevCommit> allCommits) {
        List<RevCommit> assCommits = new ArrayList<>();

        for (RevCommit commit : allCommits) {
            if( (!assCommits.contains(commit)) && (commit.getFullMessage().contains(ticket.key() + ":") || commit.getFullMessage().contains("[" + ticket.key())) )
                    assCommits.add(commit);
        }

        return assCommits;
    }


    public static List<Releases> buildWalkForward(String project, Releases allReleases) {
        List<Releases> newReleases  = new ArrayList<>();
        for(int i = 0;i<allReleases.getReleaseList().size()+1;i++){
            JiraConnector jiraConnector = new JiraConnector();
            newReleases.add(jiraConnector.getInfos(project, String.valueOf(i)));
        }
        return newReleases;


    }
}
