package org.pureboard.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class TableauDto {

    private List<String> headers;

    private List<List<ContenuDto>> lignes;

}
