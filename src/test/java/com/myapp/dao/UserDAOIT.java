package com.myapp.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.myapp.model.User;

class UserDAOIT extends BaseDaoIntegrationTest {

    private final UserDAO dao = new UserDAO();

    @Test
    void getAllUsers_returnsSeededUsersWithUsernames() {
        List<User> users = dao.getAllUsers();

        assertEquals(2, users.size());
        assertNotNull(users.get(0).getUserName());
    }

    @Test
    void updateInfo_returnsExistsWhenEmailBelongsToAnotherUser() {
        String result = dao.updateInfo(
                1,
                "Alice",
                "Johnson",
                "brian@example.com",
                "alicej-updated"
        );

        assertEquals("exists", result);
    }

    @Test
    void passwordResetTokenLifecycle_storeResolveConsume() {
        String tokenHash = "hash-token-123";
        Timestamp expiresAt = Timestamp.from(Instant.now().plus(15, ChronoUnit.MINUTES));

        boolean stored = dao.storePasswordResetToken(1, tokenHash, expiresAt);
        int resolvedUserId = dao.getUserIdByValidResetTokenHash(tokenHash);
        boolean consumed = dao.consumePasswordResetToken(tokenHash);
        int afterConsumeUserId = dao.getUserIdByValidResetTokenHash(tokenHash);

        assertTrue(stored);
        assertEquals(1, resolvedUserId);
        assertTrue(consumed);
        assertEquals(-1, afterConsumeUserId);
    }

    @Test
    void getUserById_returnsNullForMissingUser() {
        User missing = dao.getUserById(9999);
        assertNull(missing);
    }
}
