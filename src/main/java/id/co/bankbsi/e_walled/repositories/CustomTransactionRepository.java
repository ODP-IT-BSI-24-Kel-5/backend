package id.co.bankbsi.e_walled.repositories;

import id.co.bankbsi.e_walled.dto.request.TransactionFilterRequest;
import id.co.bankbsi.e_walled.dto.response.TransactionResponse;
import id.co.bankbsi.e_walled.models.TransactionCategories;
import id.co.bankbsi.e_walled.models.Transactions;
import id.co.bankbsi.e_walled.models.Users;
import id.co.bankbsi.e_walled.models.Wallets;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class CustomTransactionRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Page<TransactionResponse> findTransactionsWithSignedAmount(Users user, TransactionFilterRequest filterRequest, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // === ROOT ===
        CriteriaQuery<TransactionResponse> cq = cb.createQuery(TransactionResponse.class);
        Root<Transactions> transaction = cq.from(Transactions.class);

        // === JOINS ===
        Join<Transactions, Wallets> sender = transaction.join("wallet", JoinType.LEFT);
        Join<Wallets, Users> senderUser = sender.join("user", JoinType.LEFT);

        Join<Transactions, Wallets> receiver = transaction.join("associateWallet", JoinType.LEFT);
        Join<Wallets, Users> receiverUser = receiver.join("user", JoinType.LEFT);

        Join<Transactions, TransactionCategories> category = transaction.join("category", JoinType.LEFT);

        // === FILTERS ===
        List<Predicate> predicates = buildPredicates(cb, transaction, user, sender, receiver, senderUser, receiverUser, category, filterRequest);
        cq.where(cb.and(predicates.toArray(new Predicate[0])));

        // === SIGNED AMOUNT ===
        Expression<Object> signedAmount = cb.selectCase()
                .when(transaction.get("isDebit"), cb.prod(transaction.get("amount"), -1))
                .otherwise(transaction.get("amount"));

        // === SELECT FIELDS ===
        cq.select(cb.construct(TransactionResponse.class,
                transaction.get("id"),
                transaction.get("transactionNumber"),
                signedAmount.alias("amount"),
                transaction.get("createdAt"),
                sender.get("number").alias("wallet"),
                senderUser.get("fullName").alias("walletName"),
                receiver.get("number").alias("associateWallet"),
                receiverUser.get("fullName").alias("associateName"),
                transaction.get("type"),
                transaction.get("notes"),
                category.get("name").alias("category"),
                transaction.get("via")
        ));

        // === ORDER BY ===
        if (pageable.getSort().isSorted()) {
            List<Order> orders = new ArrayList<>();
            for (Sort.Order order : pageable.getSort()) {
                String property = order.getProperty();
                Expression<?> expression = switch (property) {
                    case "category", "categoryName" -> category.get("name");
                    default -> transaction.get(property);
                };

                orders.add(order.isAscending() ? cb.asc(expression) : cb.desc(expression));
            }
            cq.orderBy(orders);
        }


        // === PAGINATION ===
        TypedQuery<TransactionResponse> query = entityManager.createQuery(cq);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<TransactionResponse> results = query.getResultList();

        // === COUNT QUERY ===
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Transactions> countRoot = countQuery.from(Transactions.class);
        Join<Transactions, Wallets> countSender = countRoot.join("wallet", JoinType.LEFT);
        Join<Wallets, Users> countSenderUser = countSender.join("user", JoinType.LEFT);
        Join<Transactions, Wallets> countAcquirer = countRoot.join("associateWallet", JoinType.LEFT);
        Join<Wallets, Users> countAcquirerUser = countAcquirer.join("user", JoinType.LEFT);
        Join<Transactions, TransactionCategories> countCategory = countRoot.join("category", JoinType.LEFT);
        countRoot.join("category", JoinType.LEFT);

        List<Predicate> countPredicates = buildPredicates(cb, countRoot, user, countSender, countAcquirer, countSenderUser, countAcquirerUser, countCategory, filterRequest);
        countQuery.where(cb.and(countPredicates.toArray(new Predicate[0])));

        countQuery.select(cb.count(countRoot));
        Long count = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(results, pageable, count);
    }


    public Long sumAmountsFromStartDate(Wallets walletNumber, LocalDateTime startDate) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Transactions> root = cq.from(Transactions.class);

        // Case: isDebit ? amount : -amount
        Expression<Long> amountExpression = cb.<Long>selectCase()
                .when(cb.isTrue(root.get("isDebit")), root.get("amount"))
                .otherwise(cb.prod(root.get("amount"), -1L));

        // WHERE clause
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), startDate));

        if (walletNumber != null) {
            predicates.add(cb.equal(root.get("wallet"), walletNumber));
        }

        cq.select(cb.sum(amountExpression))
                .where(predicates.toArray(new Predicate[0]));

        TypedQuery<Long> query = entityManager.createQuery(cq);
        return Optional.ofNullable(query.getSingleResult()).orElse(0L);
    }


    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<Transactions> root,
                                            Users user,
                                            Join<Transactions, Wallets> sender,
                                            Join<Transactions, Wallets> receiver,
                                            Join<Wallets, Users> senderUser,
                                            Join<Wallets, Users> receiverUser,
                                            Join<Transactions, TransactionCategories> category,
                                            TransactionFilterRequest filterRequest) {
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.or(
                cb.equal(sender.get("user"), user),
                cb.equal(receiver.get("user"), user)
        ));

        if (filterRequest.getWalletNumber() != null) {
            predicates.add(cb.or(
                    cb.equal(sender.get("number"), filterRequest.getWalletNumber()),
                    cb.equal(receiver.get("number"), filterRequest.getWalletNumber())
            ));
        }

        if (filterRequest.getCategoryName() != null && !filterRequest.getCategoryName().isBlank()) {
            predicates.add(cb.like(cb.lower(category.get("name")), "%" + filterRequest.getCategoryName().toLowerCase() + "%"));
        }

        if (filterRequest.getTransactionNumber() != null) {
            predicates.add(cb.equal(root.get("transactionNumber"), filterRequest.getTransactionNumber()));
        }

        if (filterRequest.getType() != null) {
            predicates.add(cb.equal(root.get("type"), filterRequest.getType()));
        }

        if (filterRequest.getStartDate() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), parseDate(filterRequest.getStartDate())));
        }

        if (filterRequest.getEndDate() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), parseDate(filterRequest.getEndDate())));
        }

        if (filterRequest.getSearch() != null && !filterRequest.getSearch().isBlank()) {
            String likeSearch = "%" + filterRequest.getSearch().toLowerCase() + "%";
            predicates.add(cb.or(
                    cb.like(cb.lower(root.get("notes")), likeSearch),
                    cb.like(cb.lower(root.get("transactionNumber")), likeSearch),
                    cb.like(cb.lower(senderUser.get("fullName")), likeSearch),
                    cb.like(cb.lower(sender.get("number")), likeSearch),
                    cb.like(cb.lower(receiverUser.get("fullName")), likeSearch),
                    cb.like(cb.lower(receiver.get("number")), likeSearch),
                    cb.like(cb.lower(category.get("name")), likeSearch)
            ));
        }

        return predicates;
    }

    private Timestamp parseDate(String dateStr) {
        try {
            return new Timestamp(new SimpleDateFormat("dd-MM-yyyy").parse(dateStr).getTime());
        } catch (ParseException e) {
            throw new RuntimeException("Invalid date format", e);
        }
    }
}


//@Repository
//public class CustomTransactionRepository {
//
//    @PersistenceContext
//    private EntityManager entityManager;
//
//    public Page<TransactionResponse> findTransactionsWithSignedAmount(TransactionFilterRequest filter, Pageable pageable) {
//        String baseSelect = buildSelectQuery(filter, pageable);
//        Query dataQuery = entityManager.createNativeQuery(baseSelect, TransactionResponse.class);
//        setQueryParameters(dataQuery, filter);
//        applyPagination(dataQuery, pageable);
//
//        List<TransactionResponse> results = dataQuery.getResultList();
//
//        String countQueryStr = buildCountQuery(filter);
//        Query countQuery = entityManager.createNativeQuery(countQueryStr);
//        setQueryParameters(countQuery, filter);
//        long count = ((Number) countQuery.getSingleResult()).longValue();
//
//        return new PageImpl<>(results, pageable, count);
//    }
//
//    private String buildSelectQuery(TransactionFilterRequest filter, Pageable pageable) {
//        StringBuilder query = new StringBuilder("""
//            SELECT
//                t.id,
//                t.transaction_number,
//                CASE
//                    WHEN s.number = :walletNumber THEN -t.amount
//                    WHEN a.number = :walletNumber THEN t.amount
//                    ELSE 0
//                END AS amount,
//                t.created_at,
//                s.number AS sender_number,
//                a.number AS receiver_number,
//                t.type,
//                t.notes
//            FROM transactions t
//            JOIN wallets s ON t.sender_number_id = s.id
//            JOIN wallets a ON t.receiver_number_id = a.id
//            WHERE (:walletNumber IS NULL OR s.number = :walletNumber OR a.number = :walletNumber)
//        """);
//
//        appendDynamicFilters(query, filter);
//        appendSorting(query, pageable);
//
//        return query.toString();
//    }
//
//    private String buildCountQuery(TransactionFilterRequest filter) {
//        StringBuilder query = new StringBuilder("""
//            SELECT COUNT(*)
//            FROM transactions t
//            JOIN wallets s ON t.sender_number_id = s.id
//            JOIN wallets a ON t.receiver_number_id = a.id
//            WHERE (:walletNumber IS NULL OR s.number = :walletNumber OR a.number = :walletNumber)
//        """);
//
//        appendDynamicFilters(query, filter);
//        return query.toString();
//    }
//
//    private void appendDynamicFilters(StringBuilder query, TransactionFilterRequest filter) {
//        if (filter.getTransactionNumber() != null) query.append("AND (:transactionNumber IS NULL OR t.transaction_number = :transactionNumber) ");
//        if (filter.getassociateWallet() != null) query.append("AND (:associateWallet IS NULL OR a.number = :associateWallet) ");
//        if (filter.getwallet() != null) query.append("AND (:wallet IS NULL OR s.number = :wallet) ");
//        if (filter.getType() != null) query.append("AND (:type IS NULL OR t.type = :type) ");
//        if (filter.getStartDate() != null) query.append("AND (:startDate IS NULL OR t.created_at >= :startDate) ");
//        if (filter.getEndDate() != null) query.append("AND (:endDate IS NULL OR t.created_at <= :endDate) ");
//    }
//
//    private void appendSorting(StringBuilder query, Pageable pageable) {
//        String sortField = "created_at";
//        String sortDirection = "ASC";
//        if (pageable.getSort().isSorted()) {
//            Sort.Order order = pageable.getSort().iterator().next();
//            sortField = order.getProperty();
//            sortDirection = order.getDirection().name();
//        }
//
//        if ("signed_amount".equalsIgnoreCase(sortField)) {
//            query.append(" ORDER BY signed_amount ").append(sortDirection);
//        } else {
//            query.append(" ORDER BY t.").append(sortField).append(" ").append(sortDirection);
//        }
//    }
//
//    private void setQueryParameters(Query query, TransactionFilterRequest filter) {
//        query.setParameter("walletNumber", filter.getWalletNumber());
//        if (filter.getTransactionNumber() != null) query.setParameter("transactionNumber", filter.getTransactionNumber());
//        if (filter.getassociateWallet() != null) query.setParameter("associateWallet", filter.getassociateWallet());
//        if (filter.getwallet() != null) query.setParameter("wallet", filter.getwallet());
//        if (filter.getType() != null) query.setParameter("type", filter.getType());
//        if (filter.getStartDate() != null) query.setParameter("startDate", parseDate(filter.getStartDate()));
//        if (filter.getEndDate() != null) query.setParameter("endDate", parseDate(filter.getEndDate()));
//    }
//
//    private void applyPagination(Query query, Pageable pageable) {
//        query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
//        query.setMaxResults(pageable.getPageSize());
//    }
//
//    private Timestamp parseDate(String dateStr) {
//        try {
//            return new Timestamp(new SimpleDateFormat("dd-MM-yyyy").parse(dateStr).getTime());
//        } catch (ParseException e) {
//            throw new IllegalArgumentException("Invalid date format: " + dateStr);
//        }
//    }
//}
