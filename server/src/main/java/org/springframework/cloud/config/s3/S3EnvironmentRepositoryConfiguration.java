/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cloud.config.s3;

import com.amazonaws.auth.AWSCredentialsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.config.s3.environment.S3EnvironmentRepository;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * @author Mike Norman
 */
@Configuration
public class S3EnvironmentRepositoryConfiguration {

    @Autowired
    private ConfigurableEnvironment environment;

    @Autowired
    protected CloudConfigS3Properties s3Properties;

    @Bean
    @Primary
    public EnvironmentRepository environmentRepository(AWSCredentialsProvider credentialProvider,
                                                       @Value("${cloud.aws.region}") final String region,
                                                       @Value("${spring.cloud.config.s3.searchPaths:}") final String searchPaths) {
        return new S3EnvironmentRepository(environment, s3Properties, credentialProvider, region, searchPaths);
    }

    @Bean
    public CloudConfigS3Properties buildCloudConfigS3Properties() {
        return new CloudConfigS3Properties();
    }

}