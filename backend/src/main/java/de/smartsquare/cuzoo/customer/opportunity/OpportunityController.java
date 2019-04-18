package de.smartsquare.cuzoo.customer.opportunity;

import de.smartsquare.cuzoo.customer.company.CompanyRepository;
import de.smartsquare.cuzoo.customer.contactpoint.ContactPoint;
import de.smartsquare.cuzoo.customer.contactpoint.ContactPointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/opportunity")
public class OpportunityController {
    private final OpportunityRepository opportunityRepository;
    private final ContactPointRepository contactPointRepository;
    private final CompanyRepository companyRepository;

    @Autowired
    public OpportunityController(final ContactPointRepository contactPointRepository,
                                 final OpportunityRepository opportunityRepository,
                                 final CompanyRepository companyRepository) {
        this.opportunityRepository = opportunityRepository;
        this.contactPointRepository = contactPointRepository;
        this.companyRepository = companyRepository;
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

            savedOpportunity.setLastProgress(
                    contactPointRepository
                            .findFirstByOpportunityIdOrderByDateDesc(savedOpportunity.getId())
                            .getDate()
            );

            opportunityRepository.save(savedOpportunity);
        } catch (DataAccessException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (opportunityIdBeforeSaving < 1) {
            return new ResponseEntity<>(HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @PutMapping("/submit/progress/{opportunityId}")
    public final ResponseEntity<?> submitOpportunityProgress(@PathVariable("opportunityId") Long opportunityId,
                                                             @RequestBody @Valid ProgressForm progressForm,
                                                             BindingResult bindingResult) {
        Optional<Opportunity> maybeOpportunity = opportunityRepository.findById(opportunityId);

        System.out.println(progressForm.getProgressText() + progressForm.getOpportunityState());

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body("Fortschritte von Opportunities benötigen einen Status");
        }
        if (!maybeOpportunity.isPresent()) {
            return ResponseEntity.badRequest().body("Diese Opportunity existiert nicht!");
        }

        Opportunity opportunity = maybeOpportunity.get();
        Opportunity.Progress progress = new Opportunity.Progress(progressForm.getProgressText(), progressForm.getOpportunityState());

        Long opportunityIdBeforeSaving = opportunity.getId();

        try {
            opportunity.addProgress(progress);
            opportunityRepository.save(opportunity);
        } catch (DataAccessException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (opportunityIdBeforeSaving < 1) {
            return new ResponseEntity<>(HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @DeleteMapping("/delete/{opportunityId}")
    public final ResponseEntity<?> deleteOpportunity(@PathVariable Long opportunityId) {
        if (!opportunityRepository.existsById(opportunityId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Diese Opportunity wurde nicht gefunden!");
        }

        try {
            opportunityRepository.deleteById(opportunityId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get/list/{companyId}")
    public final ResponseEntity<?> getOpportunitiesOfCompany(@PathVariable Long companyId) {
        if (!companyRepository.existsById(companyId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Dieses Unternehmen wurde nicht gefunden!");
        }

        return ResponseEntity.ok(contactPointRepository.findAllOpportunitiesOfCompany(companyId));
    }

    @GetMapping("/get")
    public final ResponseEntity<List<Opportunity>> getAllOpportunities() {
        return ResponseEntity.ok(opportunityRepository.findAll());
    }
}
