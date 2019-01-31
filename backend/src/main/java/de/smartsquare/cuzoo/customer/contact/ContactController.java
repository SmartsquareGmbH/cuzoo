package de.smartsquare.cuzoo.customer.contact;

import de.smartsquare.cuzoo.customer.CSVConverter;
import de.smartsquare.cuzoo.customer.company.Company;
import de.smartsquare.cuzoo.customer.company.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/contact")
public class ContactController {

    private final ContactRepository contactRepository;
    private final CompanyRepository companyRepository;
    private final CSVConverter csvConverter;
    private final ContactExporter contactExporter;

    @Autowired
    public ContactController(final ContactRepository contactRepository, final CompanyRepository companyRepository, CSVConverter csvConverter) {
        this.contactRepository = contactRepository;
        this.companyRepository = companyRepository;
        this.csvConverter = csvConverter;
        this.contactExporter = new ContactExporter();
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

    @GetMapping(value = "/download/{contactId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public final ResponseEntity<byte[]> getContactInformation(@PathVariable Long contactId) throws IOException {
        if (!contactRepository.findById(contactId).isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Contact contact = contactRepository.findById(contactId).get();
        InputStream inputStream = Files.newInputStream(contactExporter.exportContactToTxt(contact));

        try {
            return ResponseEntity.ok(inputStream.readAllBytes());
        } finally {
            Files.delete(Paths.get("src/main/resources/" +
                    contact.getName()
                            .replace(' ', '_')
                            .toLowerCase() + ".txt"));
        }
    }

    @GetMapping("/get")
    public final ResponseEntity<List<Contact>> getContacts() {
        return ResponseEntity.ok(contactRepository.findAll());
    }
}
