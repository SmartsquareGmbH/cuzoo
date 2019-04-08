package de.smartsquare.cuzoo.customer.opportunity;

import de.smartsquare.cuzoo.customer.contactpoint.ContactPoint;
import de.smartsquare.cuzoo.customer.contactpoint.ContactPointRepository;
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

@RestController
@RequestMapping("/api/opportunity")
public class OpportunityController {
    private final OpportunityRepository opportunityRepository;
    private final ContactPointRepository contactPointRepository;

    @Autowired
    public OpportunityController(final ContactPointRepository contactPointRepository,
                                 final OpportunityRepository opportunityRepository) {
        this.opportunityRepository = opportunityRepository;
        this.contactPointRepository = contactPointRepository;
    }

    @PutMapping("/submit/{contactPointId}")
    public final ResponseEntity<?> submitOpportunity(@PathVariable("contactPointId") Long contactPointId,
                                                     @RequestBody @Valid Opportunity opportunity,
                                                     BindingResult bindingResult) {
        Optional<ContactPoint> maybeContactPoint = contactPointRepository.findById(contactPointId);

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body("Opportunities benötigen einen Titel und einen Status");
        }
        if (!maybeContactPoint.isPresent()) {
            return ResponseEntity.badRequest().body("Der Kontaktpunkt existiert nicht!");
        }

        ContactPoint contactPoint = maybeContactPoint.get();

        Long opportunityIdBeforeSaving = opportunity.getId();

        try {
            Opportunity savedOpportunity = opportunityRepository.save(opportunity);

            contactPoint.setOpportunity(savedOpportunity);
            contactPointRepository.save(contactPoint);
        } catch (DataAccessException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (opportunityIdBeforeSaving < 1) {
            return new ResponseEntity<>(HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @GetMapping("/get/{companyName}")
    public final ResponseEntity<List<Opportunity>> getOpportunitiesOfCompany(@PathVariable String companyName) {
        return ResponseEntity.ok(
                new ArrayList<>(
                        contactPointRepository.findAllOpportunitiesOfCompany(companyName)
                )
        );
    }

    @GetMapping("/get")
    public final ResponseEntity<List<Opportunity>> getAllOpportunities() {
        return ResponseEntity.ok(opportunityRepository.findAll());
    }
}
