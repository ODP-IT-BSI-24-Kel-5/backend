package id.co.bankbsi.e_walled.specifications;


import id.co.bankbsi.e_walled.dto.request.TransactionFilterRequest;
import id.co.bankbsi.e_walled.models.Transactions;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Transaction;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class TransactionSpecification {

    public static Specification<Transactions> getSpecification(TransactionFilterRequest filterRequest) {
        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (filterRequest.getType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("type"), filterRequest.getType()));
            }

            if (filterRequest.getStartDate() != null) {
                predicates.add(criteriaBuilder.equal(root.get("startDate"), filterRequest.getStartDate()));
            }
            if (filterRequest.getAssociateWallet() != null) {
                predicates.add(criteriaBuilder.equal(root.get("associateWallet").get("number"), filterRequest.getAssociateWallet()));
            }

            if (filterRequest.getWallet() != null) {
                predicates.add(criteriaBuilder.equal(root.get("wallet").get("number"), filterRequest.getWallet()));
            }

            if (filterRequest.getWalletNumber() != null) {
                Predicate senderMatch = criteriaBuilder.equal(
                        root.get("associateWallet").get("number"), filterRequest.getWalletNumber()
                );
                Predicate acquirerMatch = criteriaBuilder.equal(
                        root.get("wallet").get("number"), filterRequest.getWalletNumber()
                );
                predicates.add(criteriaBuilder.or(senderMatch, acquirerMatch));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

        };
    }
}
