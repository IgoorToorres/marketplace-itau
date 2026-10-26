package dio.marketplace.registration.infrastructure.persistence.repository;

import dio.marketplace.registration.domain.Customer;
import dio.marketplace.registration.domain.CustomerId;
import dio.marketplace.registration.domain.CustomerRepository;
import dio.marketplace.registration.infrastructure.persistence.mapper.CustomerMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.StreamSupport;

@Repository
public class JpaCustomerRepository implements CustomerRepository {
    private final CustomerEntityRepository customerEntityRepository;
    private final CustomerMapper customerMapper;

    public JpaCustomerRepository(
            CustomerEntityRepository customerEntityRepository,
            CustomerMapper customerMapper
    ) {
        this.customerEntityRepository = customerEntityRepository;
        this.customerMapper = customerMapper;
    }

    @Override
    public Customer save(Customer customer) {
        var savedEntity = customerEntityRepository.save(customerMapper.toEntity(customer));
        return customerMapper.toDomain(savedEntity);
    }

    @Override
    public Customer findById(CustomerId id) {
        return customerEntityRepository.findById(id.id())
                .map(customerMapper::toDomain)
                .orElse(null);
    }

    @Override
    public List<Customer> findAll() {
        return StreamSupport.stream(customerEntityRepository.findAll().spliterator(), false)
                .map(customerMapper::toDomain)
                .toList();
    }

    @Override
    public void delete(CustomerId id) {
        customerEntityRepository.deleteById(id.id());
    }
}
