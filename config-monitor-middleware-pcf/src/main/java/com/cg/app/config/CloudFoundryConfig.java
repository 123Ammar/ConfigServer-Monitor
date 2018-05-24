package com.cg.app.config;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.doppler.DopplerClient;
import org.cloudfoundry.operations.DefaultCloudFoundryOperations;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.DefaultConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.cloudfoundry.reactor.doppler.ReactorDopplerClient;
import org.cloudfoundry.reactor.tokenprovider.PasswordGrantTokenProvider;
import org.cloudfoundry.reactor.uaa.ReactorUaaClient;
import org.cloudfoundry.uaa.UaaClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class CloudFoundryConfig {

	//https://github.com/corbtastik/cf-java-client-demo
	//https://github.com/nebhale/cf-java-client-webinar-2016
	
	/**
     * This instance is shared with Cloud Foundry Clients
     * @param apiHost
     * @return
     */
    @Bean
    DefaultConnectionContext connectionContext(@Value("${cf.apiHost}") String apiHost) {
        return DefaultConnectionContext.builder()
            .apiHost(apiHost).build();
    }

    /**
     * This instance is shared with Cloud Foundry Clients
     * @param username
     * @param password
     * @return
     */
    @Bean
    PasswordGrantTokenProvider tokenProvider(
        @Value("${cf.username}") String username,
        @Value("${cf.password}") String password) {

        return PasswordGrantTokenProvider.builder()
                .username(username)
                .password(password)
                .build();
    }

    // Cloud Foundry Clients provide direct access to CF REST APIs

    @Bean
    ReactorCloudFoundryClient cloudFoundryClient(
        ConnectionContext connectionContext,
        TokenProvider tokenProvider) {

        return ReactorCloudFoundryClient.builder()
                .connectionContext(connectionContext)
                    .tokenProvider(tokenProvider)
                        .build();
    }

    @Bean
    ReactorDopplerClient dopplerClient(
        ConnectionContext connectionContext,
        TokenProvider tokenProvider) {

        return ReactorDopplerClient.builder()
                .connectionContext(connectionContext)
                    .tokenProvider(tokenProvider)
                        .build();
    }

    @Bean
    ReactorUaaClient uaaClient(
        ConnectionContext connectionContext,
        TokenProvider tokenProvider) {

        return ReactorUaaClient.builder()
                .connectionContext(connectionContext)
                    .tokenProvider(tokenProvider)
                        .build();
    }

    @Bean
    DefaultCloudFoundryOperations cloudFoundryOperations(
        CloudFoundryClient cloudFoundryClient,
        DopplerClient dopplerClient,
        UaaClient uaaClient,
        @Value("${cf.organization}") String organization,
        @Value("${cf.space}") String space) {

        return DefaultCloudFoundryOperations.builder()
                .cloudFoundryClient(cloudFoundryClient)
                .dopplerClient(dopplerClient)
                .uaaClient(uaaClient)
                .organization(organization)
                .space(space)
                .build();
    }
}
