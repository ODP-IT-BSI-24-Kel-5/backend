package id.co.bankbsi.e_walled.models;

import lombok.Data;
import lombok.Getter;

@Getter
public enum TransactionTypes {
    DEPOSIT("deposit", "Deposit"),
    WITHDRAWAL("withdrawal", "Withdrawal"),
    TRANSFER("transfer", "Transfer"),
    PAYMENT("payment", "Payment"),
    TOPUP("topup", "Top Up"),
    REFUND("refund", "refund");

    private final String value;
    private final String display;

    TransactionTypes(String value, String display) {
        this.value = value;
        this.display = display;
    }

    public static TransactionTypes fromValue(String value) {
        for (TransactionTypes type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown transaction type: " + value);
    }
    public static TransactionTypes fromDisplay(String display) {
        for (TransactionTypes type : values()) {
            if (type.display.equalsIgnoreCase(display)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown transaction display: " + display);
    }
}