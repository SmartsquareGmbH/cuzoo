package de.smartsquare.cuzoo.customer;

import de.smartsquare.cuzoo.csv.CSVContact;
import de.smartsquare.cuzoo.csv.CSVImporter;
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
@RequestMapping("/contact")
public class ContactController {

    private final ContactRepository contactRepository;

    @Autowired
    public ContactController(final ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @PostMapping("/import")
    public final ResponseEntity<?> postCSV(@RequestParam("file") MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            CSVImporter csvImporter = new CSVImporter();
            InputStream inputFile = new BufferedInputStream(file.getInputStream());

            insertImportedContacts(csvImporter.importFrom(inputFile, CSVContact.class));
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

    void insertImportedContacts(List<CSVContact> importedEntity) {
        for (CSVContact csvContact : importedEntity) {
            Contact contact = new Contact(
                    csvContact.getName(),
                    csvContact.getCompany(),
                    csvContact.getRole(),
                    csvContact.getAddress(),
                    csvContact.getMail(),
                    csvContact.getTelephone(),
                    csvContact.getJug(),
                    csvContact.getCloudLab(),
                    csvContact.getCioDay(),
                    csvContact.getCloudFlyer(),
                    csvContact.getLastContact(),
                    csvContact.getLastAnswer(),
                    csvContact.getComment());

            contactRepository.save(contact);
        }
    }

    @GetMapping("/get")
    public final ResponseEntity<List<Contact>> getContacts() {
        return ResponseEntity.ok(contactRepository.findAll());
    }
}
