package id.co.bankbsi.e_walled.controllers;

import id.co.bankbsi.e_walled.dto.request.TransactionStatsRequest;
import id.co.bankbsi.e_walled.dto.response.Response;
import id.co.bankbsi.e_walled.models.PeriodType;
import id.co.bankbsi.e_walled.models.Users;
import id.co.bankbsi.e_walled.services.DashboardService;
import id.co.bankbsi.e_walled.services.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;


@RestController
@RequestMapping("/api/v1/users/dashboard/chart")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {

    private final DashboardService transactionStatService;
    private final UserService userService;

    @PostMapping("/pie")
    public ResponseEntity<Response> getPieChart(@AuthenticationPrincipal Users users, @RequestBody TransactionStatsRequest.PieChart request) {
        return ResponseEntity.ok(transactionStatService.getPieChart(users, request));
    }


    @GetMapping("/balance-growth")
    public ResponseEntity<Response> getBalanceGrowth(
            @AuthenticationPrincipal Users user,
            @RequestParam PeriodType period,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) { // You can inject this however you handle auth
        var data = transactionStatService.getBalanceGrowthByPeriod(user, period, startDate, endDate);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/total-trans")
    public ResponseEntity<Response> getTotalBalance(
            @AuthenticationPrincipal Users user
    ) { // You can inject this however you handle auth
        var data = transactionStatService.getTotalBalance(user);
        return ResponseEntity.ok(data);
    }
}
