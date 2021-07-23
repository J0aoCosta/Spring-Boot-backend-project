package com.training.springbootbuyitem.service;

import com.training.springbootbuyitem.entity.model.Role;
import com.training.springbootbuyitem.entity.model.User;
import com.training.springbootbuyitem.entity.request.user.UserLoginRequestDto;
import com.training.springbootbuyitem.enums.EnumEntity;
import com.training.springbootbuyitem.error.EntityNotFoundException;
import com.training.springbootbuyitem.error.WrongCredentialsException;
import com.training.springbootbuyitem.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class UserService implements IUserService, UserDetailsService {

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bcryptEncoder;

    @Override
    public List<User> list() {
        return userRepository.findAll();
    }

    @Override
    public User get(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(EnumEntity.USER.name(), id));
    }

    @Override
    public List<User> get(List<Long> id) {
        return null;
    }

    @Override
    public List<User> update(List<User> id) {
        return null;
    }

    @Override
    public void delete(Long id) {
        userRepository.delete(get(id));
    }

    @Override
    public User update(User entity) {
        User persistedUser = get(entity.getUserUid());
        if (StringUtils.hasText(entity.getName())) {
            persistedUser.setName(entity.getName());
        }
        userRepository.save(persistedUser);
        return persistedUser;
    }

    @Override
    public User save(User entity) {
        entity.setPassword(bcryptEncoder.encode(entity.getPassword()));

        Role role = roleService.findByName("USER");
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(role);

        entity.setRoles(roleSet);
        return userRepository.save(entity);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("user not found with username: " + username));

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), getAuthority(user));
    }

    private Set<SimpleGrantedAuthority> getAuthority(User user) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        log.info("ROLES--- " + user.getUsername());
        user.getRoles().forEach(role -> {
            log.info(role.getName());
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
        });
        return authorities;
    }

}
