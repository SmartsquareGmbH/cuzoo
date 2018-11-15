package de.smartsquare.cuzoo.customer;

import de.smartsquare.cuzoo.csv.CSVContact;
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
@RequestMapping("/api/contact")
public class ContactController {

    private final ContactRepository contactRepository;
    private final CompanyRepository companyRepository;

    @Autowired
    public ContactController(final ContactRepository contactRepository, final CompanyRepository companyRepository) {
        this.contactRepository = contactRepository;
        this.companyRepository = companyRepository;
    }

    @PostMapping("/import")
    public final ResponseEntity<?> postCSV(@RequestParam("file") MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            CSVImporter csvImporter = new CSVImporter();
            InputStream inputFile = new BufferedInputStream(file.getInputStream());

            insertImportedContactsWithMissingCompanies(csvImporter.importFrom(inputFile, CSVContact.class));
            return new ResponseEntity<>(HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/submit")
    public final ResponseEntity<?> sumbitContact(@RequestBody @Valid Contact contact,
                                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            try {
                if (contact.getCompany() != null && !contact.getCompany().getName().equals("")) {
                    if (companyRepository != null && !companyRepository.existsByName(contact.getCompany().getName())) {
                        Company company = new Company(contact.getCompany().getName(), com.sun.tools.javac.util.List.of(contact), "", "", "", "", "", "", "");

                        companyRepository.save(company);
                    }
                }

                if (contactRepository.existsById(contact.getId())) {
                    contactRepository.save(contact);
                    return new ResponseEntity<>(HttpStatus.OK);
                } else {
                    contactRepository.save(contact);
                    return new ResponseEntity<>(HttpStatus.CREATED);
                }
            } catch (DataAccessException e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @PostMapping("/delete")
    public final ResponseEntity<?> deleteContact(@RequestBody @Valid Contact contact) {
        try {
            contactRepository.delete(contact);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void insertImportedContactsWithMissingCompanies(List<CSVContact> importedEntity) {
        for (CSVContact csvContact : importedEntity) {
            Company companyOfContact;
            Contact contact;

            if (csvContact.getCompany() == null && csvContact.getRole().equals("Freiberufler")) {
                contact = new Contact(
                        csvContact.getName(),
                        csvContact.getRole(),
                        csvContact.getMail(),
                        csvContact.getTelephone(),
                        csvContact.getLastContact(),
                        csvContact.getLastAnswer(),
                        csvContact.getComment());
            } else {
                if (companyRepository != null && !companyRepository.existsByName(csvContact.getCompany())) {
                    companyOfContact = new Company(csvContact.getCompany(), Collections.emptyList(), "", "", "", "", "", "", "");
                } else {
                    companyOfContact = companyRepository.findByName(csvContact.getCompany());
                }
                companyRepository.save(companyOfContact);
                contact = new Contact(
                        csvContact.getName(),
                        companyOfContact,
                        csvContact.getRole(),
                        csvContact.getMail(),
                        csvContact.getTelephone(),
                        csvContact.getLastContact(),
                        csvContact.getLastAnswer(),
                        csvContact.getComment());
            }
            contactRepository.save(contact);
        }
    }

    @GetMapping("/get")
    public final ResponseEntity<List<Contact>> getContacts() {
        return ResponseEntity.ok(contactRepository.findAll());
    }
}
