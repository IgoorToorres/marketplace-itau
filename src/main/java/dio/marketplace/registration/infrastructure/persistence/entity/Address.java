package dio.marketplace.registration.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@RequiredArgsConstructor
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String street;
    private String postalCode;
    private String city;
    private String state;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private Instant createdOn;

    @Override
    public String toString() {
        return String.format("%s, %s, %s, %s", street, postalCode, city, state);
    }
}
