package org.springframework.cloud.config.s3;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("spring.cloud.config.s3")
public class CloudConfigS3Properties {

    /**
     * S3 Bucket name for resource path (default is empty)
     */
    protected String bucketName;
    public String getBucketName() {
        return bucketName;
    }
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    /**
     * S3 Prefix for resource path (default is empty)
     */
    protected String prefix;
    public String getPrefix() {
        return prefix;
    }
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String toString() {
        return new StringBuilder("CloudConfigS3Properties [bucketName=")
            .append(bucketName)
            .append(", prefix=")
            .append(prefix)
            .append("]")
            .toString();
    }
}
