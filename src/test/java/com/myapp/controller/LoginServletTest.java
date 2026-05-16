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
class LoginServletTest {

    private final LoginServlet servlet = new LoginServlet();

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

        verify(response).sendRedirect("login.jsp?error=csrf");
    }

    @Test
    void doPost_missingFields_redirectsMissing() throws Exception {
        ControllerTestSupport.mockValidCsrf(request, session);
        when(request.getParameter("identifier")).thenReturn(" ");
        when(request.getParameter("password")).thenReturn("");

        servlet.doPost(request, response);

        verify(response).sendRedirect("login.jsp?error=missing");
    }
}
