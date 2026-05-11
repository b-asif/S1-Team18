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
class LogoutServletTest {

    private final LogoutServlet servlet = new LogoutServlet();

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Test
    void doPost_invalidCsrf_redirectsWithCsrfError() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("csrfToken")).thenReturn("expected-token");
        when(request.getParameter("csrfToken")).thenReturn("wrong-token");

        servlet.doPost(request, response);

        verify(response).sendRedirect("login.jsp?error=csrf");
    }

    @Test
    void doPost_validCsrf_invalidatesSessionAndRedirectsLogin() throws Exception {
        ControllerTestSupport.mockValidCsrf(request, session);

        servlet.doPost(request, response);

        verify(session).invalidate();
        verify(response).sendRedirect("login.jsp");
    }
}
