package dio.marketplace.registration.infrastructure.persistence.mapper;

import dio.marketplace.registration.domain.Customer;
import dio.marketplace.registration.domain.CustomerId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerMapperTest {

    private final CustomerMapper mapper = new CustomerMapper();

    @Test
    void deveSepararNomeNoPrimeiroEspacoEPreservarIdEEmail() {
        var id = UUID.fromString("a340e7d5-3a06-4f51-94c1-5c1c8ca611f6");
        var customer = new Customer(
                new CustomerId(id),
                "Maria Clara da Silva",
                "maria@example.com"
        );

        var entity = mapper.toEntity(customer);

        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getFirstName()).isEqualTo("Maria");
        assertThat(entity.getLastName()).isEqualTo("Clara da Silva");
        assertThat(entity.getEmail()).isEqualTo("maria@example.com");
    }

    @Test
    void deveManterSobrenomeNuloQuandoNomeNaoPossuiEspaco() {
        var customer = new Customer("Prince", "prince@example.com");

        var entity = mapper.toEntity(customer);

        assertThat(entity.getFirstName()).isEqualTo("Prince");
        assertThat(entity.getLastName()).isNull();
    }

    @Test
    void deveConcatenarNomeESobrenomeAoConverterParaDominio() {
        var id = UUID.fromString("1450a438-b6f2-40a2-bf10-a7202465dafe");
        var entity = entity(id, "Ana", "Maria Souza", "ana@example.com");

        var customer = mapper.toDomain(entity);

        assertThat(customer.getId()).isEqualTo(new CustomerId(id));
        assertThat(customer.getName()).isEqualTo("Ana Maria Souza");
        assertThat(customer.getEmail()).isEqualTo("ana@example.com");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "   ")
    void deveUsarApenasPrimeiroNomeQuandoSobrenomeNaoPossuiTexto(String lastName) {
        var entity = entity(UUID.randomUUID(), "Madonna", lastName, "madonna@example.com");

        var customer = mapper.toDomain(entity);

        assertThat(customer.getName()).isEqualTo("Madonna");
    }

    private dio.marketplace.registration.infrastructure.persistence.entity.Customer entity(
            UUID id,
            String firstName,
            String lastName,
            String email
    ) {
        var entity = new dio.marketplace.registration.infrastructure.persistence.entity.Customer();
        entity.setId(id);
        entity.setFirstName(firstName);
        entity.setLastName(lastName);
        entity.setEmail(email);
        return entity;
    }
}
