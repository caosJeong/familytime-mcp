package com.amber.familytime.mcp_server;

import com.amber.familytime.mcp_server.domain.Ledger;
import com.amber.familytime.mcp_server.repository.LedgerRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class FamilyTools {

    // 1. Repository 주입
    private final LedgerRepository ledgerRepository;

    public FamilyTools(LedgerRepository ledgerRepository) {
        this.ledgerRepository = ledgerRepository;
    }

    public record LedgerRequest(Long familyId, String yearMonth) {}

    @Bean
    @Description("특정 가족의 월별 지출 내역을 조회합니다. yearMonth는 'YYYY-MM' 형식입니다.")
    public Function<LedgerRequest, String> getFamilyLedgerSummary() {
        return request -> {
            // 2. 진짜 DB 조회
            List<Ledger> ledgers = ledgerRepository.findByFamilyIdAndMonth(request.familyId(), request.yearMonth());

            if (ledgers.isEmpty()) {
                return request.yearMonth() + "에 해당하는 지출 내역이 없습니다.";
            }

            // 3. 데이터를 AI가 읽기 좋은 문자열로 변환
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("[%s 지출 목록]\n", request.yearMonth()));
            
            long total = 0;
            for (Ledger l : ledgers) {
                sb.append(String.format("- %s (%s): %d원\n", l.getTitle(), l.getCategory(), l.getAmount()));
                total += l.getAmount();
            }
            sb.append("총 합계: ").append(total).append("원");

            return sb.toString();
        };
    }
}