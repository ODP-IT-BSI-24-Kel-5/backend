package id.co.bankbsi.e_walled.dto;

import id.co.bankbsi.e_walled.models.Transactions;
import id.co.bankbsi.e_walled.models.Wallets;
import jakarta.persistence.*;

import java.util.UUID;

public class TransactionDTO {
    private String date;
    private String description;
    private String type;
    private double amount;
    private double balanceAfter;
}
