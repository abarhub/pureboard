package org.pureboard.pureboard;

import org.pureboard.properties.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(AppProperties.class)
@SpringBootApplication
public class PureboardApplication {

    public static void main(String[] args) {
        SpringApplication.run(PureboardApplication.class, args);
    }

}
