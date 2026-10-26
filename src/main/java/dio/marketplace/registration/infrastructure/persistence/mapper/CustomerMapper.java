package dio.marketplace.registration.infrastructure.persistence.mapper;

import dio.marketplace.registration.domain.CustomerId;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class CustomerMapper {

    public dio.marketplace.registration.infrastructure.persistence.entity.Customer toEntity(
            dio.marketplace.registration.domain.Customer customer
    ) {
        var entity = new dio.marketplace.registration.infrastructure.persistence.entity.Customer();
        var nameParts = customer.getName().trim().split("\\s+", 2);

        entity.setId(customer.getId().id());
        entity.setFirstName(nameParts[0]);
        entity.setLastName(nameParts.length == 2 ? nameParts[1] : null);
        entity.setEmail(customer.getEmail());

        return entity;
    }

    public dio.marketplace.registration.domain.Customer toDomain(
            dio.marketplace.registration.infrastructure.persistence.entity.Customer entity
    ) {
        var name = StringUtils.hasText(entity.getLastName())
                ? entity.getFirstName() + " " + entity.getLastName().trim()
                : entity.getFirstName();

        return new dio.marketplace.registration.domain.Customer(
                new CustomerId(entity.getId()),
                name,
                entity.getEmail()
        );
    }
}
