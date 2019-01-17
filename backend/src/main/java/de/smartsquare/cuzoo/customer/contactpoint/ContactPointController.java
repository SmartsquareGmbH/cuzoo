package de.smartsquare.cuzoo.customer.contactpoint;

import de.smartsquare.cuzoo.customer.company.CompanyRepository;
import de.smartsquare.cuzoo.customer.contact.Contact;
import de.smartsquare.cuzoo.customer.contact.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/point")
public class ContactPointController {
    private final ContactPointRepository contactPointRepository;
    private final ContactRepository contactRepository;
    private final CompanyRepository companyRepository;

    @Autowired
    public ContactPointController(final ContactPointRepository contactPointRepository,
                                  final ContactRepository contactRepository, final CompanyRepository companyRepository) {
        this.contactPointRepository = contactPointRepository;
        this.contactRepository = contactRepository;
        this.companyRepository = companyRepository;
    }

    @PutMapping("/submit")
    public final ResponseEntity<?> submitContactPoint(@RequestBody @Valid ContactPoint contactPoint,
                                                      @RequestParam("contactName") String contactName,
                                                      BindingResult bindingResult) {
        if (bindingResult.hasErrors() || !contactRepository.existsByName(contactName)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Contact contactPointsContact = contactRepository.findByName(contactName);
        contactPoint.setContact(contactPointsContact);

        Long contactPointIdBeforeSaving = contactPoint.getId();

        try {
            contactPointRepository.save(contactPoint);
        } catch (DataAccessException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (contactPointIdBeforeSaving == null) {
            return new ResponseEntity<>(HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @DeleteMapping("/delete/{contactPointId}")
    public final ResponseEntity<?> deleteContactPoint(@PathVariable Long contactPointId) {
        if (!contactPointRepository.existsById(contactPointId)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        try {
            contactPointRepository.deleteById(contactPointId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get")
    public final ResponseEntity<List<ContactPoint>> getAllContactPoints() {
        return ResponseEntity.ok(contactPointRepository.findAll());
    }

    @GetMapping("/get/{companyName}")
    public final ResponseEntity<List<ContactPoint>> getContactPointsOfCompany(@PathVariable String companyName) {
        if (!companyRepository.existsByName(companyName)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(contactPointRepository.findAll()
                .stream()
                .filter(contactPoint -> contactPoint.getContact().getCompany().getName().equals(companyName))
                .collect(Collectors.toList()));
    }
}
