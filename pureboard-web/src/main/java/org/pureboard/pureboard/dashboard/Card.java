package org.pureboard.pureboard.dashboard;

import lombok.Data;
import org.pureboard.pureboard.properties.CardProperties;
import org.pureboard.pureboard.vo.Projet;

import java.nio.file.Path;

@Data
public class Card {
    private String id;
    private String titre;
    private TypeCard type;
    private CardProperties cardProperties;
    private Path pomMaven;
    private Projet projet;
}
