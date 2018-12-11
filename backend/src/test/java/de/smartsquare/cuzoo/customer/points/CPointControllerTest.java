package de.smartsquare.cuzoo.customer.points;

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
public class CPointControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CPointRepository cPointRepository;
    @Autowired
    private ContactRepository contactRepository;

    @Before
    public void initialize() {
        contactRepository.save(new Contact("Darius Tack", "", "", "", "", "", ""));
    }

    @After
    public void tearDown() throws Exception {
        cPointRepository.deleteAll();
        contactRepository.deleteAll();
    }

    @Test
    public void that_contact_point_is_getting_registered() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put("/api/point/submit")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(getContactPointInJson());

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isCreated())
                .andDo(MockMvcResultHandlers.print());

        assertThat(cPointRepository.findAll()
                .stream()
                .anyMatch(point -> point.getTitle()
                        .equals("Beratungsgespraech")))
                .isTrue();
    }

    private String getContactPointInJson() {
        Long id = contactRepository.findByName("Darius Tack").getId();
        return "{\"title\":\"Beratungsgespraech\", \"contact\":{\"id\":\"" + id + "\", \"name\":\"Darius Tack\"}}";
    }

    @Test
    public void that_contact_point_is_getting_updated() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put("/api/point/submit")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(getOutdatedContactPointInJson());

        MockHttpServletRequestBuilder updatedBuilder =
                MockMvcRequestBuilders.put("/api/point/submit")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(getUpdatedContactPointInJson());

        this.mockMvc.perform(builder);
        this.mockMvc.perform(updatedBuilder)
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andDo(MockMvcResultHandlers.print());

        assertThat(cPointRepository.findAll()
                .stream()
                .anyMatch(point -> point.getTitle()
                        .equals("Auftrag")))
                .isTrue();
    }

    private String getOutdatedContactPointInJson() {
        Long id = contactRepository.findByName("Darius Tack").getId();
        return "{\"id\":\"2\", \"title\":\"BeratungsgespraechZwei\", \"contact\":{\"id\":\"" + id + "\", \"name\":\"Darius Tack\"}}";
    }

    private String getUpdatedContactPointInJson() {
        Long id = contactRepository.findByName("Darius Tack").getId();
        return "{\"id\":\"2\", \"title\":\"Auftrag\", \"contact\":{\"id\":\"" + id + "\", \"name\":\"Darius Tack\"}}";
    }

    @Test
    public void that_submitting_invalid_contact_point_is_bad_request() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put("/api/point/submit")
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
    public void that_submitting_contact_point_without_contact_is_data_access_exception() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put("/api/point/submit")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(getContactPointWithoutValidContactInJson());

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isInternalServerError())
                .andDo(MockMvcResultHandlers.print());
    }

    private String getContactPointWithoutValidContactInJson() {
        return "{\"title\":\"Beratung\", \"contact\":{\"name\":\"Fred Feuerstein\"}}";
    }


}