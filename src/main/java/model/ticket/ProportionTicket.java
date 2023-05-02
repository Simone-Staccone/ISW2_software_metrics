package model.ticket;

import java.util.Date;
import java.util.List;


public record ProportionTicket(List<String> components,
                               Date openingVersion) {

    public List<String> getComponents() {
        return components;
    }



}
