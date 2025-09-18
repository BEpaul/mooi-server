package com.example.emotion_storage.report.controller;

import com.example.emotion_storage.global.api.ApiResponse;
import com.example.emotion_storage.global.api.SuccessMessage;
import com.example.emotion_storage.report.dto.response.DailyReportDetailResponse;
import com.example.emotion_storage.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "일일리포트", description = "일일리포트 관련 API")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/daily-report/{reportId}")
    @Operation(summary = "일일리포트 상세 조회", description = "특정 일일리포트의 상세 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<DailyReportDetailResponse>> getDailyReportDetail(
            @Parameter(description = "리포트 ID", required = true, example = "1")
            @PathVariable Long reportId) {
        
        log.info("일일리포트 상세 조회 API 호출 - reportId: {}", reportId);
        
        DailyReportDetailResponse response = reportService.getDailyReportDetail(reportId);
        
        return ResponseEntity.ok(
                ApiResponse.success(SuccessMessage.DAILY_REPORT_DETAIL_GET_SUCCESS.getMessage(), response)
        );
    }
}
