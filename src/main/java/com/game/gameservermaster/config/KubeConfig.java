package com.game.gameservermaster.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "kube")
public class KubeConfig {

    private String imgLoc;
    private String imgSecret;
    private String defaultAddr;

}
