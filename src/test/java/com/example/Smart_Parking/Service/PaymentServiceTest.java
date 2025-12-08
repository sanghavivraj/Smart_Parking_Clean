package com.example.Smart_Parking.Service;

import com.example.Smart_Parking.Model.*;
import com.example.Smart_Parking.Repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;


    @Test
    void updatePaymentStatus_returnsFalseIfPaymentNotFound() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.empty());

        boolean result = paymentService.updatePaymentStatus(
                1L, PaymentStatus.SUCCESS, null
        );

        assertFalse(result);
    }

    @Test
    void getPaymentsForUser_returnsPaymentList() {
        when(paymentRepository.findAllByUserUserId(1L))
                .thenReturn(List.of(new Payment()));

        List<Payment> result = paymentService.getPaymentsForUser(1L);

        assertEquals(1, result.size());
        verify(paymentRepository).findAllByUserUserId(1L);
    }
}