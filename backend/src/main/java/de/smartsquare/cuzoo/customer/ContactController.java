package de.smartsquare.cuzoo.customer;

import de.smartsquare.cuzoo.csv.CSVContact;
import de.smartsquare.cuzoo.csv.CSVImporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
        if (file.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        CSVImporter csvImporter = new CSVImporter();
        InputStream inputFile = new BufferedInputStream(file.getInputStream());

        insertImportedContactsWithMissingCompanies(csvImporter.importFrom(inputFile, CSVContact.class));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/submit/{companyName}")
    public final ResponseEntity<?> submitContact(@RequestBody @Valid Contact contact,
                                                 BindingResult bindingResult, @PathVariable String companyName) {
        if (!companyRepository.existsByName(companyName) || bindingResult.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Company contactsCompany = companyRepository.findByName(companyName);
        contact.setCompany(contactsCompany);

        try {
            contactRepository.save(contact);
        } catch (DataAccessException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if(contact.getId() == null) {
            return new ResponseEntity<>(HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.OK);
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

    void insertImportedContactsWithMissingCompanies(List<CSVContact> importedEntity) {
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
                    companyOfContact = new Company(csvContact.getCompany(), "", "", "", "", "", "");
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
