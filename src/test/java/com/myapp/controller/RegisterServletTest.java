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
class RegisterServletTest {

    private final RegisterServlet servlet = new RegisterServlet();

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Test
    void doPost_invalidCsrf_redirectsCsrfError() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("csrfToken")).thenReturn("expected-token");
        when(request.getParameter("csrfToken")).thenReturn("wrong-token");

        servlet.doPost(request, response);

        verify(response).sendRedirect("register.jsp?error=csrf");
    }

    @Test
    void doPost_missingFields_redirectsMissing() throws Exception {
        ControllerTestSupport.mockValidCsrf(request, session);
        when(request.getParameter("firstName")).thenReturn("Jane");
        when(request.getParameter("lastName")).thenReturn("Smith");
        when(request.getParameter("email")).thenReturn(" ");
        when(request.getParameter("userName")).thenReturn("janes");
        when(request.getParameter("password")).thenReturn("ValidPass123");

        servlet.doPost(request, response);

        verify(response).sendRedirect("register.jsp?error=missing");
    }

    @Test
    void doPost_shortPassword_redirectsPasswordError() throws Exception {
        ControllerTestSupport.mockValidCsrf(request, session);
        when(request.getParameter("firstName")).thenReturn("Jane");
        when(request.getParameter("lastName")).thenReturn("Smith");
        when(request.getParameter("email")).thenReturn("jane@example.com");
        when(request.getParameter("userName")).thenReturn("janes");
        when(request.getParameter("password")).thenReturn("short");

        servlet.doPost(request, response);

        verify(response).sendRedirect("register.jsp?error=password");
    }
}
