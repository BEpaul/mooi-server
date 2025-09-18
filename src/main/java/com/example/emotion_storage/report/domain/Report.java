package com.example.emotion_storage.report.domain;

import com.example.emotion_storage.global.entity.BaseTimeEntity;
import com.example.emotion_storage.timecapsule.domain.TimeCapsule;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "reports")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Report extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long id;

    @Column(nullable = false)
    private LocalDate historyDate;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String todaySummary;

    @Column(nullable = false)
    private Integer stressIndex;

    @Column(nullable = false)
    private Integer happinessIndex;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String emotionSummary;

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Keyword> keywords = new ArrayList<>();

    @Column(nullable = false)
    private Boolean isOpened;

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<EmotionVariation> emotionVariations = new ArrayList<>();

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TimeCapsule> timeCapsules = new ArrayList<>();

    public void addEmotionVariation(EmotionVariation emotionVariation) {
        this.emotionVariations.add(emotionVariation);
        emotionVariation.setReport(this);
    }

    public void removeEmotionVariation(EmotionVariation emotionVariation) {
        this.emotionVariations.remove(emotionVariation);
        emotionVariation.setReport(null);
    }

    public void addTimeCapsule(TimeCapsule timeCapsule) {
        this.timeCapsules.add(timeCapsule);
        timeCapsule.setReport(this);
    }

    public void removeTimeCapsule(TimeCapsule timeCapsule) {
        this.timeCapsules.remove(timeCapsule);
        timeCapsule.setReport(null);
    }

    public void addKeyword(Keyword keyword) {
        this.keywords.add(keyword);
        keyword.setReport(this);
    }
}
