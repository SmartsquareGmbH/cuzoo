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
    private Opportunity opportunity;

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
        userRepository.save(user);
        companyRepository.save(company);

        contact = new Contact("Darius", company, "", "", "", "", "");
        contact.setManager(user);
        contactRepository.save(contact);

        opportunity = new Opportunity("CUZOO", "Quote", "over 9000");
        opportunityRepository.save(opportunity);

        contactPoint = new ContactPoint("Beratung", 0L, contact, "JD$UTF%N&~", "", "");
        contactPoint.setCreator(user);
        contactPoint.setOpportunity(opportunity);
        contactPointRepository.save(contactPoint);
    }

    @Test
    public void that_opportunity_is_getting_registered() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put("/api/opportunity/submit/contactpoint/" + contactPoint.getId())
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
                .contains("Moeglichkeit");
    }

    @Test
    public void that_opportunity_is_getting_updated() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put("/api/opportunity/submit/contactpoint/" + contactPoint.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(getOpportunityInJson());

        MvcResult result = this.mockMvc.perform(builder).andReturn();
        String id = result.getResponse().getContentAsString();

        MockHttpServletRequestBuilder updatedBuilder =
                MockMvcRequestBuilders.put("/api/opportunity/submit/contactpoint/" + contactPoint.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(getUpdatedOpportunityInJson(id));

        this.mockMvc.perform(updatedBuilder)
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    private String getUpdatedOpportunityInJson(String id) {
        return "{\"id\":\"" + id + "\", \"title\":\"Moeglichkeit\", \"state\":\"Quote\"}";
    }

    @Test
    public void that_submitting_opportunity_for_non_existing_contact_point_is_bad_request() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put("/api/opportunity/submit/contactpoint/1337")
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

    @Test
    public void that_submitting_invalid_opportunity_is_bad_request() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put("/api/opportunity/submit/contactpoint/" + contactPoint.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(getInvalidOpportunityInJson());

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    private String getInvalidOpportunityInJson() {
        return "{\"id\":\"0\", \"title\":\"\", \"state\":\"\"}";
    }

    @Test
    public void that_submitting_progress_is_successfully() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put("/api/opportunity/submit/progress/" + opportunity.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(getProgressInJson());

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isCreated())
                .andDo(MockMvcResultHandlers.print());

        assertThat(opportunityRepository
                .findAll()
                .stream()
                .map(Opportunity::getState))
                .containsOnly("Win");
    }

    private String getProgressInJson() {
        return "{\"opportunityState\":\"Win\", \"progressText\":\"\"}";
    }

    @Test
    public void that_submitting_progress_to_invalid_opportunity_is_bad_request() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put("/api/opportunity/submit/progress/1337")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(getProgressInJson());

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void that_submitting_invalid_progress_is_bad_request() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put("/api/opportunity/submit/progress/" + opportunity.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(getInvalidProgressInJson());

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    private String getInvalidProgressInJson() {
        return "{\"opportunityState\":\"\", \"progressText\":\"\"}";
    }

    @Test
    public void that_submitting_second_progress_sets_right_last_progress() throws Exception {
        Opportunity opportunity = new Opportunity("KOZOO", "Lead", "");
        opportunityRepository.save(opportunity);

        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put("/api/opportunity/submit/progress/" + opportunity.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(getProgressInJson());

        this.mockMvc.perform(builder);

        MockHttpServletRequestBuilder secondBuilder =
                MockMvcRequestBuilders.put("/api/opportunity/submit/progress/" + opportunity.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(getSecondProgressInJson());

        this.mockMvc.perform(secondBuilder)
                .andExpect(MockMvcResultMatchers.status()
                        .isCreated())
                .andDo(MockMvcResultHandlers.print());
    }

    private String getSecondProgressInJson() {
        return "{\"opportunityState\":\"Quote\", \"progressText\":\"Wieder eine Quote\"}";
    }

    @Test
    public void that_opportunity_got_deleted_successfully() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.delete("/api/opportunity/delete/" + opportunity.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8");

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andDo(MockMvcResultHandlers.print());

        assertThat(opportunityRepository
                .findAll()
                .size()).isEqualTo(0);
    }

    @Test
    public void that_deleting_non_existing_opportunity_is_not_found() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.delete("/api/opportunity/delete/1337")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8");

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isNotFound())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void that_getting_opportunities_is_successfully() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.get("/api/opportunity/get/")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8");

        MvcResult result = this.mockMvc.perform(builder).andReturn();
        String response = result.getResponse().getContentAsString();

        assertThat(response).contains("CUZOO");
    }

    @Test
    public void that_getting_opportunities_of_company_is_successfully() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.get("/api/opportunity/get/list/" + company.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8");

        MvcResult result = this.mockMvc
                .perform(builder)
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
        String response = result.getResponse().getContentAsString();

        assertThat(response).contains("CUZOO");
    }

    @Test
    public void that_getting_opportunities_of_non_existing_company_is_not_found() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.get("/api/opportunity/get/list/1337")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8");

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isNotFound())
                .andDo(MockMvcResultHandlers.print());
    }
}
