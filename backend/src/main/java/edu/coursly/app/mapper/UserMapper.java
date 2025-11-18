package edu.coursly.app.mapper;

import edu.coursly.app.dto.UserRegistrationRequest;
import edu.coursly.app.dto.UserRegistrationResponse;
import edu.coursly.app.model.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserRegistrationRequest userRegistrationRequest);

    UserRegistrationResponse toDto(User user);
}
