package org.pureboard.properties;

import lombok.Data;

@Data
public class BitbucketProperties {

    private String url;
    private String clientId;
    private String secretKey;
    private String urlAuth;

}
