package id.co.bankbsi.e_walled.models;


import jakarta.persistence.*;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table
@EntityListeners(org.springframework.data.jpa.domain.support.AuditingEntityListener.class)
public class Transactions extends Timestamp {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "transaction_number", unique = true, nullable = false, updatable = false, columnDefinition = "VARCHAR(36)")
    private String transactionNumber;

    @Column(name = "amount", nullable = false, updatable = false)
    private Long amount;

    @Enumerated(EnumType.STRING)
    private TransactionTypes type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_number_id")
    private Wallets wallet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "associate_number_id")
    private Wallets associateWallet;

    private Long walletBalanceLeft;

    private String notes;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_category_id")
    private TransactionCategories category;  // Reference to the Category entity


    private boolean isDebit;
    private boolean isInternal;
    private String via;  // Reference to the Category entity
}

