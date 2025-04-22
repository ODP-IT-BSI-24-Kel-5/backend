package id.co.bankbsi.e_walled.dto;

import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.SqlResultSetMapping;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@SqlResultSetMapping(
        name = "BalanceGrowthMapping",
        classes = @ConstructorResult(
                targetClass = WalletBalanceGrowthDTO.class,
                columns = {
                        @ColumnResult(name = "wallet_id", type = UUID.class),
                        @ColumnResult(name = "period", type = LocalDateTime.class),
                        @ColumnResult(name = "total", type = Long.class)
                }
        )
)
@AllArgsConstructor
@Getter
public class WalletBalanceGrowthDTO {
    private UUID walletId;
    private LocalDateTime period;
    private Long total;
}