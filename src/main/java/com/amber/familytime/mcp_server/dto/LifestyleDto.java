package com.amber.familytime.mcp_server.dto;
import java.util.List;

public class LifestyleDto {

    // 1. Flutter -> Spring 서버로 보낼 요청 데이터
    public record FamilyDataRequest(
        List<ScheduleItem> schedules,
        List<TodoItem> todos,
        List<LedgerItem> ledgers
    ) {}

    public record ScheduleItem(String date, String title, String category) {}
    public record TodoItem(String date, String task, boolean isCompleted) {}
    public record LedgerItem(String date, String description, int amount) {}

    // 2. Spring 서버(AI) -> Flutter로 내려줄 응답 데이터 (분석 리포트)
    public record PatternAnalysisResponse(
            HealthInsight healthInsight,
            List<String> repetitiveEvents,
            List<String> lifestyleHabits,
            String summaryAdvice
    ) {}

    public record HealthInsight(
            int hospitalVisitCount,
            String observation
    ) {}
}
