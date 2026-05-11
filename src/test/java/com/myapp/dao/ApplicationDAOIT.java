package com.myapp.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.myapp.model.Application;

class ApplicationDAOIT extends BaseDaoIntegrationTest {

    private final ApplicationDAO dao = new ApplicationDAO();

    @Test
    void getApplicationsByUser_returnsScopedRows() {
        List<Application> user1Apps = dao.getApplicationsByUser(1);
        List<Application> user2Apps = dao.getApplicationsByUser(2);

        assertEquals(1, user1Apps.size());
        assertEquals(9, user2Apps.size());
        assertEquals("Engineering Manager", user1Apps.get(0).getJobTitle());
        assertEquals("Mobile Engineer (iOS)", user2Apps.get(0).getJobTitle());
    }

    @Test
    void addApplication_persistsAndListsForUser() {
        boolean created = dao.addApplication(
                1,
                "Security Engineer",
                "SafeTech",
                "Applied",
                Date.valueOf("2026-04-28")
        );

        List<Application> apps = dao.getApplicationsByUser(1);

        assertTrue(created);
        assertEquals(2, apps.size());
        assertNotNull(apps.get(0).getApplicationId());
    }

    @Test
    void deleteApplication_requiresMatchingUserId() {
        List<Application> user1Apps = dao.getApplicationsByUser(1);
        int appId = user1Apps.get(0).getApplicationId();

        boolean wrongUserDelete = dao.deleteApplication(appId, 2);
        boolean rightUserDelete = dao.deleteApplication(appId, 1);

        assertFalse(wrongUserDelete);
        assertTrue(rightUserDelete);
        assertEquals(0, dao.getApplicationsByUser(1).size());
    }
}
