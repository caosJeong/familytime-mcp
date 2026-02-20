package com.amber.familytime.mcp_server;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AiAnalysisController {

    private final ChatClient chatClient;

    // 제미나이와 통신할 ChatClient 주입
    public AiAnalysisController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    // 브라우저에서 호출한 주소와 매핑
    @GetMapping("/api/ai/analyze")
    public String analyzeFamilyPatterns(@RequestParam Long familyId, @RequestParam String yearMonth) {
        
        // 1. AI에게 부여할 역할(프롬프트) 작성
        String systemPrompt = """
            당신은 우리 가족의 소비와 생활 습관을 분석해 주는 다정한 AI 조언자입니다.
            제공된 도구를 사용하여 가족의 가계부 데이터를 조회하고,
            지출 패턴 분석과 함께 예산을 절약할 수 있는 실질적인 팁 3가지를 친근한 말투로 제안해 주세요.
            """;

        String userMessage = "가족 ID " + familyId + "의 " + yearMonth + " 소비 패턴을 분석해 줘.";

        // 2. 제미나이 호출 및 결과 반환
        return chatClient.prompt()
                .system(systemPrompt)
                .user(userMessage)
                // 3. FamilyTools에 만든 @Bean 도구 이름을 제미나이에게 쥐여줍니다.
                .functions("getFamilyLedgerSummary")
                .call()
                .content();
    }
}