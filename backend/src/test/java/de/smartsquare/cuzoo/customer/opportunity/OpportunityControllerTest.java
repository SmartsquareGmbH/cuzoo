package de.smartsquare.cuzoo.customer.opportunity;

import de.smartsquare.cuzoo.customer.company.Company;
import de.smartsquare.cuzoo.customer.company.CompanyRepository;
import de.smartsquare.cuzoo.customer.contact.Contact;
import de.smartsquare.cuzoo.customer.contact.ContactRepository;
import de.smartsquare.cuzoo.customer.contactpoint.ContactPoint;
import de.smartsquare.cuzoo.customer.contactpoint.ContactPointRepository;
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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "drs", password = "secure")
public class OpportunityControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private OpportunityRepository opportunityRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ContactPointRepository contactPointRepository;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private ContactRepository contactRepository;

    private User user;
    private Company company;
    private Contact contact;
    private ContactPoint contactPoint;

    @After
    public void tearDown() throws Exception {
        opportunityRepository.deleteAll();
        userRepository.deleteAll();
        contactPointRepository.deleteAll();
        companyRepository.deleteAll();
        contactRepository.deleteAll();
    }

    @Before
    public void initialize() throws Exception {
        user = new User("user", "", "", "");
        company = new Company("Smartsquare GmbH", "", "", "", "", "", "");
        contact = new Contact("Darius", "", "", "", "", "");

        userRepository.save(user);
        companyRepository.save(company);
        contact.setCompany(company);
        contact.setManager(user);
        contactRepository.save(contact);

        contactPoint = new ContactPoint("Beratung", 0L, contact, "JD$UTF%N&~", "", "");
        contactPoint.setCreator(user);
        contactPointRepository.save(contactPoint);
    }

    @Test
    public void that_opportunity_is_getting_registered() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put("/api/opportunity/submit/" + contactPoint.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(getOpportunityInJson());

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isCreated())
                .andDo(MockMvcResultHandlers.print());

        assertThat(opportunityRepository.findAll()
                .stream()
                .map(Opportunity::getTitle))
                .containsOnly("Moeglichkeit");
    }

    @Test
    public void that_submitting_opportunity_for_non_existing_contact_point_is_bad_request() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put("/api/opportunity/submit/1337")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(getOpportunityInJson());

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    private String getOpportunityInJson() {
        return "{\"id\":\"0\", \"title\":\"Moeglichkeit\", \"state\":\"Prospect\"}";
    }
}
