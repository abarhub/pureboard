package org.pureboard.dto;

import lombok.Data;

import java.util.List;

@Data
public class TableauDto {

    private List<String> headers;

    private List<List<ContenuDto>> lignes;

}
