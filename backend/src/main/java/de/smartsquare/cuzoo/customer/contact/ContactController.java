package de.smartsquare.cuzoo.customer.contact;

import de.smartsquare.cuzoo.customer.CSVConverter;
import de.smartsquare.cuzoo.customer.company.Company;
import de.smartsquare.cuzoo.customer.company.CompanyRepository;
import de.smartsquare.cuzoo.customer.contactpoint.ContactPointRepository;
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
@RequestMapping("/api/contact")
public class ContactController {

    private final ContactRepository contactRepository;
    private final CompanyRepository companyRepository;
    private final ContactPointRepository contactPointRepository;
    private final LabelRepository labelRepository;

    private final CSVConverter csvConverter;
    private final ContactExporter contactExporter;

    @Autowired
    public ContactController(final ContactRepository contactRepository, final CompanyRepository companyRepository,
                             final ContactPointRepository contactPointRepository, final LabelRepository labelRepository,
                             CSVConverter csvConverter) {
        this.contactRepository = contactRepository;
        this.companyRepository = companyRepository;
        this.contactPointRepository = contactPointRepository;
        this.labelRepository = labelRepository;

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
    public final ResponseEntity<?> submitContact(@RequestBody @Valid ContactForm contactForm, BindingResult bindingResult,
                                                 @RequestParam(required = false, name = "companyName") String maybeCompanyName) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Contact contact = getOrCreateContact(contactForm);

        if (hasNoCompany(maybeCompanyName)) {
            if (!companyRepository.existsByName(maybeCompanyName)) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            Company contactsCompany = companyRepository.findByName(maybeCompanyName);
            contact.setCompany(contactsCompany);
        }

        Long contactIdBeforeSaving = contact.getId();

        try {
            if (contactForm.getLabels() != null) {
                submitLabels(contactForm.getLabels(), contact);
            }

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

    private Contact getOrCreateContact(@RequestBody @Valid ContactForm contactForm) {
        Contact contact;
        Optional<Contact> byId = contactRepository.findById(contactForm.getId());

        if (byId.isPresent()) {
            contact = byId.get();

            contact.setName(contactForm.getName());
            contact.setRole(contactForm.getRole());
            contact.setMail(contactForm.getMail());
            contact.setTelephone(contactForm.getTelephone());
            contact.setMobile(contactForm.getMobile());
            contact.setComment(contactForm.getComment());
        } else {
            contact = new Contact(
                    contactForm.getName(),
                    contactForm.getRole(),
                    contactForm.getMail(),
                    contactForm.getTelephone(),
                    contactForm.getMobile(),
                    contactForm.getComment());
        }

        return contact;
    }

    private void submitLabels(List<String> titles, Contact contact) {
        List<Label> companyLabels = titles.stream()
                .map(title -> new Tuple(labelRepository.findByTitle(title), title))
                .map(tuple -> tuple.optionalLabel.orElse(new Label(tuple.title)))
                .map(label -> label.addContact(contact))
                .collect(Collectors.toList());

        contact.setLabels(companyLabels);
    }

    class Tuple {
        final Optional<Label> optionalLabel;
        final String title;

        Tuple(Optional<Label> optionalLabel, String title) {
            this.optionalLabel = optionalLabel;
            this.title = title;
        }
    }

    private boolean hasNoCompany(String maybeCompanyName) {
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

    @GetMapping("/get/labels/{input}")
    public final ResponseEntity<List<String>> getContactPointLabelsWithInput(@PathVariable String input) {
        return ResponseEntity.ok(labelRepository
                .findAllOfContactByPartOfTitle(input)
                .stream()
                .map(Label::getTitle)
                .collect(Collectors.toList()));
    }
}
