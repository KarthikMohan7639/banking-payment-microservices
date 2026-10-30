package com.banking.gateway;

import com.banking.gateway.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ApiGatewayRoutingTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void testJwtUtilInApiGateway() {
        assertNotNull(jwtUtil);
    }
}
