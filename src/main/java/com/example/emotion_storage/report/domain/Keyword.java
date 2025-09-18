package com.example.emotion_storage.report.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table(name = "keywords")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Keyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "keyword_id")
    private Long id;

    @Column(nullable = false)
    private String keyword;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;

    public void setReport(Report report) {
        this.report = report;
    }
}
