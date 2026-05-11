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
class ProfileServletTest {

    private final ProfileServlet servlet = new ProfileServlet();

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

        verify(response).sendRedirect("profile?error=csrf");
    }

    @Test
    void doPost_unknownAction_redirectsInvalid() throws Exception {
        ControllerTestSupport.mockAuthenticatedSession(request, session);
        ControllerTestSupport.mockValidCsrf(request, session);
        when(request.getParameter("action")).thenReturn("unknown");

        servlet.doPost(request, response);

        verify(response).sendRedirect("profile?error=invalid");
    }

    @Test
    void doPost_updatePasswordTooShort_redirectsPwshort() throws Exception {
        ControllerTestSupport.mockAuthenticatedSession(request, session);
        ControllerTestSupport.mockValidCsrf(request, session);
        when(request.getParameter("action")).thenReturn("updatePassword");
        when(request.getParameter("currentPassword")).thenReturn("CurrentPassword123");
        when(request.getParameter("newPassword")).thenReturn("short");
        when(request.getParameter("confirmPassword")).thenReturn("short");

        servlet.doPost(request, response);

        verify(response).sendRedirect("profile?error=pwshort");
    }
}
