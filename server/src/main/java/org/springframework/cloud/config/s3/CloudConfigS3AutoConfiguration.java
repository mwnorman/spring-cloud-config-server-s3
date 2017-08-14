package org.springframework.cloud.config.s3;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnBean(CloudConfigS3Configuration.Marker.class)
@EnableConfigurationProperties(CloudConfigS3Properties.class)
@Import({S3EnvironmentRepositoryConfiguration.class})
public class CloudConfigS3AutoConfiguration {

}