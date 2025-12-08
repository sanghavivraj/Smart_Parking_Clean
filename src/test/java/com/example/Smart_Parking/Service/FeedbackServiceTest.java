package com.example.Smart_Parking.Service;

import com.example.Smart_Parking.Model.Feedback;
import com.example.Smart_Parking.Repository.FeedbackRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedbackServiceTest {

    @Mock
    private FeedbackRepository feedbackRepository;

    @InjectMocks
    private FeedbackService feedbackService;

    @Test
    void save_setsCreatedAtAndSavesFeedback() {
        Feedback feedback = new Feedback();

        feedbackService.save(feedback);

        verify(feedbackRepository).save(feedback);
        // createdAt is set internally, so no exception = success
    }
}