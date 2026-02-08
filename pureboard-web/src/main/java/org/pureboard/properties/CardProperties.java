package org.pureboard.properties;

import lombok.Data;

import java.util.List;

@Data
public class CardProperties {

    private String type;
    private String title;
    private List<ChampsProperties> champs;
    private String repertoire;
    private String script;
}
