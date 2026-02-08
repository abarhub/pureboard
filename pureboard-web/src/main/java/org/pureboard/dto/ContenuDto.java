package org.pureboard.dto;

import lombok.Data;

@Data
public class ContenuDto {

    private TypeContenu type;

    private String texte;

    private TableauDto tableau;
}
