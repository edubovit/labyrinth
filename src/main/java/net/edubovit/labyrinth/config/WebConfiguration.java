package net.edubovit.labyrinth.config;

import net.edubovit.labyrinth.config.properties.ApplicationProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
@EnableWebMvc
@RequiredArgsConstructor
@Slf4j
public class WebConfiguration implements WebMvcConfigurer {

    private final ApplicationProperties applicationProperties;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] allowedOrigins = applicationProperties.getAllowedOrigins();
        log.info("Allowed origins: {}", Arrays.toString(allowedOrigins));
        if (allowedOrigins != null && allowedOrigins.length > 0) {
            registry.addMapping("/**")
                    .allowedOrigins(allowedOrigins)
                    .allowedMethods("*");
        }
    }

}
