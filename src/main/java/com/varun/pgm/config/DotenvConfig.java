package com.varun.pgm.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class DotenvConfig {

    @Bean
    public Dotenv dotenv(ConfigurableEnvironment environment) {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        // Add .env properties to Spring Environment
        Map<String, Object> envProperties = new HashMap<>();
        dotenv.entries().forEach(entry -> {
            envProperties.put(entry.getKey(), entry.getValue());
        });

        environment.getPropertySources().addFirst(
            new MapPropertySource("dotenv", envProperties)
        );

        return dotenv;
    }
}
