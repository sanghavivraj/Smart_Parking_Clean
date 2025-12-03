package com.example.Smart_Parking.Controller;

import com.example.Smart_Parking.DTO.SlotDTO;
import com.example.Smart_Parking.Service.SlotService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SlotControllerTest {

    @Test
    void viewSlots_whenNoParams_callsGetAllSlots() {
        SlotService service = mock(SlotService.class);
        Model model = mock(Model.class);

        when(service.getAllSlots()).thenReturn(List.of());

        SlotController controller = new SlotController(service);
        String view = controller.viewSlots(null, null, null, model);

        assertEquals("Slots", view);
        verify(service).getAllSlots();
    }

    @Test
    void viewSlots_whenParamsProvided_callsGetAvailableSlots() {
        SlotService service = mock(SlotService.class);
        Model model = mock(Model.class);

        LocalDate date = LocalDate.of(2025, 12, 1);
        LocalTime start = LocalTime.of(10, 0);

        when(service.getAvailableSlots(eq(date), eq(start), any())).thenReturn(List.of());

        SlotController controller = new SlotController(service);
        controller.viewSlots(date, start, 2, model);

        verify(service).getAvailableSlots(eq(date), eq(start), eq(start.plusHours(2)));
    }
}
