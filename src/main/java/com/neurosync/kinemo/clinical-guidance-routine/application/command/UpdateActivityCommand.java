package com.neurosync.kinemo.clinicalguidanceroutine.application.command;
public record UpdateActivityCommand(java.util.UUID routineId, java.util.UUID activityId, String title, int position, int durationMinutes) {}
