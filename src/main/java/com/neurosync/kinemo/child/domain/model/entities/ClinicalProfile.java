package com.neurosync.kinemo.child.domain.model.entities;

import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class ClinicalProfile {
    private Long id;
    private String specialNeeds;
    private String triggers;
    private String regulators;
    private LocalDateTime updateTime;

    public void updateProfile(String specialNeeds, String triggers, String regulators) {
        this.specialNeeds = specialNeeds;
        this.triggers = triggers;
        this.regulators = regulators;
        this.updateTime = LocalDateTime.now();
    }

    public static ClinicalProfile reconstitute(
            Long id,
            String specialNeeds,
            String triggers,
            String regulators,
            LocalDateTime updateTime
    ) {
        ClinicalProfile profile = new ClinicalProfile();
        profile.id = id;
        profile.specialNeeds = specialNeeds;
        profile.triggers = triggers;
        profile.regulators = regulators;
        profile.updateTime = updateTime;
        return profile;
    }
}
