package com.training.springbootbuyitem.controller;

import com.training.springbootbuyitem.configuration.SessionManagement;
import com.training.springbootbuyitem.configuration.TokenList;
import com.training.springbootbuyitem.configuration.auth.TokenProvider;
import com.training.springbootbuyitem.entity.request.user.UserLoginRequestDto;
import com.training.springbootbuyitem.entity.response.user.UserLoginResponseDto;
import com.training.springbootbuyitem.utils.annotation.ServiceOperation;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RefreshScope
@RestController
@CrossOrigin
@Slf4j
public class AuthController  {

    @Value("${jwt.header.string}")
    public String HEADER_STRING;

    @Value("${jwt.token.prefix}")
    public String TOKEN_PREFIX;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ModelMapper mapper;


    @Autowired
    private TokenProvider jwtTokenUtil;

    @Autowired
    private SessionManagement sessionManagement;

    @Autowired
    private TokenList tokenList;

    @GetMapping("/auth")
    public String hello() {
        return "Auth test";
    }

    @PostMapping("/login")
    @ServiceOperation("login")
    public ResponseEntity<UserLoginResponseDto> login(@RequestBody UserLoginRequestDto authenticationRequest, HttpServletRequest request) {
//        MDC.put(ItemStorageConstant.OPERATION, EnumOperation.Login.name());

        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getUsername(),
                        authenticationRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        HttpSession session = request.getSession();
        session.setAttribute("user", authenticationRequest.getUsername());
        log.info("SESSION CREATED --- " + session.getId());

        final String token = jwtTokenUtil.generateToken(authentication);
        tokenList.addToken(token, authenticationRequest.getUsername());
        sessionManagement.addSession(authenticationRequest.getUsername(), session);

        return new ResponseEntity<>(mapper.map(new UserLoginResponseDto(token), UserLoginResponseDto.class), HttpStatus.OK);
    }

    @PostMapping("/logout")
    @ServiceOperation("logout")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<UserLoginResponseDto> logout(HttpServletRequest request) {

        final String header = request.getHeader(HEADER_STRING);
        final String username = (String) request.getSession().getAttribute("user");
        final String authToken = request.getHeader(HEADER_STRING).replace(TOKEN_PREFIX, "").trim();

        if (tokenList.isLoggedInToken(authToken)) {
            tokenList.moveTokenToBlackList(authToken);
            log.info("BLACKLISTING TOKEN ---" + authToken);
        }

        if (sessionManagement.hasSession(username)) {
            HttpSession session = sessionManagement.getSession(username);
            log.info("SESSION TO LOGOUT---" + session.getId());
            session.invalidate();
            sessionManagement.removeSession(username);
            log.info("--");
        }

        SecurityContextHolder.clearContext();

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/logout/all")
    @ServiceOperation("logout all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserLoginResponseDto> logoutAll() {

        tokenList.clearLoggedInTokens();
        sessionManagement.clearSessions();

        SecurityContextHolder.clearContext();

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
