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

import java.io.IOException;
import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.s3.CloudConfigS3Properties;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.StreamUtils;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

/**
 * Implementation of {@link EnvironmentRepository} that is backed by AWS S3.
 *
 * @author Mike Norman
 */
public class S3EnvironmentRepository implements EnvironmentRepository, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().getClass());

    public static final String LABEL = "label";
    public static final String PROFILE = "profile";
    public static final String DEFAULT = "default";
    public static final String[] DEFAULT_PROFILES = new String[]{DEFAULT};
    public static final String DEFAULT_LABEL = null;

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

    public S3EnvironmentRepository(ConfigurableEnvironment environment, CloudConfigS3Properties s3Properties) {
        this.environment = environment;
        this.s3Properties = s3Properties;
        logger.info("S3EnvironmentRepository built with environment: {}; properties={}",
            environment.toString(), s3Properties.toString());
        s3Client = AmazonS3ClientBuilder.standard().withRegion("us-east-1").build();
        S3Object s3ApplicationDotYML =
            s3Client.getObject(s3Properties.getBucketName(), s3Properties.getPrefix() + "application.yml");
        S3ObjectInputStream inputStream = s3ApplicationDotYML.getObjectContent();
        try {
            s3ApplicationDotYMLString = StreamUtils.copyToString(inputStream, null);
        }
        catch (IOException e) {
            logger.error("something went wrong retrieving application.yml from S3", e);
        }
    }

    @Override
    public Environment findOne(String application, String profile, String label) {
        logger.info("finding for application={}, profile={}, label={}", application, profile, label);
        //Magic occurs here
        return null;
    }

}