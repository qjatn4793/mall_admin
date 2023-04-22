package com.shopping.mallAdmin.configuration;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

@AllArgsConstructor
@Configuration
public class SessionTimeout implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent sessionEvent) {
        sessionEvent.getSession().setMaxInactiveInterval(600); //세션만료60분
    }
    @Override
    public void sessionDestroyed(HttpSessionEvent sessionEvent) {
    }
}
