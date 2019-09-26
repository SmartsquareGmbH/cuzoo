package de.smartsquare.cuzoo.customer.todo;

import de.smartsquare.cuzoo.customer.company.Company;
import de.smartsquare.cuzoo.customer.company.CompanyRepository;
import de.smartsquare.cuzoo.user.User;
import de.smartsquare.cuzoo.user.UserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "drs", password = "secure")
public class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    private User creator;
    @Autowired
    private TodoRepository todoRepository;
    @Autowired
    private CompanyRepository companyRepository;
    private Company company;


    @Before
    public void initialize() {
        userRepository.deleteAll();
        creator = new User("user", "", "", "");
        userRepository.save(creator);

        company = new Company("Smartsquare", "", "", "", "", "", "");
        company = companyRepository.save(company);
    }

    @After
    public void tearDown() throws Exception {
        companyRepository.deleteAll();
        userRepository.deleteAll();
        todoRepository.deleteAll();
    }

    @Test
    public void that_todo_is_getting_registered() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put("/api/todo/submit/" + company.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(getTodoInJson());

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isCreated())
                .andDo(MockMvcResultHandlers.print());

        assertThat(todoRepository.findAll()
                .stream()
                .anyMatch(todo -> todo.getDescription()
                        .equals("Muell rausbringen")))
                .isTrue();
    }

    @Test
    public void that_todo_is_getting_updated() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put("/api/todo/submit/" + company.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(getTodoInJson());

        this.mockMvc.perform(builder);

        Todo outdatedTodo = todoRepository.findAll()
                .stream()
                .filter(todo -> todo.getDescription().equals("Muell rausbringen"))
                .findFirst().orElse(null);

        MockHttpServletRequestBuilder updateBuilder =
                MockMvcRequestBuilders.put("/api/todo/submit/" + company.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(getUpdatedTodoInJson(outdatedTodo.getId()));

        this.mockMvc.perform(updateBuilder)
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andDo(MockMvcResultHandlers.print());

        assertThat(todoRepository
                .findById(outdatedTodo.getId()).get()
                .getDescription().equals("Muelltonnen an die Strasse stellen"))
                .isTrue();
    }

    private String getUpdatedTodoInJson(Long id) {
        return "{\"description\":\"Muelltonnen an die Strasse stellen\", \"expiration\":\"0\", \"id\":\"" + id + "\", \"reminder\":\"0\", \"creator\":\"user\"}";
    }

    @Test
    public void that_todo_gets_marked_as_done() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put("/api/todo/submit/" + company.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(getUndoneTodoInJson());

        this.mockMvc.perform(builder);

        Todo undoneTodo = todoRepository.findAll()
                .stream()
                .filter(todo -> todo.getDescription().equals("Foo Bar"))
                .findFirst().orElse(null);

        MockHttpServletRequestBuilder doneBuilder =
                MockMvcRequestBuilders.put("/api/todo/done/" + undoneTodo.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8");

        this.mockMvc.perform(doneBuilder)
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andDo(MockMvcResultHandlers.print());

        assertThat(todoRepository
                .findAll()
                .stream()
                .map(Todo::getDescription))
                .containsOnly("Foo Bar");
    }

    private String getUndoneTodoInJson() {
        return "{\"description\":\"Foo Bar\", \"expiration\":\"0\", \"id\":\"1\", \"reminder\":\"0\", \"creator\":\"user\"}";
    }

    @Test
    public void that_marking_non_existing_todo_is_not_found() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put("/api/todo/done/0")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8");

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isNotFound())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void that_submitting_todo_with_invalid_company_is_bad_request() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put("/api/todo/submit/1337")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(getTodoInJson());

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void that_getting_todo_with_invalid_company_is_bad_request() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.get("/api/todo/get/FooBarGmbH")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(getTodoInJson());

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void that_submitting_todo_with_invalid_binding_result_is_bad_request() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put("/api/todo/submit/" + company.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(getInvalidTodoInJson());

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    private String getInvalidTodoInJson() {
        return "{\"id\":\"0\", \"reminder\":\"0\", \"creator\":\"user\", \"description\":\"ABC\"}";
    }

    @Test
    public void that_getting_todos_of_company_succeeds() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put("/api/todo/submit" + company.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(getTodoInJson());

        MockHttpServletRequestBuilder getBuilder =
                MockMvcRequestBuilders.get("/api/todo/get/" + company.getName())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8");

        this.mockMvc.perform(builder);
        this.mockMvc.perform(getBuilder)
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    private String getTodoInJson() {
        return "{\"description\":\"Muell rausbringen\", \"expiration\":\"0\", \"id\":\"0\", \"reminder\":\"0\", \"creator\":\"user\"}";
    }

    @Test
    public void that_submitting_todo_with_invalid_user_is_bad_request() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put("/api/todo/submit/" + company.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(getTodoWithInvalidCreatorInJson());

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    private String getTodoWithInvalidCreatorInJson() {
        return "{\"description\":\"Muell rausbringen\", \"expiration\":\"0\", \"id\":\"0\", \"reminder\":\"0\", \"creator\":\"invalid user\"}";
    }

    @Test
    public void that_getting_todos_is_successfully() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put("/api/todo/submit/" + company.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(getTodoInJson());

        this.mockMvc.perform(builder);

        MockHttpServletRequestBuilder getBuilder =
                MockMvcRequestBuilders.get("/api/todo/get")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8");

        MvcResult result = this.mockMvc.perform(getBuilder).andReturn();
        String response = result.getResponse().getContentAsString();

        assertThat(response).contains("Muell rausbringen");
    }
}
