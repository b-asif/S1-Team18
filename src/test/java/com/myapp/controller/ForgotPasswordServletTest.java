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
class ForgotPasswordServletTest {

    private final ForgotPasswordServlet servlet = new ForgotPasswordServlet();

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

        verify(response).sendRedirect("forgot-password?error=csrf");
    }

    @Test
    void doPost_lookupMissingIdentifier_redirectsMissing() throws Exception {
        ControllerTestSupport.mockValidCsrf(request, session);
        when(request.getParameter("action")).thenReturn("lookup");
        when(request.getParameter("identifier")).thenReturn(" ");

        servlet.doPost(request, response);

        verify(response).sendRedirect("forgot-password?error=missing");
    }

    @Test
    void doPost_resetMissingToken_redirectsSessionError() throws Exception {
        ControllerTestSupport.mockValidCsrf(request, session);
        when(request.getParameter("action")).thenReturn("reset");
        when(request.getParameter("resetToken")).thenReturn("");

        servlet.doPost(request, response);

        verify(response).sendRedirect("forgot-password?step=2&error=session");
    }
}
