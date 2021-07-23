package com.training.springbootbuyitem.configuration;

import com.training.springbootbuyitem.configuration.auth.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class TokenList {

    @Autowired
    private TokenProvider tokenProvider;

    private Map<String, String> loggedInTokens = new HashMap<>();
    private Map<String, String> blackListTokens = new HashMap<>();

    public boolean isLoggedInToken(String token) {
//        boolean isLogged = loggedInTokens.get(token)!= null;
//        boolean isExpired = tokenProvider.isTokenExpired(token);
//        if (isLogged  && isExpired) {
//            moveTokenToBlackList(token);
//        }
        return loggedInTokens.get(token)!= null;
    }

    public boolean isBlackListToken(String token) {
        return blackListTokens.get(token) != null;
    }

    public void addToken(String token, String username) {
        loggedInTokens.put(token, username);
    }

    public void addBlacklistToken(String token, String username) {
        blackListTokens.put(token, username);
    }

    public void moveTokenToBlackList(String token) {
        if (isLoggedInToken(token)) {
            String user = loggedInTokens.get(token);;
            loggedInTokens.remove(token, user);
            blackListTokens.put(token, user);
        }
    }

    public List<String> getLoggedInTokens() {
        return loggedInTokens.values().stream().collect(Collectors.toList());
    }

    public List<String> getBlackListTokens() {
        return blackListTokens.values().stream().collect(Collectors.toList());
    }

    public void clearLoggedInTokens() {
        loggedInTokens.clear();
    }
}
