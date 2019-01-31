package de.smartsquare.cuzoo.customer.contact;

import de.smartsquare.cuzoo.customer.company.Company;
import de.smartsquare.cuzoo.customer.company.CompanyRepository;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "drs", password = "secure")
public class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private ContactRepository contactRepository;

    @After
    public void tearDown() throws Exception {
        companyRepository.deleteAll();
        contactRepository.deleteAll();
    }

    @Test
    public void that_csv_contacts_getting_registered_after_upload() throws Exception {
        URL url = ContactControllerTest.class.getClassLoader().getResource("TestContacts.csv");
        URI uri = Objects.requireNonNull(url).toURI();
        Path path = new File(uri).toPath();

        MockMultipartFile file = new MockMultipartFile("file", "TestContacts.csv", "text/plain", Files.readAllBytes(path));

        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/api/contact/import")
                        .file(file);

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isCreated())
                .andDo(MockMvcResultHandlers.print());

        assertThat(contactRepository.findAll().size()).isEqualTo(3);
    }

    @Test
    public void that_uploading_empty_file_is_bad_request() throws Exception {
        byte[] noBytes = {};
        MockMultipartFile file = new MockMultipartFile("file", noBytes);

        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/api/contact/import")
                        .file(file);

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void that_contact_of_company_is_getting_registered() throws Exception {
        String companyName = "Smartsquare GmbH";
        companyRepository.save(new Company(companyName, "", "", "", "", "", ""));

        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put("/api/contact/submit?companyName=" + companyName)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(getContactInJson());

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isCreated())
                .andDo(MockMvcResultHandlers.print());

        assertThat(contactRepository.findAll()
                .stream()
                .anyMatch(contact -> contact.getName()
                        .equals("Freddy Faulig")))
                .isTrue();
    }

    @Test
    public void that_contact_does_not_get_registered_when_company_does_not_exists() throws Exception {
        String companyName = "Smartsquare GmbH";

        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put("/api/contact/submit?companyName=" + companyName)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(getContactInJson());

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    private String getContactInJson() {
        return "{\"name\":\"Freddy Faulig\", \"role\":\"Boesewicht\"}";
    }

    @Test
    public void that_contact_is_getting_updated() throws Exception {
        String companyName = "Smartsquare GmbH";
        companyRepository.save(new Company(companyName, "", "", "", "", "", ""));

        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put("/api/contact/submit?companyName=" + companyName)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(getOutdatedContactInJson());

        MockHttpServletRequestBuilder updatedBuilder =
                MockMvcRequestBuilders.put("/api/contact/submit?companyName=" + companyName)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(getUpdatedContactInJson());

        this.mockMvc.perform(builder);
        this.mockMvc.perform(updatedBuilder)
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andDo(MockMvcResultHandlers.print());

        assertThat(contactRepository.findAll()
                .stream()
                .anyMatch(contact -> contact.getRole()
                        .equals("Softwareentwickler")))
                .isTrue();
    }

    private String getOutdatedContactInJson() {
        return "{\"id\":\"1\", \"name\":\"Darius Tack\", \"role\":\"Azubi\"}";
    }

    private String getUpdatedContactInJson() {
        return "{\"id\":\"1\", \"name\":\"Darius Tack\", \"role\":\"Softwareentwickler\"}";
    }

    @Test
    public void that_submitting_invalid_contact_is_bad_request() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put("/api/contact/submit")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(getInvalidContactInJson());

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    private String getInvalidContactInJson() {
        return "{\"role\":\"Azubi\"}";
    }

    @Test
    public void that_freelancer_is_getting_registered() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put("/api/contact/submit")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(getFreelancerInJson());

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isCreated())
                .andDo(MockMvcResultHandlers.print());

        assertThat(contactRepository.findAll()
                .stream()
                .filter(contact -> contact.getName()
                        .equals("Fred Feuerstein"))
                .count())
                .isEqualTo(1);
    }

    private String getFreelancerInJson() {
        return "{\"name\":\"Fred Feuerstein\", \"role\":\"Freiberufler\"}";
    }

    @Test
    public void that_contact_is_getting_deleted() throws Exception {
        Contact contact = new Contact("Zoey", "", "", "", "", "", "");
        contactRepository.save(contact);

        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.delete("/api/contact/delete/" + contact.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8");

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andDo(MockMvcResultHandlers.print());

        assertThat(contactRepository.existsById(contact.getId())).isFalse();
    }

    @Test
    public void that_deleting_contact_with_non_existing_id_is_bad_request() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.delete("/api/contact/delete/" + -1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8");

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void that_getting_contacts_is_successfully() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.get("/api/contact/get")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8");

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andDo(MockMvcResultHandlers.print());
    }

}