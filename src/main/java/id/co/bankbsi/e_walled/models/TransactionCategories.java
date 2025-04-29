package id.co.bankbsi.e_walled.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;


@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class TransactionCategories extends Timestamp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // For example, "Eat" or "Vacation"
    private String icon; // For example, "Eat" or "Vacation"

    @JsonCreator
    public TransactionCategories(@JsonProperty("id") Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "parent_id", nullable = true)
    @JsonBackReference
    private TransactionCategories parentCategory; // Parent category for subcategories

    @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<TransactionCategories> subCategories; // Subcategories (self-referencing)

    public boolean isCategory() {
        return parentCategory == null;
    }

    public boolean isSubCategory() {
        return parentCategory != null;
    }
}