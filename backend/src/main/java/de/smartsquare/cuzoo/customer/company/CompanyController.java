package de.smartsquare.cuzoo.customer.company;

import de.smartsquare.cuzoo.customer.CSVConverter;
import de.smartsquare.cuzoo.customer.label.Label;
import de.smartsquare.cuzoo.customer.label.LabelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/company")
public class CompanyController {

    private final CompanyRepository companyRepository;
    private final LabelRepository labelRepository;
    private final CSVConverter csvConverter;

    @Autowired
    public CompanyController(final CompanyRepository companyRepository, CSVConverter csvConverter,
                             final LabelRepository labelRepository) {
        this.companyRepository = companyRepository;
        this.labelRepository = labelRepository;
        this.csvConverter = csvConverter;
    }

    @PostMapping("/import")
    public final ResponseEntity<?> postCSV(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        List<Company> insertedCompanies = csvConverter.getConvertedCompanies(file.getInputStream());

        insertedCompanies.forEach(companyRepository::save);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/submit")
    public final ResponseEntity<?> submitCompany(@RequestBody @Valid CompanyForm companyForm,
                                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Company company;

        if (companyRepository.findById(companyForm.getId()).isPresent()) {
            company = companyRepository.findById(companyForm.getId()).get();

            company.setName(companyForm.getName());
            company.setStreet(companyForm.getStreet());
            company.setZipcode(companyForm.getZipcode());
            company.setPlace(companyForm.getPlace());
            company.setHomepage(companyForm.getHomepage());
            company.setDescription(companyForm.getDescription());
            company.setOther(companyForm.getOther());
        } else {
            company = new Company(
                    companyForm.getName(),
                    companyForm.getStreet(),
                    companyForm.getZipcode(),
                    companyForm.getPlace(),
                    companyForm.getHomepage(),
                    companyForm.getDescription(),
                    companyForm.getOther());
        }

        Long companyIdBeforeSaving = companyForm.getId();

        try {
            if (companyForm.getLabels() != null) {
                submitLabels(companyForm.getLabels(), company);
            }

            companyRepository.save(company);
            labelRepository.deleteAllReferenceless();
        } catch (DataAccessException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (companyIdBeforeSaving < 1) {
            return new ResponseEntity<>(HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    private void submitLabels(List<String> titles, Company company) {
        List<Label> companyLabels = new ArrayList<>();

        titles.forEach(title -> {
            Optional<Label> label = labelRepository.findByTitle(title);

            if (label.isPresent()) {
                label.get().addCompany(company);

                companyLabels.add(label.get());
            } else {
                Label labelToSave = new Label(title);
                labelToSave.addCompany(company);

                companyLabels.add(labelToSave);
            }
        });

        company.setLabels(companyLabels);
    }

    @DeleteMapping("/delete/{companyId}")
    public final ResponseEntity<?> deleteCompany(@PathVariable Long companyId) {
        if (!companyRepository.existsById(companyId)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        try {
            companyRepository.deleteById(companyId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get")
    public final ResponseEntity<List<Company>> getCompanies() {
        return ResponseEntity.ok(companyRepository.findAll());
    }

    @GetMapping("/get/labels/{input}")
    public final ResponseEntity<List<String>> getContactPointLabelsWithInput(@PathVariable String input) {
        return ResponseEntity.ok(labelRepository
                .findAllOfCompanyByPartOfTitle(input)
                .stream()
                .map(Label::getTitle)
                .collect(Collectors.toList()));
    }

}