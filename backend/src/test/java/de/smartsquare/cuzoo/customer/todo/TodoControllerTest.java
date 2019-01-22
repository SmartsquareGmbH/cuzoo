package de.smartsquare.cuzoo.customer.todo;

import de.smartsquare.cuzoo.customer.company.Company;
import de.smartsquare.cuzoo.customer.company.CompanyRepository;
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
    private TodoRepository todoRepository;
    @Autowired
    private CompanyRepository companyRepository;
    private Company company;


    @Before
    public void initialize() {
        company = new Company("Smartsquare", "", "", "", "", "", "");
        companyRepository.save(company);
    }

    @After
    public void tearDown() throws Exception {
        todoRepository.deleteAll();
    }

    @Test
    public void that_todo_is_getting_registered() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put("/api/todo/submit?companyName=" + company.getName())
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

    private String getTodoInJson() {
        return "{\"description\":\"Muell rausbringen\", \"expiration\":\"0\", \"reminder\":\"0\"}";
    }

}
