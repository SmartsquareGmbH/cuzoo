package de.smartsquare.cuzoo.customer;

import de.smartsquare.cuzoo.csv.CSVContact;
import de.smartsquare.cuzoo.csv.CSVImporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    void insertImportedContacts(List<CSVContact> importedEntity) {
        for (CSVContact csvContact : importedEntity) {
            Contact contact = new Contact(
                    csvContact.getName(),
                    csvContact.getCompany(),
                    csvContact.getRole(),
                    csvContact.getAddress(),
                    csvContact.getMail(),
                    csvContact.getTelephone(),
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
