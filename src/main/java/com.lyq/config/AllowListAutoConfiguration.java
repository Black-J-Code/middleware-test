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
@ConditionalOnClass(AllowListProperties.class)
@EnableConfigurationProperties(AllowListProperties.class)
public class AllowListAutoConfiguration {

    @Bean("allowListConfig")
    @ConditionalOnMissingBean
    public String allowListConfig(AllowListProperties allowListProperties) {
        return allowListProperties.getAllows();
    }

}
