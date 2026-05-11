package com.myapp.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

final class ControllerTestSupport {
    static final String CSRF_TOKEN = "test-csrf-token";

    private ControllerTestSupport() {
    }

    static void mockValidSession(HttpServletRequest request, HttpSession session) {
        lenient().when(request.getSession(false)).thenReturn(session);
    }

    static void mockAuthenticatedSession(HttpServletRequest request, HttpSession session) {
        mockValidSession(request, session);
        lenient().when(session.getAttribute("userId")).thenReturn(1);
    }

    static void mockValidCsrf(HttpServletRequest request, HttpSession session) {
        lenient().when(request.getSession(false)).thenReturn(session);
        lenient().when(session.getAttribute("csrfToken")).thenReturn(CSRF_TOKEN);
        lenient().when(request.getParameter("csrfToken")).thenReturn(CSRF_TOKEN);
    }
}
