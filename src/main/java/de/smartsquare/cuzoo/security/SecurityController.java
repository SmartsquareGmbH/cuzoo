package de.smartsquare.cuzoo.security;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/security")
public class SecurityController {

    @RequestMapping("/login")
    public String checkAuthorization() {
        return "LOGIN SUCCEEDED";
    }

}
