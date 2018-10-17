package de.smartsquare.cuzoo.customer;

import de.smartsquare.cuzoo.csv.CSVImporter;
import de.smartsquare.cuzoo.csv.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    private final CustomerRepository repository;

    @Autowired
    public CustomerController(final CustomerRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/import")
    public final ResponseEntity<?> postCompanyCSV(@RequestParam("file") MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            CSVImporter csvImporter = new CSVImporter();
            InputStream inputFile = new BufferedInputStream(file.getInputStream());

            insertImportedCompanies(csvImporter.importFrom(inputFile, Company.class));
            return new ResponseEntity<>(HttpStatus.CREATED);
        } else
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/submit")
    public final ResponseEntity<?> submitCustomer(@RequestBody @Valid Customer customer, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            try {
                if (repository.existsById(customer.getId())) {
                    repository.save(customer);
                    return new ResponseEntity<>(HttpStatus.OK);
                } else {
                    repository.save(customer);
                    return new ResponseEntity<>(HttpStatus.CREATED);
                }
            } catch (DataAccessException e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @PostMapping("/delete")
    public final ResponseEntity<?> deleteCustomer(@RequestBody @Valid Customer customer) {
        try {
            repository.delete(customer);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    void insertImportedCompanies(List<Company> importedEntity) {
        for (Company company : importedEntity) {
            Customer customer = new Customer(
                    company.getCompany(),
                    company.getStreet(),
                    company.getZipCode(),
                    company.getPlace(),
                    company.getHomepage(),
                    company.getPurpose(),
                    company.getOther());

            repository.save(customer);
        }
    }

    @GetMapping("/get")
    public final List<Customer> getCustomers() {
        return repository.findAll();
    }
}
