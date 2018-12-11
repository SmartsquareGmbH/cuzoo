package de.smartsquare.cuzoo.customer.points;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/point")
public class CPointController {
    private final CPointRepository cPointRepository;

    @Autowired
    public CPointController(final CPointRepository cPointRepository) {
        this.cPointRepository = cPointRepository;
    }

    @PutMapping("/submit")
    public final ResponseEntity<?> submitContactPoint(@RequestBody @Valid CPoint cPoint,
                                                      BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

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

}
