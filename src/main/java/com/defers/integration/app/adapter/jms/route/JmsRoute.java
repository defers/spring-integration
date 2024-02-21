package com.defers.integration.app.adapter.jms.route;

import jakarta.jms.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.jms.dsl.Jms;

@Configuration
public class JmsRoute {
    private static final Logger log = LoggerFactory.getLogger(JmsRoute.class.getName());

    @Bean
    public IntegrationFlow readFromQueue(ConnectionFactory connectionFactory) {
        return IntegrationFlow.from(Jms.messageDrivenChannelAdapter(connectionFactory)
                        .destination("integration-users"))
                .log()
                .handle((payload, headers) -> {
                    log.info("Handle readFromQueue from queue integration-users");
                    return payload;
                })
                .channel("reply")
                .get();
    }

    @Bean
    public IntegrationFlow handleReply() {
        return IntegrationFlow.from("reply")
                .log()
                .handle((payload, headers) -> {
                    log.info("Handle Reply from queue");
                    return null;
                })
                .get();
    }
}
