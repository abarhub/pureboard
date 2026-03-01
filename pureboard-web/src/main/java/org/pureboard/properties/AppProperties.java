package org.pureboard.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private JiraProperties jira;
    private BitbucketProperties bitbucket;

    private List<DashboardProperties> dashboards;
}
