package com.training.springbootbuyitem.configuration;

import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Component
public class SessionManagement {
    private Map<String, HttpSession> sessions = new HashMap();

    public void addSession(String username, HttpSession session) {
        sessions.put(username, session);
    }

    public void removeSession(String username) {
        if (sessions.get(username) != null) {
            sessions.remove(username);
        }
    }

    public HttpSession getSession(String username) {
        return sessions.get(username);
    }

    public boolean hasSession(String username) {
        return sessions.get(username) != null;
    }

    public void clearSessions() {
        sessions.clear();
    }
}
