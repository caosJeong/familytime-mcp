package com.amber.familytime.mcp_server.domain;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ledger")
public class Ledger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "family_id")
    private Long familyId;

    private Integer amount;

    private String category;

    private String title;

    @Column(name = "transaction_date")
    // 2. 이 부분을 LocalDate로 바꿉니다!
    private LocalDate transactionDate;

    public Integer getAmount() { return amount; }
    public String getCategory() { return category; }
    public String getTitle() { return title; }
}
