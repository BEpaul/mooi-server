package com.example.emotion_storage.report.repository;

import com.example.emotion_storage.report.domain.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    
    List<Keyword> findByReportId(Long reportId);
}
