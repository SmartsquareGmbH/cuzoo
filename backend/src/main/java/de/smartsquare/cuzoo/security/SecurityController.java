package de.smartsquare.cuzoo.security;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/security")
public class SecurityController {

    @RequestMapping("/login")
    public ResponseEntity<?> checkAuthorization() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
