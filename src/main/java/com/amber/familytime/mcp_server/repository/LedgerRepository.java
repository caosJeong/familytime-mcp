package com.amber.familytime.mcp_server.repository;
import com.amber.familytime.mcp_server.domain.Ledger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface LedgerRepository extends JpaRepository<Ledger, Long> {

    // 특정 가족의 특정 월 데이터를 가져오는 쿼리 (날짜 문자열 검색)
    @Query(value = "SELECT * FROM ledger WHERE family_id = :familyId AND to_char(transaction_date, 'YYYY-MM') = :yearMonth", nativeQuery = true)
    List<Ledger> findByFamilyIdAndMonth(@Param("familyId") Long familyId, @Param("yearMonth") String yearMonth);
}