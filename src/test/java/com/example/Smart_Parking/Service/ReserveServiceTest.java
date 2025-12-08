package com.example.Smart_Parking.Service;

import com.example.Smart_Parking.Model.*;
import com.example.Smart_Parking.Repository.ReserveRepository;
import com.example.Smart_Parking.Repository.SlotRepository;
import com.example.Smart_Parking.Repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReserveServiceTest {

    @Mock
    private ReserveRepository reserveRepo;

    @Mock
    private SlotRepository slotRepo;

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private ReserveService reserveService;

    @Test
    void isSlotAvailable_returnsTrueIfNoOverlap() {
        Slots slot = new Slots(1, "A1");

        when(slotRepo.findById(1)).thenReturn(Optional.of(slot));
        when(reserveRepo.existsBySlotAndDateTimeOverlap(
                slot, LocalDate.now(), LocalTime.of(9,0), LocalTime.of(10,0)))
                .thenReturn(false);

        boolean result = reserveService.isSlotAvailable(
                1, LocalDate.now(), LocalTime.of(9,0), LocalTime.of(10,0));

        assertTrue(result);
    }

    @Test
    void reserveSlot_returnsNullIfUserNotFound() {
        when(userRepo.findById(1L)).thenReturn(Optional.empty());

        Long result = reserveService.reserveSlot(
                1L, 1, LocalDate.now(),
                LocalTime.of(10,0), 2, "GJ01AB1234"
        );

        assertNull(result);
    }
}