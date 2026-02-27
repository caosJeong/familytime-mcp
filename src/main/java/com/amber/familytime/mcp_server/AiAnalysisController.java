// 경로: src/main/java/com/amber/familytime/mcp_server/AiAnalysisController.java
package com.amber.familytime.mcp_server;

import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amber.familytime.mcp_server.domain.Ledger;
import com.amber.familytime.mcp_server.dto.LifestyleDto.FamilyDataRequest;
import com.amber.familytime.mcp_server.dto.LifestyleDto.PatternAnalysisResponse;
import com.amber.familytime.mcp_server.repository.LedgerRepository;

@RestController
@RequestMapping("/api/ai")
public class AiAnalysisController {

    private final ChatClient chatClient;
    private final LedgerRepository ledgerRepository;

    public AiAnalysisController(ChatClient.Builder chatClientBuilder, LedgerRepository ledgerRepository) {
        this.chatClient = chatClientBuilder.build();
        this.ledgerRepository = ledgerRepository;
    }

    // ==========================================
    // 1. 기존 기능: 월별 가계부 요약 (GET 방식)
    // ==========================================
    @GetMapping("/insight")
    public AiInsight analyzeFamilyPatterns(@RequestParam int familyId, @RequestParam String yearMonth) {
        List<Ledger> ledgers = ledgerRepository.findAllByMonthOrderByTransactionDateDesc(yearMonth);
        var outputConverter = new BeanOutputConverter<>(AiInsight.class);

        String promptTemplate = """
                당신은 친절하고 똑똑한 가족 가계부 AI 비서입니다.
                다음은 이번 달 우리 가족의 지출 내역입니다:
                {ledgers}
                
                이 데이터를 분석해서 다음 항목들을 작성해주세요:
                1. summary: 이번 달 지출에 대한 총평 (다정한 말투)
                2. financeTips: 지출 패턴을 분석한 재정 조언 3가지 (배열)
                3. scheduleTips: 지출 내역을 통해 유추해본 가족 일정이나 이벤트 조언 2가지 (배열)
                4. encouragement: 오늘도 힘내는 가족을 위한 따뜻한 응원 한마디
                
                반드시 아래의 JSON 형식에 맞추어 답변해야 합니다.
                {format}
                """;

        String systemPrompt = promptTemplate
                .replace("{ledgers}", ledgers.toString())
                .replace("{format}", outputConverter.getFormat());

        String response = chatClient.prompt()
                .system(systemPrompt)
                .call()
                .content();

        return outputConverter.convert(response);
    }

    // ==========================================
    // 2. 신규 기능: 생활 습관, 가족 행사, 병원 방문 패턴 분석 (POST 방식)
    // ==========================================
    @PostMapping("/lifestyle-insight")
    public PatternAnalysisResponse analyzeLifestyle(@RequestBody FamilyDataRequest request) {
        var outputConverter = new BeanOutputConverter<>(PatternAnalysisResponse.class);

        String promptTemplate = """
                당신은 다정하고 통찰력 있는 가족 생활 패턴 분석 전문가입니다.
                다음은 우리 가족의 최근 일정, 할 일, 지출 내역 데이터입니다.
                
                [가족 데이터]
                - 일정: {schedules}
                - 할 일: {todos}
                - 지출 내역: {ledgers}
                
                이 데이터를 바탕으로 다음 항목을 세밀하게 분석해주세요:
                1. 건강 지표 (healthInsight): 병원, 약국, 진료 등 건강 관련 기록을 모두 찾아 방문 횟수(hospitalVisitCount)를 세고, 전반적인 건강 관리에 대한 특징이나 조언(observation)을 작성해주세요.
                2. 반복 행사 (repetitiveEvents): 가족 단위로 반복되거나 규칙적으로 보이는 행사, 모임을 찾아 배열로 나열해주세요.
                3. 생활 습관 (lifestyleHabits): 지출과 할 일을 통해 엿보이는 우리 가족만의 생활 패턴과 습관을 2~3개 추출해주세요.
                4. 요약 조언 (summaryAdvice): 가족의 행복과 건강을 위해 동기를 부여하는 따뜻한 조언 3줄을 작성해주세요.
                
                반드시 아래의 JSON 형식에 정확히 맞추어 답변해야 합니다.
                {format}
                """;

        String systemPrompt = promptTemplate
                .replace("{schedules}", request.schedules() != null ? request.schedules().toString() : "[]")
                .replace("{todos}", request.todos() != null ? request.todos().toString() : "[]")
                .replace("{ledgers}", request.ledgers() != null ? request.ledgers().toString() : "[]")
                .replace("{format}", outputConverter.getFormat());

        String response = chatClient.prompt()
                .system(systemPrompt)
                .call()
                .content();

        return outputConverter.convert(response);
    }
}

// 기존 AiInsight 레코드 (변경 없음)
record AiInsight(
        String summary,
        List<String> financeTips,
        List<String> scheduleTips,
        String encouragement
) {}
