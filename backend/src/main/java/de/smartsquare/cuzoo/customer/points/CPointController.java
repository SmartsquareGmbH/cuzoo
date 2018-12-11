package de.smartsquare.cuzoo.customer.points;

import de.smartsquare.cuzoo.customer.contact.Contact;
import de.smartsquare.cuzoo.customer.contact.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/point")
public class CPointController {
    private final CPointRepository cPointRepository;
    private final ContactRepository contactRepository;

    @Autowired
    public CPointController(final CPointRepository cPointRepository, final ContactRepository contactRepository) {
        this.cPointRepository = cPointRepository;
        this.contactRepository = contactRepository;
    }

    @PutMapping("/submit")
    public final ResponseEntity<?> submitContactPoint(@RequestBody @Valid CPoint cPoint,
                                                      @RequestParam(name = "contactName") String contactName,
                                                      BindingResult bindingResult) {
        if (bindingResult.hasErrors() || !contactRepository.existsByName(contactName)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Contact cPointsContact = contactRepository.findByName(contactName);
        cPoint.setContact(cPointsContact);

        Long cPointIdBeforeSaving = cPoint.getId();

        try {
            cPointRepository.save(cPoint);
        } catch (DataAccessException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (cPointIdBeforeSaving == null) {
            return new ResponseEntity<>(HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @DeleteMapping("/delete/{cPointId}")
    public final ResponseEntity<?> deleteContactPoint(@PathVariable Long cPointId) {
        if (!cPointRepository.existsById(cPointId)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        try {
            cPointRepository.deleteById(cPointId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get")
    public final ResponseEntity<List<CPoint>> getContacts() {
        return ResponseEntity.ok(cPointRepository.findAll());
    }
}
