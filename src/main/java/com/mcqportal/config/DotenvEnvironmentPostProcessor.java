package com.mcqportal.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class DotenvEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Map<String, Object> envProperties = new HashMap<>();
        loadDotEnv(new File(".env"), envProperties);
        loadDotEnv(new File("mcq-portal-main/.env"), envProperties);

        if (!envProperties.isEmpty()) {
            environment.getPropertySources().addFirst(new MapPropertySource("dotenvProperties", envProperties));
        }
    }

    private void loadDotEnv(File file, Map<String, Object> props) {
        if (!file.exists() || !file.isFile()) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                int idx = line.indexOf('=');
                if (idx > 0) {
                    String key = line.substring(0, idx).trim();
                    String value = line.substring(idx + 1).trim();
                    if ((value.startsWith("\"") && value.endsWith("\"")) || (value.startsWith("'") && value.endsWith("'"))) {
                        value = value.substring(1, value.length() - 1);
                    }
                    if (StringUtils.hasText(key) && !props.containsKey(key)) {
                        props.put(key, value);
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }
}
