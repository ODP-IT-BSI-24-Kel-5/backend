package id.co.bankbsi.e_walled.repositories;

import id.co.bankbsi.e_walled.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findFirstByEmailOrMobilePhoneOrFullName(String email, String mobilePhone, String fullName);
    Users findFirstByEmail(String email);
    Optional<Users> findByEmail(String email);
    Users findById(UUID userId);
    Optional<Users> findUserById(UUID userId);
    boolean existsById(UUID userId);
}
