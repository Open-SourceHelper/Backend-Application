package com.neurosync.kinemo.clinicalguidanceroutine.application.command;
public record AddActivityCommand(java.util.UUID routineId, String title, int position, int durationMinutes) {}
