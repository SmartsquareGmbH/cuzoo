package de.smartsquare.cuzoo.customer.contactpoint;

import de.smartsquare.cuzoo.customer.company.Company;
import de.smartsquare.cuzoo.customer.company.CompanyRepository;
import de.smartsquare.cuzoo.customer.contact.Contact;
import de.smartsquare.cuzoo.customer.contact.ContactRepository;
import de.smartsquare.cuzoo.customer.label.Label;
import de.smartsquare.cuzoo.customer.label.LabelRepository;
import de.smartsquare.cuzoo.customer.opportunity.Opportunity;
import de.smartsquare.cuzoo.customer.opportunity.OpportunityRepository;
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

import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "drs", password = "secure")
public class ContactPointControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private ContactRepository contactRepository;
    @Autowired
    private ContactPointRepository contactPointRepository;
    @Autowired
    private LabelRepository labelRepository;
    @Autowired
    private OpportunityRepository opportunityRepository;

    private User user;
    private Company company;
    private Contact contact;
    private ContactPoint contactPoint;
    private Label label;
    private Opportunity opportunity;

    @Before
    public void initialize() {
        userRepository.deleteAll();

        user = new User("user", "", "", "");
        company = new Company("Smartsquare GmbH", "", "", "", "", "", "");
        contact = new Contact("Darius", "", "", "", "", "");
        opportunity = new Opportunity("Moeglichkeit", "Prospect", "Eine Moeglichkeit!");
        label = new Label("Cloud Flyer");

        userRepository.save(user);
        companyRepository.save(company);
        contact.setCompany(company);
        contact.setManager(user);
        contactRepository.save(contact);

        contactPoint = new ContactPoint("Beratung", 0L, contact, "JD$UTF%N&~", "", "");
        contactPoint.setCreator(user);
        contactPoint.addLabel(label);
        label.addContactPointWithLabel(contactPoint);

        opportunity.addContactPoint(contactPoint);
        contactPoint.setOpportunity(opportunity);
        opportunityRepository.save(opportunity);
        contactPointRepository.save(contactPoint);
        labelRepository.save(label);
    }

    @After
    public void tearDown() throws Exception {
        userRepository.deleteAll();
        companyRepository.deleteAll();
        contactRepository.deleteAll();
        contactPointRepository.deleteAll();
        labelRepository.deleteAll();
    }

    @Test
    public void that_contact_point_is_getting_registered() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put("/api/point/submit/" + contact.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(getContactPointInJson());

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isCreated())
                .andDo(MockMvcResultHandlers.print());

        assertThat(contactPointRepository.findAll()
                .stream()
                .map(ContactPoint::getTitle))
                .contains("Beratungsgespraech");
    }

    @Test
    public void that_submitting_contact_point_without_existing_contact_is_bad_request() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put("/api/point/submit/13337")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(getContactPointInJson());

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isBadRequest())
                .andDo(MockMvcResultHandlers.print());

        assertThat(contactPointRepository.findAll()
                .stream()
                .map(ContactPoint::getTitle))
                .doesNotContain("Beratungsgespraech");
    }

    private String getContactPointInJson() {
        return "{\"id\":\"0\", \"title\":\"Beratungsgespraech\", \"date\":\"0\", \"types\":[\"Social Media\"], \"creator\":\"user\"}";
    }

    @Test
    public void that_submitting_invalid_title_with_contact_point_is_bad_request() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put("/api/point/submit/" + contact.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(getInvalidTitleContactPointInJson());

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isBadRequest())
                .andDo(MockMvcResultHandlers.print());

        assertThat(contactPointRepository.findAll()
                .stream()
                .map(ContactPoint::getTitle))
                .doesNotContain("");
    }

    private String getInvalidTitleContactPointInJson() {
        return "{\"id\":\"0\", \"title\":\"\", \"date\":\"0\", \"types\":[\"Social Media\"], \"creator\":\"user\"}";
    }

    @Test
    public void that_submitting_invalid_date_with_contact_point_is_bad_request() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put("/api/point/submit/" + contact.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(getInvalidDateContactPointInJson());

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isBadRequest())
                .andDo(MockMvcResultHandlers.print());

        assertThat(contactPointRepository.findAll()
                .stream()
                .map(ContactPoint::getTitle))
                .doesNotContain("Beratungsgespraech");
    }

    private String getInvalidDateContactPointInJson() {
        return "{\"id\":\"0\", \"title\":\"Beratungsgespraech\", \"date\":\"\", \"types\":[\"Social Media\"], \"creator\":\"user\"}";
    }

    @Test
    public void that_submitting_contact_point_without_existing_creator_is_bad_request() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put("/api/point/submit/" + contact.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(getContactPointWithInvalidCreatorInJson());

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isBadRequest())
                .andDo(MockMvcResultHandlers.print());

        assertThat(contactPointRepository.findAll()
                .stream()
                .map(ContactPoint::getTitle))
                .doesNotContain("Beratungsgespraech");
    }

    private String getContactPointWithInvalidCreatorInJson() {
        return "{\"id\":\"0\", \"title\":\"Beratungsgespraech\", \"date\":\"0\", \"types\":[\"Social Media\"], \"creator\":\"Fridolin\"}";
    }

    @Test
    public void that_non_existing_labels_are_getting_registered_by_submitting_contact_point() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put("/api/point/submit/" + contact.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(getContactPointInJsonWithNonExistingLabels());

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isCreated())
                .andDo(MockMvcResultHandlers.print());

        assertThat(labelRepository.findAll()
                .stream()
                .map(Label::getTitle))
                .contains("Weihnachtskarte");
    }

    private String getContactPointInJsonWithNonExistingLabels() {
        return "{\"id\":\"0\", \"title\":\"Beratungsgespraech\", " +
                "\"date\":\"0\", \"creator\":\"user\", " +
                "\"labels\": [\"Weihnachtskarte\"], \"types\":[\"Social Media\"]}";
    }

    @Test
    @Transactional
    public void that_existing_labels_are_getting_assigned() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put("/api/point/submit/" + contact.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(getContactPointInJsonWithExistingLabels());

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isCreated())
                .andDo(MockMvcResultHandlers.print());

        assertThat(contactPointRepository.findAll()
                .stream()
                .anyMatch(contactPoint -> contactPoint.getLabels().contains(label)))
                .isTrue();
    }

    private String getContactPointInJsonWithExistingLabels() {
        return "{\"id\":\"0\", \"title\":\"Beratungsgespraech\", " +
                "\"date\":\"0\", \"creator\":\"user\"," +
                "\"labels\": [\"" + label.getTitle() + "\"], \"types\":[\"Social Media\"]}";
    }

    @Test
    public void that_contact_point_is_getting_updated() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put("/api/point/submit/" + contact.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(getOutdatedContactPointInJson());

        MvcResult result = this.mockMvc.perform(builder).andReturn();
        String id = result.getResponse().getContentAsString();

        MockHttpServletRequestBuilder updatedBuilder =
                MockMvcRequestBuilders.put("/api/point/submit/" + contact.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(getUpdatedContactPointInJson(id));

        this.mockMvc.perform(updatedBuilder)
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andDo(MockMvcResultHandlers.print());

        assertThat(contactPointRepository.findAll()
                .stream()
                .map(ContactPoint::getTitle))
                .contains("Auftrag");
    }

    private String getOutdatedContactPointInJson() {
        return "{\"id\":\"0\", \"title\":\"Beratungsgespraech\", \"date\":\"0\", \"types\":[\"Social Media\"], \"creator\":\"user\"}";
    }

    private String getUpdatedContactPointInJson(String id) {
        return "{\"id\":\"" + id + "\", \"title\":\"Auftrag\", \"date\":\"0\", \"types\":[\"Social Media\"], \"creator\":\"user\"}";
    }

    @Test
    public void that_contact_point_is_getting_deleted() throws Exception {
        ContactPoint contactPoint = new ContactPoint("Beratung", 0L, contact, "", "", "");
        contactPoint.setCreator(user);
        contactPointRepository.save(contactPoint);

        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.delete("/api/point/delete/" + contactPoint.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8");

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andDo(MockMvcResultHandlers.print());

        assertThat(contactPointRepository.existsById(contactPoint.getId())).isFalse();
    }

    @Test
    public void that_deleting_contact_point_with_non_existing_id_is_not_found() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.delete("/api/point/delete/" + -1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8");

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isNotFound())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void that_getting_contact_points_is_successfully() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.get("/api/point/get")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8");

        MvcResult result = this.mockMvc.perform(builder).andReturn();
        String response = result.getResponse().getContentAsString();

        assertThat(response).contains("Beratung");
    }

    @Test
    public void that_getting_contact_points_of_company_is_successfully() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.get("/api/point/get/" + company.getName())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8");

        MvcResult result = this.mockMvc.perform(builder).andReturn();
        String response = result.getResponse().getContentAsString();

        assertThat(response).contains("Beratung");
    }

    @Test
    public void that_getting_contact_points_of_invalid_company_is_bad_request() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.get("/api/point/get/xxx")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8");

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void that_getting_labels_of_contact_points_with_part_of_title_is_successfully() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.get("/api/point/get/labels/oud")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8");

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .string("[\"Cloud Flyer\"]"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void that_getting_types_by_input_is_successfully() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put("/api/point/submit/" + contact.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(getContactPointWithLabelsAndTypesInJson());

        this.mockMvc.perform(builder);

        MockHttpServletRequestBuilder getBuilder =
                MockMvcRequestBuilders.get("/api/point/get/types/med")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8");

        MvcResult result = this.mockMvc.perform(getBuilder).andReturn();

        assertThat(result
                .getResponse()
                .getContentAsString()
                .equals("[\"Social Media\"]"))
                .isTrue();
    }

    @Test
    public void that_getting_labels_is_successfully() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put("/api/point/submit/" + contact.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(getContactPointWithLabelsAndTypesInJson());

        this.mockMvc.perform(builder);

        MockHttpServletRequestBuilder getBuilder =
                MockMvcRequestBuilders.get("/api/point/get/labels")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8");

        MvcResult result = this.mockMvc.perform(getBuilder).andReturn();

        assertThat(result
                .getResponse()
                .getContentAsString()
                .contains("JKL"))
                .isTrue();
    }

    @Test
    public void that_getting_types_is_successfully() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put("/api/point/submit/" + contact.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(getContactPointWithLabelsAndTypesInJson());

        this.mockMvc.perform(builder);

        MockHttpServletRequestBuilder getBuilder =
                MockMvcRequestBuilders.get("/api/point/get/types")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8");

        MvcResult result = this.mockMvc.perform(getBuilder).andReturn();

        assertThat(result
                .getResponse()
                .getContentAsString()
                .contains("Social Media"))
                .isTrue();
    }

    private String getContactPointWithLabelsAndTypesInJson() {
        return "{\"id\":\"0\", \"title\":\"Beratungsgespraech\", " +
                "\"date\":\"0\", \"creator\":\"user\"," +
                "\"labels\": [\"JKL\"], \"types\":[\"Social Media\"]}";
    }

    @Test
    public void that_getting_contact_points_of_opportunity_is_successfully() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.get("/api/point/get/opportunity/" + opportunity.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8");

        MvcResult result = this.mockMvc.perform(builder).andReturn();

        assertThat(result
                .getResponse()
                .getContentAsString()
                .contains(contactPoint.getComment()))
                .isTrue();
    }
}