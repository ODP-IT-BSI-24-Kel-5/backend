package id.co.bankbsi.e_walled.repositories;

import id.co.bankbsi.e_walled.models.Users;
import id.co.bankbsi.e_walled.models.Wallets;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletRepository extends JpaRepository<Wallets, Long> {
    List<Wallets> findByUserId(UUID userId);
    Wallets findFirstByUserIdAndIsMain(UUID userId, boolean isMain);
    Optional<Wallets> findById(UUID userId);
    Wallets findFirstByUserIdAndNumber(UUID userId, String number);
    Optional<Wallets> findByUserIdAndNumber(UUID userId, String number);
    boolean existsByNumber(String number);
    Wallets findByNumber(String number);

    Wallets findByNumberAndUserId(String number,UUID userId);

    @Query("SELECT SUM(balance) FROM Wallets w WHERE w.user.id = :userId")
    Long sumBalance(UUID userId);


    @Modifying
    @Transactional
    @Query("UPDATE Wallets w SET w.balance = :balance WHERE w.id = :walletId")
    void updateWalletBalance(UUID walletId, Long balance);


    @Modifying
    @Transactional
    @Query("UPDATE Wallets w SET w.name = :name WHERE w.id = :walletId")
    void updateWalletName(UUID walletId, String name);

    @Modifying
    @Transactional
    @Query("UPDATE Wallets w SET w.isMain = false WHERE w.user = :user")
    void removeMain(Users user);
    Long findBalanceByNumber(String number);
}
