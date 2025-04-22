package id.co.bankbsi.e_walled.dto.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class SortRequest {
    private String field;
    private String direction;

    public boolean isAscending() {
        return "asc".equalsIgnoreCase(this.direction);
    }
}