package com.neurosync.kinemo.clinicalguidanceroutine.application.command;
public record ConfigureTimerCommand(java.util.UUID routineId, java.util.UUID activityId, int transitionMinutes, com.neurosync.kinemo.clinicalguidanceroutine.domain.model.AlertType alertType) {}
