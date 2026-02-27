// src/main/java/com/amber/familytime/mcp_server/repository/LedgerRepository.java
package com.amber.familytime.mcp_server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.amber.familytime.mcp_server.domain.Ledger;

public interface LedgerRepository extends JpaRepository<Ledger, Long> {

    // 특정 가족의 특정 월 데이터를 가져오는 쿼리 (기존 유지)
    @Query(value = "SELECT * FROM ledger WHERE family_id = :familyId AND to_char(transaction_date, 'YYYY-MM') = :yearMonth", nativeQuery = true)
    List<Ledger> findByFamilyIdAndMonth(@Param("familyId") Long familyId, @Param("yearMonth") String yearMonth);

    // 💡 StartingWith 대신 nativeQuery를 사용하여 연월을 비교합니다.
    @Query(value = "SELECT * FROM ledger WHERE to_char(transaction_date, 'YYYY-MM') = :yearMonth ORDER BY transaction_date DESC", nativeQuery = true)
    List<Ledger> findAllByMonthOrderByTransactionDateDesc(@Param("yearMonth") String yearMonth);
}
