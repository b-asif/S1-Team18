package com.myapp.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.myapp.model.Interview;

class InterviewDAOIT extends BaseDaoIntegrationTest {

    private final InterviewDAO dao = new InterviewDAO();

    @Test
    void addInterview_allowsNullEndTimeAndReturnsInListing() {
        boolean created = dao.addInterview(
                1,
                "Platform Engineer",
                "Engineering",
                "Jordan Smith",
                "Onsite",
                Date.valueOf("2026-05-20"),
                Time.valueOf("09:00:00"),
                null,
                "HQ",
                "Panel interview"
        );

        List<Interview> interviews = dao.getInterviewsByUser(1);

        assertTrue(created);
        assertEquals(2, interviews.size());
        assertNull(interviews.get(0).getEndTime());
    }

    @Test
    void deleteInterview_requiresMatchingUserId() {
        Interview existing = dao.getInterviewsByUser(1).get(0);

        boolean wrongUserDelete = dao.deleteInterview(existing.getInterviewId(), 2);
        boolean rightUserDelete = dao.deleteInterview(existing.getInterviewId(), 1);

        assertFalse(wrongUserDelete);
        assertTrue(rightUserDelete);
        assertEquals(0, dao.getInterviewsByUser(1).size());
    }
}
