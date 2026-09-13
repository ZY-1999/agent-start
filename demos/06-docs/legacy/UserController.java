package com.acme.user.controller;

import com.acme.user.dto.UserCreateDto;
import com.acme.user.dto.UserDto;
import com.acme.user.dto.UserQuery;
import com.acme.user.exception.DuplicatePhoneException;
import com.acme.user.exception.UserNotFoundException;
import com.acme.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理接口
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 注册新用户
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Valid @RequestBody UserCreateDto dto) {
        return userService.create(dto);
    }

    /**
     * 查询用户详情
     */
    @GetMapping("/{id}")
    public UserDto getById(@PathVariable Long id) {
        return userService.getById(id);
    }

    /**
     * 分页查询用户列表
     */
    @GetMapping
    public Page<UserDto> list(UserQuery query) {
        return userService.list(query);
    }

    /**
     * 更新用户昵称
     */
    @PatchMapping("/{id}/nickname")
    public UserDto updateNickname(@PathVariable Long id,
                                  @RequestParam String nickname) {
        return userService.updateNickname(id, nickname);
    }

    /**
     * 注销用户（逻辑删除）
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}

/* UserCreateDto 关键校验（简化展示）：
   @NotBlank String phone;       // 11 位手机号
   @NotBlank @Size(max=64) String nickname;
   @NotNull @Past LocalDate birthday;
*/
