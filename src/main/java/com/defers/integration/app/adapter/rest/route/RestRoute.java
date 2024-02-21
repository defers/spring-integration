package com.defers.integration.app.adapter.rest.route;

import com.defers.integration.app.adapter.rest.dto.Response;
import com.defers.integration.domain.user.model.User;
import com.defers.integration.domain.user.port.in.UserUseCase;
import jakarta.jms.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.http.dsl.Http;
import org.springframework.integration.jms.dsl.Jms;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandlingException;

@Configuration
public class RestRoute {
    private static final Logger log = LoggerFactory.getLogger(RestRoute.class.getName());
    private static final String BASE_PATH = "/users";
    private final UserUseCase userUseCase;
    private final ConnectionFactory connectionFactory;
    private final MessageConverter messageConverter;

    public RestRoute(
            UserUseCase userUseCase,
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter) {
        this.userUseCase = userUseCase;
        this.connectionFactory = connectionFactory;
        this.messageConverter = messageConverter;
    }

    @Bean
    public MessageChannel errorChannel() {
        return MessageChannels.direct().getObject();
    }

    @Bean
    public IntegrationFlow exceptionHandler() {
        return IntegrationFlow.from(errorChannel())
                .log(LoggingHandler.Level.ERROR, "Exception handled")
                .handle((payload, headers) -> new Response(
                        ((MessageHandlingException) payload).getCause().getMessage(), "Error"))
                .get();
    }

    @Bean
    public IntegrationFlow createUser() {
        return IntegrationFlow.from(Http.inboundGateway(BASE_PATH)
                        .requestMapping(method -> method.methods(HttpMethod.POST))
                        .requestPayloadType(User.class)
                        .crossOrigin(cors -> cors.origin("*"))
                        .errorChannel(errorChannel()))
                .handle((payload, headers) -> {
                    log.info("Get message createUser: %s".formatted(payload));
                    var user = userUseCase.create((User) payload);
                    log.info("Created user: %s".formatted(user));
                    if (user != null) {
                        throw new RuntimeException("Тестовая ошибка");
                    }
                    return user;
                })
                .get();
    }

    @Bean
    public IntegrationFlow getAllUsers() {
        return IntegrationFlow.from(Http.inboundGateway(BASE_PATH)
                        .requestMapping(method -> method.methods(HttpMethod.GET))
                        .errorChannel(errorChannel()))
                .handle((payload, headers) -> userUseCase.findAll())
                .handle((payload, header) -> {
                    log.info("Handle getAllUsers");
                    return payload;
                })
                .get();
    }

    @Bean
    public IntegrationFlow sendUserToQueue() {
        return IntegrationFlow.from(Http.inboundGateway(BASE_PATH + "/to-queue")
                        .requestMapping(method -> method.methods(HttpMethod.POST))
                        .requestPayloadType(User.class))
                .log()
                .channel(MessageChannels.publishSubscribe("jmsChannel"))
                .handle((payload, headers) -> new Response("Message sent to queue integration-users", null))
                .get();
    }

    @Bean
    public IntegrationFlow handleJmsChannel() {
        return IntegrationFlow.from("jmsChannel")
                .log()
                .handle(Jms.outboundAdapter(connectionFactory)
                        .configureJmsTemplate(t -> t.jmsMessageConverter(messageConverter))
                        .destination("integration-users"))
                .get();
    }
}
