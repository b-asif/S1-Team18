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
class ApplicationServletTest {

    private final ApplicationServlet servlet = new ApplicationServlet();

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Test
    void doGet_withoutSession_redirectsToLogin() throws Exception {
        when(request.getSession(false)).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).sendRedirect("login.jsp");
    }

    @Test
    void doPost_invalidCsrf_redirectsCsrfError() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("csrfToken")).thenReturn("expected-token");
        when(request.getParameter("csrfToken")).thenReturn("wrong-token");

        servlet.doPost(request, response);

        verify(response).sendRedirect("applications?error=csrf");
    }

    @Test
    void doPost_addMissingRequiredFields_redirectsMissing() throws Exception {
        ControllerTestSupport.mockValidCsrf(request, session);
        ControllerTestSupport.mockAuthenticatedSession(request, session);
        when(request.getParameter("action")).thenReturn(null);
        when(request.getParameter("jobTitle")).thenReturn("");
        when(request.getParameter("companyName")).thenReturn("Acme");
        when(request.getParameter("appStatus")).thenReturn("Applied");
        when(request.getParameter("dateApplied")).thenReturn("2026-04-28");

        servlet.doPost(request, response);

        verify(response).sendRedirect("applications?error=missing");
    }

    @Test
    void doPost_deleteWithNonNumericId_redirectsApplications() throws Exception {
        ControllerTestSupport.mockValidCsrf(request, session);
        ControllerTestSupport.mockAuthenticatedSession(request, session);
        when(request.getParameter("action")).thenReturn("delete");
        when(request.getParameter("applicationId")).thenReturn("abc");

        servlet.doPost(request, response);

        verify(response).sendRedirect("applications");
    }
}
