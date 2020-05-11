package de.smartsquare.cuzoo.customer.contact;

import de.smartsquare.cuzoo.customer.company.Company;
import de.smartsquare.cuzoo.customer.company.CompanyRepository;
import de.smartsquare.cuzoo.customer.label.Label;
import de.smartsquare.cuzoo.customer.label.LabelRepository;
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
import org.springframework.test.web.servlet.MvcResult;
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
    @Autowired
    private LabelRepository labelRepository;

    private User manager;

    @After
    public void tearDown() throws Exception {
        userRepository.deleteAll();
        contactRepository.deleteAll();
        companyRepository.deleteAll();
        labelRepository.deleteAll();
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

        assertThat(contactRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    public void that_contact_of_company_is_getting_registered() throws Exception {
        Company newCompany = companyRepository.save(new Company("Smartsquare GmbH", "", "", "", "", "", ""));

        MockHttpServletRequestBuilder builder = submitContact(getContactInJson(), newCompany.getId());

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isCreated())
                .andDo(MockMvcResultHandlers.print());

        assertThat(contactRepository.findAll()
                .stream()
                .map(Contact::getName))
                .contains("Freddy Faulig");
    }

    @Test
    public void that_contact_does_not_get_registered_when_company_does_not_exists() throws Exception {
        Long companyId = 2349345L;
        manager.setId(1L);

        MockHttpServletRequestBuilder builder = submitContact(getContactInJson(), companyId);

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isBadRequest())
                .andDo(MockMvcResultHandlers.print());

        assertThat(contactRepository.findAll()
                .stream()
                .map(Contact::getName))
                .doesNotContain("Freddy Faulig");
    }

    private String getContactInJson() {
        return "{\"id\":\"0\", \"name\":\"Freddy Faulig\", \"role\":\"Boesewicht\", \"manager\":\"alex\"}";
    }

    @Test
    public void that_contact_is_getting_updated() throws Exception {
        //given
        Company newCompany = companyRepository.save(new Company("Smartsquare GmbH", "", "", "", "", "", ""));

        MockHttpServletRequestBuilder builder = submitContact(getOutdatedContactInJson(), newCompany.getId());

        MvcResult result = this.mockMvc.perform(builder).andReturn();
        String id = result.getResponse().getContentAsString();

        //when
        MockHttpServletRequestBuilder updatedBuilder = submitContact(getUpdatedContactInJson(id), newCompany.getId());

        this.mockMvc.perform(updatedBuilder)
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andDo(MockMvcResultHandlers.print());

        //then
        assertThat(contactRepository.findAll()
                .stream()
                .map(Contact::getRole))
                .containsOnly("Softwareentwickler");
    }

    private MockHttpServletRequestBuilder submitContact(String content, Long companyId) {
        String requestPath = "/api/contact/submit";
        if (companyId != null) {
            requestPath += "?companyId=" + companyId;
        }

        return MockMvcRequestBuilders.put(requestPath)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(content);
    }

    private MockHttpServletRequestBuilder submitContact(String content) {
        return submitContact(content, null);
    }

    private String getOutdatedContactInJson() {
        return "{\"id\":\"0\", \"name\":\"Darius Tack\", \"role\":\"Azubi\", \"manager\":\"alex\"}";
    }

    private String getUpdatedContactInJson(String id) {
        return "{\"id\":\"" + id + "\", \"name\":\"Darius Tack\", \"role\":\"Softwareentwickler\", \"manager\":\"alex\"}";
    }

    @Test
    public void that_submitting_invalid_contact_is_bad_request() throws Exception {
        MockHttpServletRequestBuilder builder = submitContact(getInvalidContactInJson());

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isBadRequest())
                .andDo(MockMvcResultHandlers.print());

        assertThat(contactRepository.findAll()
                .stream()
                .map(Contact::getRole))
                .doesNotContain("Azubi");
    }

    private String getInvalidContactInJson() {
        return "{\"role\":\"Azubi\"}";
    }

    @Test
    public void that_submitting_invalid_manager_for_contact_is_bad_request() throws Exception {
        MockHttpServletRequestBuilder builder = submitContact(getInvalidManagerInJson());

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isBadRequest())
                .andDo(MockMvcResultHandlers.print());

        assertThat(contactRepository.findAll()
                .stream()
                .map(Contact::getName))
                .doesNotContain("Darius Tack");
    }

    private String getInvalidManagerInJson() {
        return "{\"id\":\"1\", \"name\":\"Darius Tack\", \"role\":\"Azubi\", \"manager\":\"Fridolin\"}";
    }

    @Test
    public void that_freelancer_is_getting_registered() throws Exception {
        MockHttpServletRequestBuilder builder = submitContact(getFreelancerInJson());

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isCreated())
                .andDo(MockMvcResultHandlers.print());

        assertThat(contactRepository.findAll()
                .stream()
                .map(Contact::getName))
                .containsOnlyOnce("Fred Feuerstein");
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

        MvcResult result = this.mockMvc.perform(builder).andReturn();
        String response = result.getResponse().getContentAsString();

        assertThat(response).contains("Tom");
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
        MockHttpServletRequestBuilder builder = submitContact(getContactInJson());
        this.mockMvc.perform(builder);

        MockHttpServletRequestBuilder getBuilder =
                MockMvcRequestBuilders.get("/api/contact/get")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8");

        MvcResult result = this.mockMvc.perform(getBuilder).andReturn();
        String response = result.getResponse().getContentAsString();

        assertThat(response).contains("Freddy Faulig");
    }

    @Test
    public void that_contact_labels_got_registered() throws Exception {
        MockHttpServletRequestBuilder builder = submitContact(getContactWithLabelsInJson());

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isCreated())
                .andDo(MockMvcResultHandlers.print());

        assertThat(labelRepository.findAll()
                .stream()
                .map(Label::getTitle))
                .contains("EJK");
    }

    @Test
    public void that_getting_labels_is_successfully() throws Exception {
        MockHttpServletRequestBuilder builder = submitContact(getContactWithLabelsInJson());

        this.mockMvc.perform(builder);

        MockHttpServletRequestBuilder getBuilder =
                MockMvcRequestBuilders.get("/api/contact/get/labels")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8");

        MvcResult result = this.mockMvc.perform(getBuilder).andReturn();

        assertThat(result
                .getResponse()
                .getContentAsString()
                .contains("GHI"))
                .isTrue();
    }

    @Test
    public void that_getting_labels_by_input_is_successfully() throws Exception {
        MockHttpServletRequestBuilder builder = submitContact(getContactWithLabelsInJson());

        this.mockMvc.perform(builder);

        MockHttpServletRequestBuilder getBuilder =
                MockMvcRequestBuilders.get("/api/contact/get/labels/JK")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8");

        MvcResult result = this.mockMvc.perform(getBuilder).andReturn();

        assertThat(result
                .getResponse()
                .getContentAsString()
                .equals("[\"EJK\"]"))
                .isTrue();
    }

    private String getContactWithLabelsInJson() {
        return "{\"id\":\"0\", \"name\":\"Darius Tack\", \"role\":\"Azubi\", \"manager\":\"alex\", \"labels\": [\"GHI\", \"EJK\"]}";
    }

}
