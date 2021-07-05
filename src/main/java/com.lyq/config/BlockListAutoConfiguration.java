package com.lyq.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Jack
 */
@Configuration
@ConditionalOnClass(BlockListProperties.class)
@EnableConfigurationProperties(BlockListProperties.class)
public class BlockListAutoConfiguration {

    @Bean("blockListConfig")
    @ConditionalOnMissingBean
    public String blockListConfig(BlockListProperties blockListProperties) {
        return blockListProperties.getBlocks();
    }

}
