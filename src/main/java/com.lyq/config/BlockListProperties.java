package com.lyq.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Jack
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "com.ayla.blocklist")
public class BlockListProperties {

    private String blocks;

}
