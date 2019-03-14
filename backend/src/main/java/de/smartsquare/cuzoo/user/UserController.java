package de.smartsquare.cuzoo.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserRepository userRepository;

    @Autowired
    public UserController(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @RequestMapping("/get/usernames")
    public final ResponseEntity<List<String>> getUsernames() {
        return ResponseEntity.ok(userRepository
                .findAll()
                .stream()
                .map(User::getUsername)
                .collect(Collectors.toList()));
    }
}
