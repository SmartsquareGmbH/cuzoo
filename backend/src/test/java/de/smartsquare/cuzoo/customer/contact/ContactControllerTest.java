package de.smartsquare.cuzoo.customer.contact;

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
    @Autowired
    private UserRepository userRepository;

    private User manager;

    @After
    public void tearDown() throws Exception {
        userRepository.deleteAll();
        contactRepository.deleteAll();
        companyRepository.deleteAll();
    }

    @Before
    public void initialize() throws Exception {
        userRepository.deleteAll();
        manager = new User("alex", "1234", "Alexander Mustermann", "");
        manager.setId(1L);
        userRepository.save(manager);
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
        manager.setId(1L);

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
        return "{\"id\":\"0\", \"name\":\"Freddy Faulig\", \"role\":\"Boesewicht\", \"manager\":\"alex\"}";
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
        return "{\"id\":\"1\", \"name\":\"Darius Tack\", \"role\":\"Azubi\", \"manager\":\"alex\"}";
    }

    private String getUpdatedContactInJson() {
        return "{\"id\":\"1\", \"name\":\"Darius Tack\", \"role\":\"Softwareentwickler\", \"manager\":\"alex\"}";
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
        return "{\"id\":\"0\", \"name\":\"Fred Feuerstein\", \"role\":\"Freiberufler\", \"manager\":\"alex\"}";
    }

    @Test
    public void that_contact_is_getting_deleted() throws Exception {
        Contact contact = new Contact("Zoey", "", "", "", "", "");
        contact.setManager(userRepository.findByUsername("alex"));
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
    public void that_downloading_contacts_information_is_successfully() throws Exception {
        Contact contact = new Contact("Tom", "Azubi", "", "123", "", "");
        contact.setManager(userRepository.findByUsername("alex"));
        Company company = new Company("Tom AG", "", "", "", "", "", "");
        companyRepository.save(company);
        contact.setCompany(company);
        contactRepository.save(contact);

        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.get("/api/contact/download/" + contact.getId())
                        .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE)
                        .accept(MediaType.APPLICATION_OCTET_STREAM_VALUE)
                        .characterEncoding("UTF-8");

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void that_downloading_non_existing_contacts_information_is_bad_request() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.get("/api/contact/download/" + -1)
                        .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE)
                        .accept(MediaType.APPLICATION_OCTET_STREAM_VALUE)
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