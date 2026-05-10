package com.openwebinars.dto;

public record DashboardDto(

        long totalTasks,
        long completedTasks,
        long pendingTasks,
        long importantTasks,
        long expiredTasks

) {
}