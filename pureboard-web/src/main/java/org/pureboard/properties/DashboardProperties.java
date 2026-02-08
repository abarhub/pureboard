package org.pureboard.properties;

import lombok.Data;

import java.util.List;

@Data
public class DashboardProperties {

    private String titre;
    private String type;
    private List<CardProperties> listeCard;

}
