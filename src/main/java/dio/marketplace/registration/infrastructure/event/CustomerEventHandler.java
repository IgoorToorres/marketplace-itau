package dio.marketplace.registration.infrastructure.event;

import dio.marketplace.registration.infrastructure.persistence.entity.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterDelete;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler
public class CustomerEventHandler {
    private static final Logger logger = LoggerFactory.getLogger(CustomerEventHandler.class);

    @HandleAfterCreate
    public void handleAfterCreate(Customer customer) {
        logger.info("CustomerEventHandler#handleAfterCreate customerId={}", customer.getId());
    }

    @HandleAfterSave
    public void handleAfterSave(Customer customer) {
        logger.info("CustomerEventHandler#handleAfterSave customerId={}", customer.getId());
    }

    @HandleAfterDelete
    public void handleAfterDelete(Customer customer) {
        logger.info("CustomerEventHandler#handleAfterDelete customerId={}", customer.getId());
    }
}
