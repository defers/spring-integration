package com.defers.integration.infra.model.mapper;

import com.defers.integration.domain.user.model.User;
import com.defers.integration.infra.model.UserModel;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserToUserModelConverter implements Converter<User, UserModel> {
    private final ModelMapper modelMapper;

    public UserToUserModelConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public UserModel convert(User source) {
        return modelMapper.map(source, UserModel.class);
    }
}
