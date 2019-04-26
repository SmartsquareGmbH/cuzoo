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
            return ResponseEntity.badRequest().body("Die hoch zu ladende Datei ist leer!");
        }

        List<Company> insertedCompanies = csvConverter.getConvertedCompanies(file.getInputStream());

        insertedCompanies.forEach(companyRepository::save);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/submit")
    public final ResponseEntity<?> submitCompany(@RequestBody @Valid CompanyForm companyForm,
                                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body("Ein Unternehmen kann nicht ohne Namen eingereicht werden!");
        }

        Company company = getOrCreateCompany(companyForm);
        Company savedCompany;

        Long companyIdBeforeSaving = companyForm.getId();

        try {
            if (companyForm.getLabels() != null) {
                submitLabels(companyForm.getLabels(), company);
            }

            savedCompany = companyRepository.save(company);
            labelRepository.deleteAllReferenceless();
        } catch (DataAccessException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (companyIdBeforeSaving < 1) {
            return new ResponseEntity<>(savedCompany.getId(), HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Company getOrCreateCompany(@RequestBody @Valid CompanyForm companyForm) {
        Company company;
        Optional<Company> byId = companyRepository.findById(companyForm.getId());
        if (byId.isPresent()) {
            company = byId.get();

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
        return company;
    }

    private void submitLabels(List<String> titles, Company company) {
        List<Label> companyLabels = titles.stream()
                .map(title -> new Tuple(labelRepository.findByTitle(title), title))
                .map(tuple -> tuple.optionalLabel.orElse(new Label(tuple.title)))
                .map(label -> label.addCompany(company))
                .collect(Collectors.toList());

        company.setLabels(companyLabels);
    }

    class Tuple {
        final Optional<Label> optionalLabel;
        final String title;

        Tuple(Optional<Label> optionalLabel, String title) {
            this.optionalLabel = optionalLabel;
            this.title = title;
        }
    }

    @DeleteMapping("/delete/{companyId}")
    public final ResponseEntity<?> deleteCompany(@PathVariable Long companyId) {
        if (!companyRepository.existsById(companyId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Das Unternehmen wurde nicht gefunden!");
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

    @GetMapping("/get/labels")
    public final ResponseEntity<List<String>> getCompanyLabels() {
        return ResponseEntity.ok(labelRepository
                .findAllOfCompany()
                .stream()
                .map(Label::getTitle)
                .collect(Collectors.toList()));
    }
}