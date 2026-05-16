package com.myapp.dao;

import org.junit.jupiter.api.BeforeEach;

import com.myapp.util.H2TestSupport;

abstract class BaseDaoIntegrationTest {
    @BeforeEach
    void resetDatabase() throws Exception {
        H2TestSupport.resetDatabase();
    }
}
