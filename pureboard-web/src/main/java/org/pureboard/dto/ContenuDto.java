package org.pureboard.dto;

import lombok.Data;

import java.util.List;

@Data
public class ContenuDto {

    private TypeContenu type;

    private String texte;

    private TableauDto tableau;

    private String classe;

    private String lien;

    private List<ContenuDto> listeContenu;

    private String nomMethode;

    private String parametresMethode;
}
