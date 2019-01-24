package de.smartsquare.cuzoo.customer.contactpoint;

import de.smartsquare.cuzoo.customer.contact.Contact;
import de.smartsquare.cuzoo.customer.contact.ContactRepository;
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
public class ContactPointControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ContactPointRepository contactPointRepository;
    @Autowired
    private ContactRepository contactRepository;
    private Contact contact;

    @Before
    public void initialize() {
        contact = new Contact("Darius Tack", "", "", "", "", "", "");
        contactRepository.save(contact);
    }

    @After
    public void tearDown() throws Exception {
        contactPointRepository.deleteAll();
        contactRepository.deleteAll();
    }

    @Test
    public void that_contact_point_is_getting_registered() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put("/api/point/submit/" + contact.getName())
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
                .anyMatch(point -> point.getTitle()
                        .equals("Beratungsgespraech")))
                .isTrue();
    }

    @Test
    public void that_submitting_contact_point_without_existing_contact_is_bad_request() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put("/api/point/submit/xxx")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(getContactPointInJson());

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    private String getContactPointInJson() {
        return "{\"title\":\"Beratungsgespraech\", \"type\":\"Telefon\", \"date\":\"0\"}";
    }

    @Test
    public void that_contact_point_is_getting_updated() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put("/api/point/submit/" + contact.getName())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(getOutdatedContactPointInJson());

        MockHttpServletRequestBuilder updatedBuilder =
                MockMvcRequestBuilders.put("/api/point/submit/" + contact.getName())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(getUpdatedContactPointInJson());

        this.mockMvc.perform(builder);
        this.mockMvc.perform(updatedBuilder)
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andDo(MockMvcResultHandlers.print());

        assertThat(contactPointRepository.findAll()
                .stream()
                .anyMatch(point -> point.getTitle()
                        .equals("Auftrag")))
                .isTrue();
    }

    private String getOutdatedContactPointInJson() {
        return "{\"id\":\"2\", \"title\":\"Beratungsgespraech\", \"type\":\"Telefon\", \"date\":\"0\"}";
    }

    private String getUpdatedContactPointInJson() {
        return "{\"id\":\"2\", \"title\":\"Auftrag\", \"type\":\"Telefon\", \"date\":\"0\"}";
    }

    @Test
    public void that_submitting_invalid_contact_point_is_bad_request() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put("/api/point/submit/" + contact.getName())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(getInvalidContactPointInJson());

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    private String getInvalidContactPointInJson() {
        return "{\"title\":\"\"}";
    }

    @Test
    public void that_contact_point_is_getting_deleted() throws Exception {
        ContactPoint contactPoint = new ContactPoint("Beratung", "Telefon", 0L, contact, "");
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

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andDo(MockMvcResultHandlers.print());
    }
}