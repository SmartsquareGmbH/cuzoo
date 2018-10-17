package de.smartsquare.cuzoo.customer;

import de.smartsquare.cuzoo.csv.*;
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
@RequestMapping("/company")
public class CompanyController {

    private final CompanyRepository repository;

    @Autowired
    public CompanyController(final CompanyRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/import")
    public final ResponseEntity<?> postCSV(@RequestParam("file") MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            CSVImporter csvImporter = new CSVImporter();
            InputStream inputFile = new BufferedInputStream(file.getInputStream());

            insertImportedCompanies(csvImporter.importFrom(inputFile, CSVCompany.class));
            return new ResponseEntity<>(HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/submit")
    public final ResponseEntity<?> sumbitCompany(@RequestBody @Valid Company company,
                                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            try {
                if (repository.existsById(company.getId())) {
                    repository.save(company);
                    return new ResponseEntity<>(HttpStatus.OK);
                } else {
                    repository.save(company);
                    return new ResponseEntity<>(HttpStatus.CREATED);
                }
            } catch (DataAccessException e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @PostMapping("/delete")
    public final ResponseEntity<?> deleteCompany(@RequestBody @Valid Company company) {
        try {
            repository.delete(company);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    void insertImportedCompanies(List<CSVCompany> importedEntity) {
        for (CSVCompany CSVCompany : importedEntity) {
            Company company = new Company(
                    CSVCompany.getCompany(),
                    CSVCompany.getStreet(),
                    CSVCompany.getZipCode(),
                    CSVCompany.getPlace(),
                    CSVCompany.getHomepage(),
                    CSVCompany.getPurpose(),
                    CSVCompany.getOther());

            repository.save(company);
        }
    }

    @GetMapping("/get")
    public final ResponseEntity<List<Company>> getCompanies() {
        return ResponseEntity.ok(repository.findAll());
    }
}
