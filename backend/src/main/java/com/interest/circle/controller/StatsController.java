package com.interest.circle.controller;

import com.interest.circle.dto.ApiResponse;
import com.interest.circle.service.StatsService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/user/{id}")
    public ApiResponse<Map<String, Object>> getUserStats(@PathVariable Long id) {
        return ApiResponse.success(statsService.getUserStats(id));
    }

    @GetMapping("/admin/overview")
    public ApiResponse<Map<String, Object>> getAdminOverview() {
        return ApiResponse.success(statsService.getAdminOverview());
    }

    @GetMapping("/admin/trends")
    public ApiResponse<Map<String, Object>> getAdminTrends() {
        return ApiResponse.success(statsService.getAdminTrends());
    }
}
