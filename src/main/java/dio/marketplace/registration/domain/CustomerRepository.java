package dio.marketplace.registration.domain;

import java.util.List;

public interface CustomerRepository {
    Customer save(Customer customer);
    Customer findById(CustomerId id);
    List<Customer> findAll();
    void delete(CustomerId id);
}
