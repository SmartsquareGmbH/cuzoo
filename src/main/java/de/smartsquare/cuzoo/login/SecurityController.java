package de.smartsquare.cuzoo.login;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/security")
public class SecurityController {

    @RequestMapping("/login")
    public String checkAuthorization() {
        return "OK";
    }

}
