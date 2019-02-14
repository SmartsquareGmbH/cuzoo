package de.smartsquare.cuzoo.customer.contact;

import de.smartsquare.cuzoo.customer.CSVConverter;
import de.smartsquare.cuzoo.customer.company.Company;
import de.smartsquare.cuzoo.customer.company.CompanyRepository;
import de.smartsquare.cuzoo.customer.contactpoint.ContactPointRepository;
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

@RestController
@RequestMapping("/api/contact")
public class ContactController {

    private final ContactRepository contactRepository;
    private final CompanyRepository companyRepository;
    private final ContactPointRepository contactPointRepository;

    private final CSVConverter csvConverter;
    private final ContactExporter contactExporter;

    @Autowired
    public ContactController(final ContactRepository contactRepository, final CompanyRepository companyRepository,
                             final ContactPointRepository contactPointRepository, CSVConverter csvConverter) {
        this.contactRepository = contactRepository;
        this.companyRepository = companyRepository;
        this.contactPointRepository = contactPointRepository;
        this.csvConverter = csvConverter;

        contactExporter = new ContactExporter();
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

        if (contactIdBeforeSaving == null) {
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
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            contactRepository.deleteById(contactId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/download/{contactId}")
    public final ResponseEntity<String> getContactInformation(@PathVariable Long contactId) {
        if (!contactRepository.findById(contactId).isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Contact contact = contactRepository.findById(contactId).get();

        return ResponseEntity
                .ok(String.join("\n", contactExporter.getContactContent(contact, this.contactPointRepository)));
    }

    @GetMapping("/get")
    public final ResponseEntity<List<Contact>> getContacts() {
        return ResponseEntity.ok(contactRepository.findAll());
    }
}
