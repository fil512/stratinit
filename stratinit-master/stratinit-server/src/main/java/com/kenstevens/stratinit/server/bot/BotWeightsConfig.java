package com.kenstevens.stratinit.server.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class BotWeightsConfig {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Bean
    public PhasedBotWeights phasedBotWeights() {
        try {
            ClassPathResource resource = new ClassPathResource("bot-weights.json");
            if (resource.exists()) {
                InputStream is = resource.getInputStream();
                String json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                logger.info("Loaded bot weights from bot-weights.json");
                // If JSON has "phases" key, it's phased format; otherwise wrap flat
                if (json.contains("\"phases\"")) {
                    return PhasedBotWeights.fromJson(json);
                } else {
                    return PhasedBotWeights.fromFlat(BotWeights.fromJson(json));
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to load bot-weights.json, using defaults", e);
        }
        logger.info("Using default bot weights");
        return new PhasedBotWeights();
    }
}
