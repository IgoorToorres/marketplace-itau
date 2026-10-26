package dio.marketplace.registration.infrastructure.persistence.repository;

import dio.marketplace.registration.domain.Customer;
import dio.marketplace.registration.domain.CustomerId;
import dio.marketplace.registration.domain.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest(properties = "spring.docker.compose.skip.in-tests=false")
class JpaCustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerEntityRepository customerEntityRepository;

    @BeforeEach
    void limparRegistros() {
        customerEntityRepository.deleteAll();
    }

    @Test
    void deveSalvarEConsultarCustomerPorId() {
        var id = new CustomerId(UUID.fromString("e49523d9-73d4-43af-a4c5-71d6883e836c"));
        var customer = new Customer(id, "Maria Clara da Silva", "maria@example.com");

        var savedCustomer = customerRepository.save(customer);
        var foundCustomer = customerRepository.findById(id);

        assertThat(savedCustomer.getId()).isEqualTo(id);
        assertThat(foundCustomer).isNotNull();
        assertThat(foundCustomer.getId()).isEqualTo(id);
        assertThat(foundCustomer.getName()).isEqualTo("Maria Clara da Silva");
        assertThat(foundCustomer.getEmail()).isEqualTo("maria@example.com");
    }

    @Test
    void deveListarTodosOsCustomersPersistidos() {
        customerRepository.save(new Customer("Prince", "prince@example.com"));
        customerRepository.save(new Customer("Ana Maria Souza", "ana@example.com"));

        var customers = customerRepository.findAll();

        assertThat(customers)
                .extracting(Customer::getName)
                .containsExactlyInAnyOrder("Prince", "Ana Maria Souza");
    }

    @Test
    void deveExcluirCustomerPorId() {
        var id = UUID.fromString("94429237-fd12-4c32-a856-1ba252ccdd4a");
        var entity = new dio.marketplace.registration.infrastructure.persistence.entity.Customer();
        entity.setId(id);
        entity.setFirstName("Joao");
        entity.setLastName("Silva");
        entity.setEmail("joao@example.com");
        customerEntityRepository.save(entity);

        customerRepository.delete(new CustomerId(id));

        assertThat(customerEntityRepository.existsById(id)).isFalse();
    }

    @Test
    void deveRetornarNuloQuandoCustomerNaoExistir() {
        var customer = customerRepository.findById(new CustomerId());

        assertThat(customer).isNull();
    }
}
