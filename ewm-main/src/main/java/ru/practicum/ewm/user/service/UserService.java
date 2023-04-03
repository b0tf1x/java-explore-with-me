package ru.practicum.ewm.user.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> findAll(List<Long>ids, Pageable pageable);
    UserDto create(UserDto userDto);
    void delete(long userId);
}
