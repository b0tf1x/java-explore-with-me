package ru.practicum.ewm.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Validated
@RequestMapping("/admin/users")
public class UserController {
    private final UserService userService;
    @GetMapping
    public List<UserDto> findAll(List<Long>ids,
                                 @RequestParam(defaultValue = "0") Integer from,
                                 @RequestParam(defaultValue = "20") Integer size){
        PageRequest page = PageRequest.of(from / size, size);
        return userService.findAll(ids, page);
    }
    @PostMapping
    public UserDto create(@Validated @RequestBody UserDto userDto){
        return userService.create(userDto);
    }
    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId){
        userService.delete(userId);
    }
}
