package com.example.webhookapp.repo;

import com.example.webhookapp.entity.Solution;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolutionRepository extends JpaRepository<Solution, Long> {}
