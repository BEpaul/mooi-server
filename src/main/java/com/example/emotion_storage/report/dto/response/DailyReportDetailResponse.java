package com.example.emotion_storage.report.dto.response;

import com.example.emotion_storage.report.domain.EmotionVariation;
import com.example.emotion_storage.report.domain.Keyword;
import com.example.emotion_storage.report.domain.Report;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
public class DailyReportDetailResponse {

    private Long id;
    private String createdAt;
    private String updatedAt;
    private List<String> summaries;
    private List<String> keywords;
    private Integer stressIndex;
    private Integer happinessIndex;
    private String emotionSummary;
    private List<EmotionChangeDto> emotionChanges;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class EmotionChangeDto {
        private String time;
        private String label;
    }

    public static DailyReportDetailResponse from(Report report) {
        return DailyReportDetailResponse.builder()
                .id(report.getId())
                .createdAt(formatDateTime(report.getCreatedAt()))
                .updatedAt(formatDateTime(report.getUpdatedAt()))
                .summaries(List.of(report.getTodaySummary()))
                .keywords(report.getKeywords().stream()
                        .map(Keyword::getKeyword)
                        .collect(Collectors.toList()))
                .stressIndex(report.getStressIndex())
                .happinessIndex(report.getHappinessIndex())
                .emotionSummary(report.getEmotionSummary())
                .emotionChanges(report.getEmotionVariations().stream()
                        .map(emotionVariation -> EmotionChangeDto.builder()
                                .time(formatTime(emotionVariation.getTime()))
                                .label(emotionVariation.getLabel())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    private static String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    private static String formatTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
    }
}
