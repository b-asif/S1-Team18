package com.myapp.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.myapp.model.Technical;

class TechnicalDAOIT extends BaseDaoIntegrationTest {

    private final TechnicalDAO dao = new TechnicalDAO();

    @Test
    void addAssessment_allowsNullDueDateAndReturnsInListing() {
        boolean created = dao.addAssessment(
                1,
                "Database Challenge",
                Date.valueOf("2026-05-18"),
                null,
                "Complete normalization exercise",
                "Assigned",
                "N/A"
        );

        List<Technical> assessments = dao.getAssessmentsByUser(1);

        assertTrue(created);
        assertEquals(2, assessments.size());
        assertNull(assessments.get(0).getDueDate());
    }

    @Test
    void deleteAssessment_requiresMatchingUserId() {
        Technical existing = dao.getAssessmentsByUser(1).get(0);

        boolean wrongUserDelete = dao.deleteAssessment(existing.getAssessmentId(), 2);
        boolean rightUserDelete = dao.deleteAssessment(existing.getAssessmentId(), 1);

        assertFalse(wrongUserDelete);
        assertTrue(rightUserDelete);
        assertEquals(0, dao.getAssessmentsByUser(1).size());
    }
}
