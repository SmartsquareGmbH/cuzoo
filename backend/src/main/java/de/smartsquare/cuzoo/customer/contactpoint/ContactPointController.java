package de.smartsquare.cuzoo.customer.contactpoint;

import de.smartsquare.cuzoo.customer.company.CompanyRepository;
import de.smartsquare.cuzoo.customer.contact.Contact;
import de.smartsquare.cuzoo.customer.contact.ContactRepository;
import de.smartsquare.cuzoo.customer.label.Label;
import de.smartsquare.cuzoo.customer.label.LabelRepository;
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
    private final LabelRepository labelRepository;

    @Autowired
    public ContactPointController(final ContactPointRepository contactPointRepository,
                                  final ContactRepository contactRepository,
                                  final CompanyRepository companyRepository,
                                  final LabelRepository labelRepository) {
        this.contactPointRepository = contactPointRepository;
        this.contactRepository = contactRepository;
        this.companyRepository = companyRepository;
        this.labelRepository = labelRepository;
    }

    @PutMapping("/submit/{contactName}")
    public final ResponseEntity<?> submitContactPoint(@PathVariable("contactName") String contactName,
                                                      @RequestBody @Valid ContactPointForm contactPointForm,
                                                      BindingResult bindingResult) {
        if (bindingResult.hasErrors() || !contactRepository.existsByName(contactName)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Contact contactPointsContact = contactRepository.findByName(contactName);
        ContactPoint contactPoint;

        if (contactPointRepository.findById(contactPointForm.getId()).isPresent()) {
            contactPoint = contactPointRepository.findById(contactPointForm.getId()).get();

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
            return new ResponseEntity<>(HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.OK);
        }
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
}
