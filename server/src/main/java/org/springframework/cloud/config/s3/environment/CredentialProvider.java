package org.springframework.cloud.config.s3.environment;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.auth.SystemPropertiesCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AWS Credential provider.
 */
@SuppressWarnings("InstanceVariableMayNotBeInitialized")
@Configuration
public class CredentialProvider {


	/**
	 * AWS Chain Provider that try credentials provider and stop at first success
	 * @return AWSCredentialsProvider
	 */
	@Bean
	public AWSCredentialsProvider credentialsProvider() {
		return new AWSCredentialsProviderChain(
				new EnvironmentVariableCredentialsProvider(),
				new SystemPropertiesCredentialsProvider(),
				new ProfileCredentialsProvider(),
				new InstanceProfileCredentialsProvider(false)
		);
	}
}
