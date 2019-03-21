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
    public final ResponseEntity<?> submitContactPoint(@PathVariable("contactPointId") Long contactPointId,
                                                      @RequestBody @Valid Opportunity opportunity,
                                                      BindingResult bindingResult) {
        Optional<ContactPoint> contactPoint = contactPointRepository.findById(contactPointId);

        if (bindingResult.hasErrors() || !contactPoint.isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        opportunity.addContactPoint(contactPoint.get());
        Long opportunityIdBeforeSaving = opportunity.getId();

        try {
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
}
