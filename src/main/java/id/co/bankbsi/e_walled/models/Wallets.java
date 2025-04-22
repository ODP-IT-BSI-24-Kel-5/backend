package id.co.bankbsi.e_walled.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import id.co.bankbsi.e_walled.repositories.WalletRepository;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table
@EntityListeners(org.springframework.data.jpa.domain.support.AuditingEntityListener.class)
public class Wallets extends Timestamp {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private UUID id;
    private long balance;
    private String name;
    private String number;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonBackReference
    @JoinColumn(name = "user_id")
    private Users user;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToMany(mappedBy = "wallet",cascade = CascadeType.PERSIST)
    private List<Transactions> transactions;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToMany(mappedBy = "associateWallet",cascade = CascadeType.PERSIST)
    private List<Transactions> associateTrans;

    @JoinColumn(columnDefinition = "default false")
    private Boolean isMain;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToMany(mappedBy = "walletFavorites")
    private List<Users> userFavorites;
}
