package id.co.bankbsi.e_walled.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email"),
                @UniqueConstraint(columnNames = "mobilePhone"),
                @UniqueConstraint(columnNames = "fullName")
        }
)
@EntityListeners(org.springframework.data.jpa.domain.support.AuditingEntityListener.class)
public class Users extends Timestamp {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private UUID id;
    private String fullName;
    private String email;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String mobilePhone;
    private String imageUrl;

    @OneToMany(mappedBy = "user")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Wallets> wallets;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name="tokenVersion", columnDefinition="integer default 0")
    private Integer tokenVersion = 0;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private UUID sessionId;


    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_wallet_favorites",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "wallet_id"))
    private List<Wallets> walletFavorites;
}
