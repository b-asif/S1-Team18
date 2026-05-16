package com.myapp.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CsrfUtilTest {

    @Mock
    private HttpSession session;

    @Mock
    private HttpServletRequest request;

    @Test
    void getOrCreateToken_createsAndReusesToken() {
        when(session.getAttribute("csrfToken"))
                .thenReturn(null)
                .thenReturn("existing-token");

        String created = CsrfUtil.getOrCreateToken(session);
        String reused = CsrfUtil.getOrCreateToken(session);

        assertNotNull(created);
        assertFalse(created.isBlank());
        assertEquals("existing-token", reused);
    }

    @Test
    void isValidToken_falseWhenSessionMissing() {
        when(request.getSession(false)).thenReturn(null);
        assertFalse(CsrfUtil.isValidToken(request));
    }

    @Test
    void isValidToken_falseWhenTokenMissingOrMismatch() {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("csrfToken")).thenReturn("expected-token");
        when(request.getParameter("csrfToken")).thenReturn(null);
        assertFalse(CsrfUtil.isValidToken(request));

        when(request.getParameter("csrfToken")).thenReturn("wrong-token");
        assertFalse(CsrfUtil.isValidToken(request));
    }

    @Test
    void isValidToken_trueWhenMatch() {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("csrfToken")).thenReturn("expected-token");
        when(request.getParameter("csrfToken")).thenReturn("expected-token");

        assertTrue(CsrfUtil.isValidToken(request));
    }
}
