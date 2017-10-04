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
package org.springframework.cloud.config.s3.environment;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.ConfigurationException;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.environment.PropertySource;
import org.springframework.cloud.config.s3.CloudConfigS3Properties;
import org.springframework.cloud.config.s3.yaml.YamlFileConfiguration;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;
import java.util.Properties;

/**
 * Implementation of {@link EnvironmentRepository} that is backed by AWS S3.
 *
 * @author Mike Norman
 */
@Slf4j
public class S3EnvironmentRepository implements EnvironmentRepository, Ordered {

    public static final String LABEL = "label";
    public static final String PROFILE = "profile";
    public static final String DEFAULT = "default";
    public static final String[] DEFAULT_PROFILES = new String[]{DEFAULT};
    public static final String DEFAULT_LABEL = null;

    private final String searchPaths;

    protected CloudConfigS3Properties s3Properties;
    protected ConfigurableEnvironment environment;

    protected int order = Ordered.LOWEST_PRECEDENCE;


    @Override
    public int getOrder() {
        return order;
    }
    public void setOrder(int order) {
        this.order = order;
    }

    protected AmazonS3 s3Client = null;
    protected String s3ApplicationDotYMLString = null;

    public S3EnvironmentRepository(ConfigurableEnvironment environment, CloudConfigS3Properties s3Properties,
                                   AWSCredentialsProvider credentialProvider, final String region, String searchPaths) {
        this.environment = environment;
        this.s3Properties = s3Properties;
        this.searchPaths = searchPaths;
        log.info("S3EnvironmentRepository built with environment: {}; properties={}",
            environment.toString(), s3Properties.toString());
        AmazonS3ClientBuilder s3ClientBuilder = AmazonS3ClientBuilder.standard()
                .withRegion(region).withCredentials(credentialProvider);
        s3Client = s3ClientBuilder.build();
    }

    @Override
    public Environment findOne(String application, String profile, String label) {
        log.info("finding for application={}, profile={}, label={}", application, profile, label);
        StringBuilder objectKeyPrefix = new StringBuilder(application);
        String validProfile = profile;
        if(StringUtils.isEmpty(validProfile)) {
            validProfile = DEFAULT;
        }
        objectKeyPrefix.append("-").append(validProfile);
        if(!StringUtils.isEmpty(label)) {
            objectKeyPrefix.append("-").append(label);
        }
        // find properties config file
        Environment environment = searchProperties(application, profile, objectKeyPrefix.toString());
        if(environment == null) {
            // find yml config file
            environment = searchYaml(application, profile, objectKeyPrefix.toString());
        }
        return environment;
    }

    private Environment searchProperties(String application, String profile, String objectKeyPrefix) {
        String objectKey = objectKeyPrefix + ".properties";

        if(!StringUtils.isEmpty(searchPaths)) {
            objectKey = searchPaths + objectKey;
        }
        log.info("finding S3 resource '{}' from bucket '{}'", objectKey, s3Properties.getBucketName());

        Environment environment;
        Properties props;
        Map<String, String> properties;
        PropertySource propertySource;
        try {
            S3Object s3Object = s3Client.getObject(s3Properties.getBucketName(), objectKey);
            environment = new Environment(application, profile);

            props = new Properties();
            try (InputStream in = s3Object.getObjectContent()) {
                props.load(in);
            } catch (IOException ioex) {
                log.error(ioex.getMessage());
                return null;
            }
            properties = Maps.fromProperties(props);

            propertySource = new PropertySource(application, properties);
            environment.add(propertySource);
        } catch (Exception ex) {
            log.warn("Properties '{}' not found in S3 bucket '{}'", objectKey, s3Properties.getBucketName());
            return null;
        }

        return environment;
    }

    private Environment searchYaml(String application, String profile, String objectKeyPrefix) {
        String objectKey = objectKeyPrefix + ".yml";

        if(!StringUtils.isEmpty(searchPaths)) {
            objectKey = searchPaths + objectKey;
        }
        log.info("finding S3 resource '{}' from bucket '{}'", objectKey, s3Properties.getBucketName());

        S3Object s3Object = s3Client.getObject(s3Properties.getBucketName(), objectKey);
        Environment environment = new Environment(application, profile);

        YamlFileConfiguration yamlFileConfiguration = new YamlFileConfiguration();
        try(InputStream in = s3Object.getObjectContent()) {
            Reader targetReader = new InputStreamReader(in);
            yamlFileConfiguration.load(targetReader);
        } catch (IOException|ConfigurationException ex) {
            log.error(ex.getMessage());
            return null;
        }
        Map<String, String> properties = yamlFileConfiguration.getProperties();

        PropertySource propertySource = new PropertySource(application, properties);
        environment.add(propertySource);
        return environment;
    }

   

}