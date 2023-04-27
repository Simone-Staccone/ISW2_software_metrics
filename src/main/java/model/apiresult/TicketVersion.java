package model.apiresult;

import control.ConstantNames;
import model.Release;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class TicketVersion{
    List<Release> releases = new ArrayList<>();

    public TicketVersion(List<List<String>> entries){
        for(List<String> entry : entries){
            try {
                releases.add(
                        new Release(
                                Integer.parseInt(entry.get(0)),
                                entry.get(1),
                                new SimpleDateFormat(ConstantNames.FORMATTING_STRING).parse(entry.get(2))
                        )
                );
            } catch (ParseException e) {
                releases.add(
                        new Release(
                                Integer.parseInt(entry.get(0)),
                                entry.get(1),
                                null
                        )
                );
            }
        }
    }
}
