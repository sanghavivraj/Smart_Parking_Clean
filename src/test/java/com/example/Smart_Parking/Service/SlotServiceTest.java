package com.example.Smart_Parking.Service;

import com.example.Smart_Parking.DTO.SlotDTO;
import com.example.Smart_Parking.Model.Slots;
import com.example.Smart_Parking.Repository.ReserveRepository;
import com.example.Smart_Parking.Repository.SlotRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SlotServiceTest {

    @Mock
    private SlotRepository slotRepository;

    @Mock
    private ReserveRepository reserveRepository;

    @InjectMocks
    private SlotService slotService;

    @Test
    void getAllSlots_returnsSlotDTOListWithAvailabilityTrue() {
        List<Slots> slots = List.of(
                new Slots(1, "A1"),
                new Slots(2, "A2")
        );

        when(slotRepository.findAll()).thenReturn(slots);

        List<SlotDTO> result = slotService.getAllSlots();

        assertEquals(2, result.size());
        assertEquals("A1", result.get(0).getSlotLabel());
        assertTrue(result.get(0).isAvailable());
        verify(slotRepository).findAll();
    }

    @Test
    void getAvailableSlots_marksUnavailableIfReservationExists() {
        LocalDate date = LocalDate.now();
        LocalTime start = LocalTime.of(10, 0);
        LocalTime end = LocalTime.of(12, 0);

        Slots slot = new Slots(1, "A1");

        when(slotRepository.findAll()).thenReturn(List.of(slot));
        when(reserveRepository.existsBySlotAndDateTimeOverlap(slot, date, start, end))
                .thenReturn(true);

        List<SlotDTO> result = slotService.getAvailableSlots(date, start, end);

        assertEquals(1, result.size());
        assertFalse(result.get(0).isAvailable(), "Slot should be unavailable due to reservation overlap");
        verify(reserveRepository).existsBySlotAndDateTimeOverlap(slot, date, start, end);
    }

    @Test
    void getAvailableSlots_marksAvailableIfNoReservation() {
        LocalDate date = LocalDate.now();
        LocalTime start = LocalTime.of(9, 0);
        LocalTime end = LocalTime.of(10, 0);

        Slots slot = new Slots(2, "B1");

        when(slotRepository.findAll()).thenReturn(List.of(slot));
        when(reserveRepository.existsBySlotAndDateTimeOverlap(slot, date, start, end))
                .thenReturn(false);

        List<SlotDTO> result = slotService.getAvailableSlots(date, start, end);

        assertTrue(result.get(0).isAvailable(), "Slot should be available");
    }
}
