package com.example.Smart_Parking.Repository;

import com.example.Smart_Parking.Model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
}
