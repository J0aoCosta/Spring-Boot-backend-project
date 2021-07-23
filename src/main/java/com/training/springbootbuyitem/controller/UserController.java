package com.training.springbootbuyitem.controller;


import com.training.springbootbuyitem.constant.ItemStorageConstant;
import com.training.springbootbuyitem.entity.model.User;

import com.training.springbootbuyitem.entity.request.user.CreateUserRequestDto;
import com.training.springbootbuyitem.entity.response.user.CreateUserResponseDto;
import com.training.springbootbuyitem.entity.response.user.GetUserResponseDto;
import com.training.springbootbuyitem.entity.response.user.UpdateUserResponseDto;
import com.training.springbootbuyitem.enums.EnumOperation;
import com.training.springbootbuyitem.service.UserService;
import com.training.springbootbuyitem.utils.annotation.ServiceOperation;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RefreshScope
@RestController
@RequestMapping("user")
@CrossOrigin
@Slf4j
public class UserController{

    @Autowired
    UserService userService;

    @Autowired
    private ModelMapper mapper;


    @PostMapping("")
    @ServiceOperation("createUSer")
    public ResponseEntity<CreateUserResponseDto> createUser(@RequestBody @Valid CreateUserRequestDto request) {
        MDC.put(ItemStorageConstant.OPERATION, EnumOperation.CreateItem.name());
        return new ResponseEntity<>(mapper.map(userService.save(mapper.map(request, User.class)), CreateUserResponseDto.class), HttpStatus.CREATED);
    }

    //    @Override
//
    @GetMapping("/{id}")
    @ServiceOperation("getUser")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GetUserResponseDto> getUser(@PathVariable("id") Long id, HttpSession session) {
        MDC.put(ItemStorageConstant.OPERATION, EnumOperation.GetUSer.name());
        return new ResponseEntity<>(mapper.map(userService.get(id), GetUserResponseDto.class), HttpStatus.OK);
    }

    @GetMapping("/all")
    @ServiceOperation("listUsers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<GetUserResponseDto>> listUsers() {
        MDC.put(ItemStorageConstant.OPERATION, EnumOperation.ListUser.name());
        return new ResponseEntity<>(userService.list().stream().map(i -> mapper.map(i, GetUserResponseDto.class)).collect(
                Collectors.toList()), HttpStatus.OK);
    }

    //    @Override
    @PatchMapping("/{id}")
    @ServiceOperation("updateIUser")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UpdateUserResponseDto> updateUser(@PathVariable("id") Long id, @RequestBody User user) {
        MDC.put(ItemStorageConstant.OPERATION, EnumOperation.UpdateUser.name());
        user.setUserUid(id);
        return new ResponseEntity<>(mapper.map(userService.update(user), UpdateUserResponseDto.class), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @ServiceOperation("deleteUser")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable("id") Long id) {
        MDC.put(ItemStorageConstant.OPERATION, EnumOperation.DeleteUser.name());
        userService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // TODO authenticated user gets info from his profile
    @GetMapping("/")
    @ServiceOperation("getUser")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<String> getUser( HttpSession session) {
        MDC.put(ItemStorageConstant.OPERATION, EnumOperation.GetUSer.name());
        return new ResponseEntity<>("TODO", HttpStatus.OK);
    }

    // TODO ...
    @PatchMapping("/")
    @ServiceOperation("updateIUser")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<String> updateUser(@RequestBody User user) {
        return new ResponseEntity<>("TODO", HttpStatus.OK);
    }

    // TODO ...
    @DeleteMapping("/")
    @ServiceOperation("deleteUser")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<String> deleteUser() {
        return new ResponseEntity<>("TODO",HttpStatus.OK);
    }
}