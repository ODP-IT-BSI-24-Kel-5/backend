package id.co.bankbsi.e_walled.repositories;

import id.co.bankbsi.e_walled.models.Transactions;
import id.co.bankbsi.e_walled.models.Users;
import id.co.bankbsi.e_walled.models.Wallets;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

@Repository
public class DashboardRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Object[]> getIncomeExpenseStatistic(
            Users user,
            Wallets wallet,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
        Root<Transactions> root = query.from(Transactions.class);


        Join<Transactions, Wallets> senderJoin = root.join("wallet", JoinType.LEFT);

        Join<Wallets, Users> senderUserJoin = senderJoin.join("user", JoinType.LEFT);

        Expression<String> direction = cb.<String>selectCase()
                .when(cb.and(cb.equal(senderUserJoin, user), cb.and(cb.isFalse(root.get("isDebit")), cb.isFalse(root.get("isInternal")))), "INCOME")
                .when(cb.and(cb.equal(senderUserJoin, user), cb.and(cb.isTrue(root.get("isDebit")), cb.isFalse(root.get("isInternal")))), "EXPENSE")
                .otherwise(cb.literal("INTERNAL"));


        Expression<Long> totalAmount = cb.sum(root.get("amount"));

        query.multiselect(direction, totalAmount);

        List<Predicate> predicates = new ArrayList<>();

        predicates.add(
                cb.equal(senderUserJoin, user)
        );

        if (wallet != null) {
            predicates.add(cb.equal(senderJoin, wallet));
        }

        if (startDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), startDate));
        }

        if (endDate != null) {
            predicates.add(cb.lessThan(root.get("createdAt"), endDate.plusDays(1).toLocalDate().atStartOfDay()));
        }

        query.where(cb.and(predicates.toArray(new Predicate[0])));
        query.groupBy(direction);

        return entityManager.createQuery(query).getResultList();
    }


    public List<Object[]> getTransactionGrowth(UUID userId, String truncUnit, LocalDateTime startDate, LocalDateTime endDate) {
        String sql = """
                SELECT t.created_at, (CASE WHEN t.is_internal = false THEN t.amount ELSE 0 END) as amount, t.is_debit
                FROM transactions t
                LEFT JOIN wallets sn ON sn.id = t.wallet_number_id
                WHERE (sn.user_id = :userId)
                AND t.created_at BETWEEN :startDate AND :endDate
                """;

        Query query = entityManager.createNativeQuery(sql)
                .setParameter("userId", userId)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate);

        // Retrieve raw transaction data
        return query.getResultList();
    }

    public List<Object[]> getWalletTransactionGrowth(UUID walletId, UUID userId, String truncUnit, LocalDateTime startDate, LocalDateTime endDate) {
        String sql = """
                SELECT t.created_at, t.amount, t.is_debit
                FROM transactions t
                LEFT JOIN wallets sn ON sn.id = t.wallet_number_id
                WHERE (sn.user_id = :userId)
                AND (sn.id = :walletId)
                AND t.created_at BETWEEN :startDate AND :endDate
                """;

        Query query = entityManager.createNativeQuery(sql)
                .setParameter("walletId", walletId)
                .setParameter("userId", userId)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate);

        // Retrieve raw transaction data
        return query.getResultList();
    }
}
