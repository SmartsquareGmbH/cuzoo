package de.smartsquare.cuzoo.customer.todo;

import de.smartsquare.cuzoo.customer.company.Company;
import de.smartsquare.cuzoo.customer.company.CompanyRepository;
import de.smartsquare.cuzoo.user.User;
import de.smartsquare.cuzoo.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/todo")
public class TodoController {
    private final TodoRepository todoRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    @Autowired
    public TodoController(final TodoRepository todoRepository, final CompanyRepository companyRepository,
                          final UserRepository userRepository) {
        this.todoRepository = todoRepository;
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
    }

    @PutMapping("/submit")
    public final ResponseEntity<?> submitTodo(@RequestBody @Valid TodoForm todoForm,
                                              @RequestParam("companyName") String companyName,
                                              BindingResult bindingResult) {
        Optional<User> creator = userRepository.findMaybeByUsername(todoForm.getCreator());
        Optional<Company> company = companyRepository.findMaybeByName(companyName);

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body("Todos benötigen eine Beschreibung, sowie ein Fälligkeits- und ein Erinnerungsdatum!");
        }
        if (!company.isPresent()) {
            return ResponseEntity.badRequest().body("Das angegebene Unternehmen existiert nicht!");
        }
        if (!creator.isPresent()) {
            return ResponseEntity.badRequest().body("Der Ersteller existiert nicht!");
        }

        Todo todo = getOrCreateTodo(todoForm, company.get());

        todo.setCreator(creator.get());
        todo.setCompany(company.get());

        Long todoIdBeforeSaving = todo.getId();

        try {
            todoRepository.save(todo);
        } catch (DataAccessException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (todoIdBeforeSaving == null) {
            return new ResponseEntity<>(HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    private Todo getOrCreateTodo(@RequestBody @Valid TodoForm todoForm, Company company) {
        Optional<Todo> maybeTodo = todoRepository.findById(todoForm.getId());
        Todo todo;

        if (maybeTodo.isPresent()) {
            todo = maybeTodo.get();

            todo.setDescription(todoForm.getDescription());
            todo.setCompany(company);
            todo.setExpiration(todoForm.getExpiration());
            todo.setReminder(todoForm.getReminder());
        } else {
            todo = new Todo(
                    todoForm.getDescription(),
                    company,
                    todoForm.getExpiration(),
                    todoForm.getReminder());
        }

        return todo;
    }

    @PutMapping("/done/{todoId}")
    public final ResponseEntity<?> doneTodo(@PathVariable Long todoId) {
        Optional<Todo> maybeTodo = todoRepository.findById(todoId);

        if (!maybeTodo.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Das Todo wurde nicht gefunden!");
        }

        Todo todo = maybeTodo.get();
        todo.setDone(true);

        try {
            todoRepository.save(todo);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (DataAccessException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get/{companyName}")
    public final ResponseEntity<?> getTodosOfCompany(@PathVariable String companyName) {
        if (!companyRepository.existsByName(companyName)) {
            return ResponseEntity.badRequest().body("Das Unternehmen existiert nicht!");
        }

        return ResponseEntity.ok(todoRepository.findAll()
                .stream()
                .filter(todo -> todo.getCompany().getName().equals(companyName))
                .collect(Collectors.toList()));
    }

    @GetMapping("/get")
    public final ResponseEntity<List<Todo>> getTodos() {
        return ResponseEntity.ok(todoRepository.findAll());
    }
}
