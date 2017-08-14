package org.springframework.cloud.config.server.s3.client;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.config.s3.EnableS3CloudConfig;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(
    exclude = {
        org.springframework.cloud.config.server.config.ConfigServerAutoConfiguration.class
    }
)
@EnableS3CloudConfig
public class Client {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = new SpringApplicationBuilder(Client.class)
            .web(false)
            .run(args)
        ;
        applicationContext.getEnvironment().getProperty("foo.bar");
    }
}
