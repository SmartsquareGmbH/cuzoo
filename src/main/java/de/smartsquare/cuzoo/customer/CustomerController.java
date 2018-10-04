package de.smartsquare.cuzoo.customer;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    private final CustomerRepository repository;

    public CustomerController(final CustomerRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/list")
    public final List<Customer> getCustomers() {
        return repository.findAll();
    }
}
