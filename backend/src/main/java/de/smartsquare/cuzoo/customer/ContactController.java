package de.smartsquare.cuzoo.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/contact")
public class ContactController {

    private final ContactRepository contactRepository;
    private final CompanyRepository companyRepository;
    private final CSVConverter csvConverter;

    @Autowired
    public ContactController(final ContactRepository contactRepository, final CompanyRepository companyRepository, CSVConverter csvConverter) {
        this.contactRepository = contactRepository;
        this.companyRepository = companyRepository;
        this.csvConverter = csvConverter;
    }

    @PostMapping("/import")
    public final ResponseEntity<?> postCSV(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        List<Contact> insertedContacts = csvConverter.getConvertedContacts(file.getInputStream());

        insertedContacts.forEach(contactRepository::save);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/submit")
    public final ResponseEntity<?> submitContact(@RequestBody @Valid Contact contact, BindingResult bindingResult,
                                                 @RequestParam(required = false, name = "companyName") String maybeCompanyName) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (isFreelancer(maybeCompanyName)) {
            if (!companyRepository.existsByName(maybeCompanyName)) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            Company contactsCompany = companyRepository.findByName(maybeCompanyName);
            contact.setCompany(contactsCompany);
        }

        Long contactIdBeforeSaving = contact.getId();

        try {
            contactRepository.save(contact);
        } catch (DataAccessException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if(contactIdBeforeSaving == null) {
            return new ResponseEntity<>(HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    private boolean isFreelancer(String maybeCompanyName) {
        return maybeCompanyName != null;
    }

    @DeleteMapping("/delete/{contactId}")
    public final ResponseEntity<?> deleteContact(@PathVariable Long contactId) {
        if (!contactRepository.existsById(contactId)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        try {
            contactRepository.deleteById(contactId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get")
    public final ResponseEntity<List<Contact>> getContacts() {
        return ResponseEntity.ok(contactRepository.findAll());
    }
}
