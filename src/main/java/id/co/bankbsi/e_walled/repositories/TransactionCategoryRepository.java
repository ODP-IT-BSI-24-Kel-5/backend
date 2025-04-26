package id.co.bankbsi.e_walled.repositories;

import id.co.bankbsi.e_walled.models.TransactionCategories;
import id.co.bankbsi.e_walled.models.Transactions;
import id.co.bankbsi.e_walled.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionCategoryRepository extends JpaRepository<TransactionCategories, Long> {
    TransactionCategories findFirstById(Long id);
}
