package com.defers.integration.app.config;

import com.defers.integration.domain.user.port.in.UserUseCase;
import com.defers.integration.domain.user.port.out.UserRepository;
import com.defers.integration.domain.user.usecase.UserService;
import jakarta.jms.ConnectionFactory;
import org.modelmapper.ModelMapper;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

@Configuration
@ConfigurationPropertiesScan(basePackages = "com.defers.integration")
public class AppConfig {
    @Bean
    public UserUseCase userUseCase(UserRepository userRepository) {
        return new UserService(userRepository);
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public MessageConverter messageConverter() {
        var converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }

    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setMessageConverter(messageConverter);
        return jmsTemplate;
    }
}
