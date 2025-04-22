package id.co.bankbsi.e_walled.repositories;

import id.co.bankbsi.e_walled.dto.response.TransactionResponse;
import id.co.bankbsi.e_walled.models.Transactions;
import id.co.bankbsi.e_walled.models.Users;
import id.co.bankbsi.e_walled.models.Wallets;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public interface TransactionRepository extends CrudRepository<Transactions, Long>, JpaSpecificationExecutor<Transactions> {
    @Override
    List<Transactions> findAll(Specification<Transactions> specification);


    @Query("SELECT t.transactionNumber FROM Transactions t WHERE t.transactionNumber LIKE CONCAT('%', :date, '%') ORDER BY t.transactionNumber DESC LIMIT 1")
    String findLatestTransactionNumberForDate(@Param("date") String date);


    List<Transactions> findByWalletIdOrAssociateWalletId(UUID wallet, UUID associateWallet);

    @Query("""
                SELECT t FROM Transactions t
                WHERE (:wallet IS NULL OR t.wallet = :wallet)
                  AND (t.wallet.user = :user OR t.associateWallet.user = :user)
                  AND EXTRACT(MONTH FROM t.createdAt) = :month
                  AND EXTRACT(YEAR FROM t.createdAt) = :year
                ORDER BY t.createdAt ASC
            """)
    List<Transactions> findByWalletInMonth(
            @Param("user") Users user,
            @Param("wallet") Wallets wallet,
            @Param("month") Integer month,
            @Param("year") Integer year
    );


    @Query("""
                SELECT t FROM Transactions t
                WHERE (t.wallet = :wallet OR t.associateWallet = :wallet)
                  AND EXTRACT(MONTH FROM t.createdAt) = :month
                  AND EXTRACT(YEAR FROM t.createdAt) = :year
                ORDER BY t.createdAt ASC
            """)
    List<Transactions> sumAmount(
            @Param("wallet") Wallets wallet,
            @Param("month") Integer month,
            @Param("year") Integer year
    );

    @Query(value = """
                SELECT t.type, SUM(t.amount)
                FROM Transactions t
                WHERE
                 (t.wallet = :walletNumber OR t.associateWallet = :walletNumber)
                 AND t.createdAt BETWEEN :startDate AND :endDate
                GROUP BY t.type
            """)
    List<Object[]> sumAmountsByTypeAndDate(
            @Param("walletNumber") String walletNumber,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

//    @Query("""
//                SELECT SUM(CASE WHEN isDebit = true THEN amount ELSE -amount END)
//                FROM Transactions t
//                WHERE
//                    (:walletNumber IS NULL AND t.wallet = :walletNumber)
//                AND t.createdAt >= :startDate
//            """)
//    Long sumAmountsFromStartDate(
//            @Param("walletNumber") Wallets walletNumber,
//            @Param("startDate") LocalDateTime startDate
//    );

    @Query("SELECT "
            + "date_trunc(:period, t.createdAt) AS periodLabel, "
            + "SUM(CASE WHEN t.wallet.id = :walletId THEN t.amount "
            + "ELSE t.amount * -1 END) AS balanceGrowth "
            + "FROM Transactions t "
            + "WHERE (t.wallet.user.id = :userId OR t.associateWallet.user.id = :userId) "
            + "AND t.createdAt >= :startDate "
            + "AND t.createdAt <= :endDate "
            + "GROUP BY date_trunc(:period, t.createdAt) "
            + "ORDER BY periodLabel")
    public Map<String, Long> getWalletBalanceGrowth(
            @Param("walletId") UUID walletId,
            @Param("period") String period,
            @Param("userId") UUID userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
