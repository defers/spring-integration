package com.defers.integration.infra.config;

import com.defers.integration.domain.user.port.out.UserRepository;
import com.defers.integration.infra.repository.UserInMemoryRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;

@Configuration
public class DBConfig {
    @Bean
    public UserRepository userRepository(ConversionService conversionService) {
        return new UserInMemoryRepository(conversionService);
    }
}
