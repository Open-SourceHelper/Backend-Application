package com.neurosync.kinemo.crisisalert.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProfessionalAlertReviewRepository extends JpaRepository<ProfessionalAlertReviewEntity, String> {
    List<ProfessionalAlertReviewEntity> findByProfessionalId(String professionalId);
}
