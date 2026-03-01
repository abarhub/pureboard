package org.pureboard.dto;

import lombok.Data;

@Data
public class JiraDto {

    private String key;
    private String url;
    private String summary;
    private String description;
    private String status;
    private String lastUpdate;
}
