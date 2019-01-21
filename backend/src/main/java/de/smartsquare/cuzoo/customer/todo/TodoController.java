package de.smartsquare.cuzoo.customer.todo;

import de.smartsquare.cuzoo.customer.company.Company;
import de.smartsquare.cuzoo.customer.company.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/todo")
public class TodoController {
    private final TodoRepository todoRepository;
    private final CompanyRepository companyRepository;

    @Autowired
    public TodoController(final TodoRepository todoRepository, final CompanyRepository companyRepository) {
        this.todoRepository = todoRepository;
        this.companyRepository = companyRepository;
    }

    @PutMapping("/submit")
    public final ResponseEntity<?> submitTodo(@RequestBody @Valid Todo todo,
                                              @RequestParam("companyName") String companyName,
                                              BindingResult bindingResult) {
        if (bindingResult.hasErrors() || !companyRepository.existsByName(companyName)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Company companyOfTodo = companyRepository.findByName(companyName);
        todo.setCompany(companyOfTodo);

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
}