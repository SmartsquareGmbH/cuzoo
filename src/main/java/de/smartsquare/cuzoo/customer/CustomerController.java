package de.smartsquare.cuzoo.customer;

import de.smartsquare.cuzoo.csv.CSVImporter;
import de.smartsquare.cuzoo.csv.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public final String postCompanyCSV(@RequestParam("file") MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            CSVImporter csvImporter = new CSVImporter();
            InputStream inputFile = new BufferedInputStream(file.getInputStream());

            insertImportedCompanies(csvImporter.importFrom(inputFile, Company.class));
            return "IMPORTING CUSTOMER CSV SUCCEEDED";
        } else
            return "IMPORTING CUSTOMER CSV FAILED: File is empty!";
    }

    @PostMapping("/submit")
    public final String submitCustomer(@RequestBody Customer customer) {
        try {
            repository.save(customer);
            return "SUBMITTING CUSTOMER SUCCEEDED";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @PostMapping("/delete")
    public final String deleteCustomer(@RequestBody Customer customer) {
        try {
            repository.delete(customer);
            return "DELETING CUSTOMER SUCCEEDED";
        } catch (Exception e) {
            return e.getMessage();
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
