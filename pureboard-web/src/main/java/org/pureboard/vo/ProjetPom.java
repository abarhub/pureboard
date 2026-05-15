package org.pureboard.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ProjetPom {

    private String nom;
    private ArtefactMaven parent;
    private ArtefactMaven artifact;
    private Map<String,String> properties;
    private List<ArtefactMaven> dependencies;
    private List<ProjetPom> projetPomEnfants;
    private ProjetNode projetNode;
    private ProjetRust projetRust;
    private ProjetGo projetGo;

}
