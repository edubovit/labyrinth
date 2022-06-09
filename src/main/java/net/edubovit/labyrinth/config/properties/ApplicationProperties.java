package net.edubovit.labyrinth.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("labyrinth")
@Getter
@Setter
public class ApplicationProperties {

    private String[] allowedOrigins;

}
