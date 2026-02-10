package org.pureboard.properties;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class CardProperties {

    private String type;
    private String title;
    private List<ChampsProperties> champs;
    private List<String> repertoire;
    private String script;
    private Map<String, String> parametres;
}
