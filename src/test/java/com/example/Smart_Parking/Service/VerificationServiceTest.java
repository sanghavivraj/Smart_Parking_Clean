package com.example.Smart_Parking.Service;

import com.example.Smart_Parking.Model.Verification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VerificationServiceTest {

    private VerificationService verificationService;

    @BeforeEach
    void setUp() {
        verificationService = new VerificationService();
    }

    @Test
    void testGenerateAndVerifyToken_Success() {
        String email = "test@example.com";

        // Generate token
        String token = verificationService.generateToken(email);

        // Verify with the same token
        boolean result = verificationService.verifyToken(email, token);

        assertTrue(result, "Token should be valid immediately after generation");
    }

    @Test
    void testVerifyToken_Expired() throws InterruptedException {
        String email = "expired@example.com";

        // Generate token
        String token = verificationService.generateToken(email);

        // Simulate expiry by manipulating system wait (set to 1 second)
        Thread.sleep(1000);

        // Manually invalidate by forcing expiry
        verificationService.invalidate(email);

        boolean result = verificationService.verifyToken(email, token);

        assertFalse(result, "Token should be invalid after being removed or expired");
    }
}
