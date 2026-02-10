package org.pureboard.dashboard;

import lombok.Data;
import org.pureboard.properties.CardProperties;
import org.pureboard.utils.GroovyCards;
import org.pureboard.vo.Projet;

import java.nio.file.Path;

@Data
public class Card {
    private String id;
    private String titre;
    private TypeCard type;
    private CardProperties cardProperties;
    private Path pomMaven;
    private Projet projet;
    private GroovyCards objectGroovy;
}
