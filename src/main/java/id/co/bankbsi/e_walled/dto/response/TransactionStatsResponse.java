package id.co.bankbsi.e_walled.dto.response;

import id.co.bankbsi.e_walled.models.Users;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@Data
public class TransactionStatsResponse extends Response {
    private ChartStats stats;

    public TransactionStatsResponse(String status, String message, HttpStatus statusCode, List<String> labels, List<ChartData> datasets) {
        super(status, message, statusCode);
        this.stats = new ChartStats(labels, datasets);
    }

    public static TransactionStatsResponse success(List<String> labels, List<ChartData> datasets) {
        return new TransactionStatsResponse("success", "User retrieved successfully!", HttpStatus.OK, labels, datasets);
    }

    @AllArgsConstructor
    @Setter
    @Getter
    public static class ChartStats {
        private List<String> labels;
        private List<ChartData> datasets;
    }

    @AllArgsConstructor
    @Setter
    @Getter
    public static class ChartData {
        private String label;
        private List<Long> data;
        private String borderColor;
        private String backgroundColor;
        private Float tension = 0.5F;
        private boolean fill = true;
    }
    @Setter
    @Getter
    @Accessors(chain = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TotalBalance extends Response {
        private Long totalBalance;

        public TotalBalance(String status, String message, HttpStatus statusCode, Long totalBalance) {
            super(status, message, statusCode);
            this.totalBalance = totalBalance;
        }

        public static TotalBalance success(Long totalBalance) {
            return new TotalBalance("success", "User retrieved successfully!", HttpStatus.OK, totalBalance);
        }
    }
}