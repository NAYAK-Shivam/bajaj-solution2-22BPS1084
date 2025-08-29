package com.example.webhookapp.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "solutions")
public class Solution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String regNo;

    @Lob
    @Column(length = 65535)
    private String finalQuery;

    private LocalDateTime createdAt;

    public Solution() {}
    // getters/setters
    public Long getId(){return id;}
    public void setId(Long id){this.id = id;}
    public String getRegNo(){return regNo;}
    public void setRegNo(String regNo){this.regNo = regNo;}
    public String getFinalQuery(){return finalQuery;}
    public void setFinalQuery(String finalQuery){this.finalQuery = finalQuery;}
    public LocalDateTime getCreatedAt(){return createdAt;}
    public void setCreatedAt(LocalDateTime createdAt){this.createdAt = createdAt;}
}
