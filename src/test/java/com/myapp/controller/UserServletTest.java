package com.myapp.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServletTest {

    private final UserServlet servlet = new UserServlet();

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Test
    void doGet_authenticatedNonAdmin_redirectsToDashboard() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(1);
        when(session.getAttribute("isAdmin")).thenReturn(Boolean.FALSE);

        servlet.doGet(request, response);

        verify(response).sendRedirect("dashboard");
    }

    @Test
    void doGet_withoutSession_redirectsToLogin() throws Exception {
        when(request.getSession(false)).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).sendRedirect("login.jsp");
    }
}
