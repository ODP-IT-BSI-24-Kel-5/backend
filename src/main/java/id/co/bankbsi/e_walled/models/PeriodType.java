package id.co.bankbsi.e_walled.models;

public enum PeriodType {
    HOURLY, WEEKLY, DAILY, MONTHLY, QUARTERLY, YEARLY;

    public String toTruncFormat() {
        return switch (this) {
            case HOURLY -> "hour";
            case WEEKLY -> "week";
            case DAILY -> "day";
            case MONTHLY -> "month";
            case QUARTERLY -> "quarter";
            case YEARLY -> "year";
        };
    }
}