package com.example.emotion_storage.report.service;

import com.example.emotion_storage.global.exception.BaseException;
import com.example.emotion_storage.global.exception.ErrorCode;
import com.example.emotion_storage.report.domain.Report;
import com.example.emotion_storage.report.dto.response.DailyReportDetailResponse;
import com.example.emotion_storage.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final ReportRepository reportRepository;

    public DailyReportDetailResponse getDailyReportDetail(Long reportId) {
        log.info("일일리포트 상세 조회 요청 - reportId: {}", reportId);
        
        Report report = findReportWithDetails(reportId);
        
        DailyReportDetailResponse response = DailyReportDetailResponse.from(report);
        
        log.info("일일리포트 상세 조회 완료 - reportId: {}", reportId);
        return response;
    }

    private Report findReportWithDetails(Long reportId) {
        return reportRepository.findById(reportId)
                .orElseThrow(() -> {
                    log.warn("존재하지 않는 리포트 조회 시도 - reportId: {}", reportId);
                    return new BaseException(ErrorCode.REPORT_NOT_FOUND);
                });
    }
}
