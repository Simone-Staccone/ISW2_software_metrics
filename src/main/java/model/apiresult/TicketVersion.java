package model.apiresult;

import control.ConstantNames;
import control.GitHubConnector;
import model.Release;
import org.eclipse.jgit.revwalk.RevCommit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class TicketVersion{
    private List<Release> releases;

    public TicketVersion(List<List<String>> entries, List<RevCommit> commits){
        releases = new ArrayList<>();
        for(List<String> entry : entries){
            try {
                releases.add(
                        new Release(
                                Integer.parseInt(entry.get(0)),
                                entry.get(1),
                                new SimpleDateFormat(ConstantNames.FORMATTING_STRING).parse(entry.get(2)),
                                GitHubConnector.getCommitOfRelease(commits,new SimpleDateFormat(ConstantNames.FORMATTING_STRING).parse(entry.get(2)))
                        )
                );
            } catch (ParseException e) {
                releases.add(
                        new Release(
                                Integer.parseInt(entry.get(0)),
                                entry.get(1),
                                null,
                                null
                        )
                );
            }
        }
    }

    public TicketVersion(List<List<String>> entries){
        releases = new ArrayList<>();
        for(List<String> entry : entries){
            try {
                releases.add(
                        new Release(
                                Integer.parseInt(entry.get(0)),
                                entry.get(1),
                                new SimpleDateFormat(ConstantNames.FORMATTING_STRING).parse(entry.get(2)),
                                null
                        )
                );
            } catch (ParseException e) {
                releases.add(
                        new Release(
                                Integer.parseInt(entry.get(0)),
                                entry.get(1),
                                null,
                                null
                        )
                );
            }
        }
    }

    public List<Release> getReleases(){
        return this.releases;
    }
}
