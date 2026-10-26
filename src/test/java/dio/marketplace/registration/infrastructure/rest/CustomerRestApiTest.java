package dio.marketplace.registration.infrastructure.rest;

import dio.marketplace.registration.infrastructure.persistence.entity.Address;
import dio.marketplace.registration.infrastructure.persistence.entity.Customer;
import dio.marketplace.registration.infrastructure.persistence.repository.CustomerEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(properties = "spring.docker.compose.skip.in-tests=false")
class CustomerRestApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerEntityRepository customerRepository;

    @BeforeEach
    void limparRegistros() {
        customerRepository.deleteAll();
    }

    @Test
    void deveCriarClienteComUuidEConsultarDadosPersistidos() throws Exception {
        var result = mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Maria",
                                  "lastName": "Silva",
                                  "email": "maria.rest@example.com",
                                  "address": {
                                    "street": "Rua das Flores, 10",
                                    "postalCode": "01001-000",
                                    "city": "Sao Paulo",
                                    "state": "SP"
                                  }
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn();

        var location = URI.create(result.getResponse().getHeader("Location"));
        var id = UUID.fromString(location.getPath().substring(location.getPath().lastIndexOf('/') + 1));

        assertThat(customerRepository.findById(id))
                .get()
                .satisfies(customer -> {
                    assertThat(customer.getId()).isEqualTo(id);
                    assertThat(customer.getAddress()).isNotNull();
                    assertThat(customer.getAddress().getCity()).isEqualTo("Sao Paulo");
                });

        mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Maria"))
                .andExpect(jsonPath("$.lastName").value("Silva"))
                .andExpect(jsonPath("$.email").value("maria.rest@example.com"));
    }

    @Test
    void deveExibirEnderecoFormatadoNaProjecaoDaColecao() throws Exception {
        customerRepository.save(customer("Ana", "Souza", "ana.rest@example.com"));

        mockMvc.perform(get("/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.customers[0].firstName").value("Ana"))
                .andExpect(jsonPath("$._embedded.customers[0].lastName").value("Souza"))
                .andExpect(jsonPath("$._embedded.customers[0].address")
                        .value("Rua das Flores, 10, 01001-000, Sao Paulo, SP"));
    }

    @Test
    void deveBuscarClientesPorPrefixoSemDiferenciarMaiusculasEMinusculas() throws Exception {
        customerRepository.save(customer("Marina", "Costa", "marina.rest@example.com"));
        customerRepository.save(customer("Carlos", "Lima", "carlos.rest@example.com"));

        mockMvc.perform(get("/customers/search/findByFirstNameStartingWithIgnoreCase")
                        .queryParam("firstName", "MAR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.customers.length()").value(1))
                .andExpect(jsonPath("$._embedded.customers[0].firstName").value("Marina"));
    }

    @Test
    void deveAtualizarParcialmenteCliente() throws Exception {
        var customer = customerRepository.save(customer("Joao", "Silva", "joao.rest@example.com"));

        mockMvc.perform(patch("/customers/{id}", customer.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"lastName": "Oliveira"}
                                """))
                .andExpect(status().isNoContent());

        assertThat(customerRepository.findById(customer.getId()))
                .get()
                .extracting(Customer::getLastName)
                .isEqualTo("Oliveira");
    }

    @Test
    void naoDevePermitirExclusaoPelaApiRest() throws Exception {
        var customer = customerRepository.save(customer("Pedro", "Santos", "pedro.rest@example.com"));

        mockMvc.perform(delete("/customers/{id}", customer.getId()))
                .andExpect(status().isMethodNotAllowed());

        assertThat(customerRepository.existsById(customer.getId())).isTrue();
    }

    private Customer customer(String firstName, String lastName, String email) {
        var address = new Address();
        address.setStreet("Rua das Flores, 10");
        address.setPostalCode("01001-000");
        address.setCity("Sao Paulo");
        address.setState("SP");

        var customer = new Customer();
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setEmail(email);
        customer.setAddress(address);
        return customer;
    }
}
