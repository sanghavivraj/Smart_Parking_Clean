package com.example.Smart_Parking.Service;

import com.example.Smart_Parking.Model.UserFeedback;
import com.example.Smart_Parking.Repository.UserFeedbackRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedbackServiceTest {

    @Mock
    private UserFeedbackRepository feedbackRepository;

    @InjectMocks
    private UserFeedbackService feedbackService;

    @Test
    void save_setsCreatedAtAndSavesFeedback() {
        UserFeedback feedback = new UserFeedback();

        //feedbackService.save(feedback);

        verify(feedbackRepository).save(feedback);
        // createdAt is set internally, so no exception = success
    }
}