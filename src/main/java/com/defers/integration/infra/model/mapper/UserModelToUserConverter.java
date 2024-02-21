package com.defers.integration.infra.model.mapper;

import com.defers.integration.domain.user.model.User;
import com.defers.integration.infra.model.UserModel;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserModelToUserConverter implements Converter<UserModel, User> {
    private final ModelMapper modelMapper;

    public UserModelToUserConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public User convert(UserModel source) {
        return modelMapper.map(source, User.class);
    }
}
