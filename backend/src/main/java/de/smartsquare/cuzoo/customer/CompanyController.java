package de.smartsquare.cuzoo.customer;

import de.smartsquare.cuzoo.csv.CSVCompany;
import de.smartsquare.cuzoo.csv.CSVImporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/company")
public class CompanyController {

    private final CompanyRepository companyRepository;

    @Autowired
    public CompanyController(final CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
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
                if (companyRepository.existsById(company.getId())) {
                    companyRepository.save(company);
                    return new ResponseEntity<>(HttpStatus.OK);
                } else {
                    companyRepository.save(company);
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
            companyRepository.delete(company);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    void insertImportedCompanies(List<CSVCompany> importedEntity) {
        for (CSVCompany csvCompany : importedEntity) {
            Company company = new Company(
                    csvCompany.getCompany(),
                    csvCompany.getStreet(),
                    csvCompany.getZipCode(),
                    csvCompany.getPlace(),
                    csvCompany.getHomepage(),
                    "Lead",
                    csvCompany.getPurpose(),
                    csvCompany.getOther());

            companyRepository.save(company);
        }
    }

    @GetMapping("/get")
    public final ResponseEntity<List<Company>> getCompanies() {
        return ResponseEntity.ok(companyRepository.findAll());
    }
}
