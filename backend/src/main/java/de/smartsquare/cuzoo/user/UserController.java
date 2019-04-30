package de.smartsquare.cuzoo.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserRepository userRepository;

    @Autowired
    public UserController(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PutMapping("/submit/info/{username}")
    public final ResponseEntity<?> submitCompany(@RequestBody UserInformationForm userInformationForm,
                                                 @PathVariable String username) {
        Optional<User> maybeUser = userRepository.findMaybeByUsername(username);
        if (!maybeUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Dieser User wurde nicht gefunden!");
        }

        User user = maybeUser.get();
        user.setFullname(userInformationForm.getFullname());
        user.setMail(userInformationForm.getMail());

        try {
            userRepository.save(user);
        } catch (DataAccessException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping("/get/{username}")
    public final ResponseEntity<?> getUserInformation(@PathVariable String username) {
        Optional<User> maybeUser = userRepository.findMaybeByUsername(username);

        if (!maybeUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Dieser User wurde nicht gefunden!");
        }

        return ResponseEntity.ok(maybeUser.get());
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
