package de.smartsquare.cuzoo.customer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CustomerRepositorySpecification {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    public final void save_customer() {
        Customer darius = new Customer("Tack", "Darius");

        customerRepository.save(darius);

        assertThat(customerRepository.findAll().size()).isEqualTo(1);
    }

}
