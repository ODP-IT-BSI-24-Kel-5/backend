package id.co.bankbsi.e_walled.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

public class TransactionStatsRequest {
    @Data
    @AllArgsConstructor
    public static class PieChart {
        private String rangeType = ""; // "weekly", "monthly", "quarterly", "yearly"
    }
}
