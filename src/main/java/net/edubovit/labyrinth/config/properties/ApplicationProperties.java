package net.edubovit.labyrinth.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.Positive;

@Configuration
@ConfigurationProperties("labyrinth")
@Getter
@Setter
public class ApplicationProperties {

    private String[] allowedOrigins;

    @Positive
    private int gameFlushPeriod;

}
