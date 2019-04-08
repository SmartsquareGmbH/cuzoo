package de.smartsquare.cuzoo.customer.contactpoint;

import de.smartsquare.cuzoo.customer.company.CompanyRepository;
import de.smartsquare.cuzoo.customer.contact.Contact;
import de.smartsquare.cuzoo.customer.contact.ContactRepository;
import de.smartsquare.cuzoo.customer.label.Label;
import de.smartsquare.cuzoo.customer.label.LabelRepository;
import de.smartsquare.cuzoo.customer.opportunity.OpportunityRepository;
import de.smartsquare.cuzoo.user.User;
import de.smartsquare.cuzoo.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/point")
public class ContactPointController {
    private final ContactPointRepository contactPointRepository;
    private final ContactRepository contactRepository;
    private final CompanyRepository companyRepository;
    private final OpportunityRepository opportunityRepository;
    private final UserRepository userRepository;
    private final LabelRepository labelRepository;

    @Autowired
    public ContactPointController(final ContactPointRepository contactPointRepository,
                                  final OpportunityRepository opportunityRepository,
                                  final ContactRepository contactRepository, final LabelRepository labelRepository,
                                  final CompanyRepository companyRepository, final UserRepository userRepository) {
        this.userRepository = userRepository;
        this.contactPointRepository = contactPointRepository;
        this.opportunityRepository = opportunityRepository;
        this.contactRepository = contactRepository;
        this.companyRepository = companyRepository;
        this.labelRepository = labelRepository;
    }

    @PutMapping("/submit/{contactName}")
    public final ResponseEntity<?> submitContactPoint(@PathVariable("contactName") String contactName,
                                                      @RequestBody @Valid ContactPointForm contactPointForm,
                                                      BindingResult bindingResult) {
        Optional<User> creator = userRepository.findMaybeByUsername(contactPointForm.getCreator());
        Optional<Contact> contact = contactRepository.findMaybeByName(contactName);

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body("Kontaktpunkte benötigt einen Titel und ein Datum!");
        }
        if (!contact.isPresent()) {
            return ResponseEntity.badRequest().body("Der angegebene Ansprechpartner existiert nicht!");
        }
        if (!creator.isPresent()) {
            return ResponseEntity.badRequest().body("Der Ersteller existiert nicht!");
        }

        ContactPoint contactPoint = getOrCreateContactPoint(contactPointForm, contact.get());

        contactPoint.setCreator(creator.get());
        Long contactPointIdBeforeSaving = contactPointForm.getId();

        try {
            if (contactPointForm.getTypes() != null) {
                submitLabels(contactPointForm.getTypes(), contactPoint, true);
            }

            if (contactPointForm.getLabels() != null) {
                submitLabels(contactPointForm.getLabels(), contactPoint, false);
            }

            contactPointRepository.save(contactPoint);
            labelRepository.deleteAllReferenceless();
        } catch (DataAccessException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (contactPointIdBeforeSaving < 1) {
            return new ResponseEntity<Long>(contactPoint.getId(), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<Long>(contactPoint.getId(), HttpStatus.OK);
        }
    }

    private ContactPoint getOrCreateContactPoint(@RequestBody @Valid ContactPointForm contactPointForm, Contact contactPointsContact) {
        Optional<ContactPoint> maybeContactPoint = contactPointRepository.findById(contactPointForm.getId());
        ContactPoint contactPoint;

        if (maybeContactPoint.isPresent()) {
            contactPoint = maybeContactPoint.get();

            contactPoint.setTitle(contactPointForm.getTitle());
            contactPoint.setDate(contactPointForm.getDate());
            contactPoint.setContact(contactPointsContact);
            contactPoint.setComment(contactPointForm.getComment());
        } else {
            contactPoint = new ContactPoint(
                    contactPointForm.getTitle(),
                    contactPointForm.getDate(),
                    contactPointsContact,
                    contactPointForm.getComment());
        }
        return contactPoint;
    }

    private void submitLabels(List<String> titles, ContactPoint contactPoint, boolean mediaTypes) {
        List<Label> contactPointLabels = new ArrayList<>();

        titles.forEach(title -> {
            Optional<Label> label = labelRepository.findByTitle(title);

            if (label.isPresent()) {
                if (mediaTypes) {
                    label.get().addContactPointTypes(contactPoint);
                } else {
                    label.get().addContactPointWithLabel(contactPoint);
                }

                contactPointLabels.add(label.get());
            } else {
                Label labelToSave = new Label(title);

                if (mediaTypes) {
                    labelToSave.addContactPointTypes(contactPoint);
                } else {
                    labelToSave.addContactPointWithLabel(contactPoint);
                }

                contactPointLabels.add(labelToSave);
            }
        });

        if (mediaTypes) {
            contactPoint.setTypes(contactPointLabels);
        } else {
            contactPoint.setLabels(contactPointLabels);
        }
    }

    @DeleteMapping("/delete/{contactPointId}")
    public final ResponseEntity<?> deleteContactPoint(@PathVariable Long contactPointId) {
        if (!contactPointRepository.existsById(contactPointId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Der Ansprechpartner wurde nicht gefunden!");
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
    public final ResponseEntity<?> getContactPointsOfCompany(@PathVariable String companyName) {
        if (!companyRepository.existsByName(companyName)) {
            return ResponseEntity.badRequest().body("Das Unternehmen existiert nicht!");
        }

        return ResponseEntity.ok(contactPointRepository
                .findAll()
                .stream()
                .filter(contactPoint -> contactPoint.getContact().getCompany().getName().equals(companyName))
                .collect(Collectors.toList()));
    }

    @GetMapping("/get/labels/{input}")
    public final ResponseEntity<List<String>> getContactPointLabelsWithInput(@PathVariable String input) {
        return ResponseEntity.ok(labelRepository
                .findAllOfContactPointLabelsByPartOfTitle(input)
                .stream()
                .map(Label::getTitle)
                .collect(Collectors.toList()));
    }

    @GetMapping("/get/types/{input}")
    public final ResponseEntity<List<String>> getContactPointTypesWithInput(@PathVariable String input) {
        return ResponseEntity.ok(labelRepository
                .findAllOfContactPointTypesByPartOfTitle(input)
                .stream()
                .map(Label::getTitle)
                .collect(Collectors.toList()));
    }

    @GetMapping("/get/opportunity/{opportunityId}")
    public final ResponseEntity<?> getContactPointsOfOpportunity(@PathVariable Long opportunityId) {
        if (!opportunityRepository.existsById(opportunityId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Diese Opportunity wurde nicht gefunden!");
        }

        return ResponseEntity.ok(
                new ArrayList<>(
                        contactPointRepository.findAllContactPointsOfOpportunity(opportunityId)
                )
        );
    }

}
