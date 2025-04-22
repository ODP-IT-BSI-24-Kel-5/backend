package id.co.bankbsi.e_walled.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Setter
@Getter
@MappedSuperclass
public abstract class Timestamp {

    @CreatedDate
    @Column(updatable = false, name = "created_at")
    @JsonFormat(pattern = "dd MMMM yyyy HH:mm", locale = "en", shape = JsonFormat.Shape.STRING)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @JsonFormat(pattern = "dd MMMM yyyy HH:mm", locale = "en", shape = JsonFormat.Shape.STRING)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(updatable = false, name = "updated_at")
    private LocalDateTime updatedAt;

}