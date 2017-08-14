package org.springframework.cloud.config.s3;

import org.springframework.context.annotation.Bean;

public class CloudConfigS3Configuration {
    class Marker {}

    @Bean
    public Marker enableMarker() {
        return new Marker();
    }
}
