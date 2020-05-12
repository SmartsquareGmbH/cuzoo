package de.smartsquare.cuzoo.customer.company;

import de.smartsquare.cuzoo.customer.contact.ContactControllerTest;
import de.smartsquare.cuzoo.customer.label.Label;
import de.smartsquare.cuzoo.customer.label.LabelRepository;
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
public class CompanyControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private LabelRepository labelRepository;

    @After
    public void tearDown() throws Exception {
        companyRepository.deleteAll();
        labelRepository.deleteAll();
    }

    @Test
    public void that_csv_companies_getting_registered_after_upload() throws Exception {
        URL url = ContactControllerTest.class.getClassLoader().getResource("TestCompanies.csv");
        URI uri = Objects.requireNonNull(url).toURI();
        Path path = new File(uri).toPath();

        MockMultipartFile file = new MockMultipartFile("file", "TestCompanies.csv", "text/plain", Files.readAllBytes(path));

        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/api/company/import")
                        .file(file);

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isCreated())
                .andDo(MockMvcResultHandlers.print());

        assertThat(companyRepository.findAll().size()).isEqualTo(3);
    }

    @Test
    public void that_uploading_empty_file_is_bad_request() throws Exception {
        byte[] noBytes = {};
        MockMultipartFile file = new MockMultipartFile("file", noBytes);

        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/api/company/import")
                        .file(file);

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isBadRequest())
                .andDo(MockMvcResultHandlers.print());

        assertThat(companyRepository.findAll()).isEmpty();
    }

    @Test
    public void that_company_is_getting_registered() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put("/api/company/submit")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(getCompanyInJson());

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isCreated())
                .andDo(MockMvcResultHandlers.print());

        assertThat(companyRepository.findAll()
                .stream()
                .map(Company::getName))
                .contains("Fidea Development");
    }

    private String getCompanyInJson() {
        return "{\"id\":\"0\", \"name\":\"Fidea Development\"}";
    }

    @Test
    public void that_company_is_getting_updated() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put("/api/company/submit")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(getOutdatedCompanyInJson());

        MvcResult result = this.mockMvc.perform(builder).andReturn();
        String id = result.getResponse().getContentAsString();

        MockHttpServletRequestBuilder updatedBuilder =
                MockMvcRequestBuilders.put("/api/company/submit")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(getUpdatedCompanyInJson(id));

        this.mockMvc.perform(updatedBuilder)
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andDo(MockMvcResultHandlers.print());

        assertThat(companyRepository.findAll()
                .stream()
                .map(Company::getName))
                .contains("Smartsquare GmbH");
    }

    private String getOutdatedCompanyInJson() {
        return "{\"id\":\"0\", \"name\":\"Smartsquare GmbH\", \"status\":\"Lead\"}";
    }

    private String getUpdatedCompanyInJson(String id) {
        return "{\"id\":\"" + id + "\", \"name\":\"Smartsquare GmbH\", \"status\":\"Bestandskunde\"}";
    }

    @Test
    public void that_submitting_invalid_company_is_bad_request() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put("/api/company/submit")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(getInvalidCompanyInJson());

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isBadRequest())
                .andDo(MockMvcResultHandlers.print());

        assertThat(companyRepository.findAll()
                .stream()
                .map(Company::getName))
                .doesNotContain("");
    }

    private String getInvalidCompanyInJson() {
        return "{\"name\":\"\"}";
    }

    @Test
    public void that_company_is_getting_deleted() throws Exception {
        Company company = new Company("Fidea UG", "", "", "", "", "", "");
        companyRepository.save(company);

        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.delete("/api/company/delete/" + company.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8");

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andDo(MockMvcResultHandlers.print());

        assertThat(companyRepository.existsById(company.getId())).isFalse();
    }

    @Test
    public void that_deleting_company_with_non_existing_id_is_not_found() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.delete("/api/company/delete/" + -1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8");

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isNotFound())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void that_getting_companies_is_successfully() throws Exception {
        companyRepository.save(new Company("Fidea UG", "", "", "", "", "", ""));

        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.get("/api/company/get")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8");

        MvcResult result = this.mockMvc.perform(builder).andReturn();
        String response = result.getResponse().getContentAsString();

        assertThat(response).contains("Fidea UG");
    }

    @Test
    public void that_company_labels_got_registered() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put("/api/company/submit")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(getCompanyWithLabelsInJson());

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isCreated())
                .andDo(MockMvcResultHandlers.print());

        assertThat(labelRepository.findAll()
                .stream()
                .map(Label::getTitle))
                .contains("DEF");
    }

    @Test
    public void that_getting_labels_is_successfully() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put("/api/company/submit")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(getCompanyWithLabelsInJson());

        this.mockMvc.perform(builder);

        MockHttpServletRequestBuilder getBuilder =
                MockMvcRequestBuilders.get("/api/company/get/labels")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8");

        MvcResult result = this.mockMvc.perform(getBuilder).andReturn();

        assertThat(result
                .getResponse()
                .getContentAsString()
                .contains("ABC"))
                .isTrue();
    }

    @Test
    public void that_getting_labels_by_input_is_successfully() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put("/api/company/submit")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(getCompanyWithLabelsInJson());

        this.mockMvc.perform(builder);

        MockHttpServletRequestBuilder getBuilder =
                MockMvcRequestBuilders.get("/api/company/get/labels/DE")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8");

        MvcResult result = this.mockMvc.perform(getBuilder).andReturn();

        assertThat(result
                .getResponse()
                .getContentAsString()
                .equals("[\"DEF\"]"))
                .isTrue();
    }

    private String getCompanyWithLabelsInJson() {
        return "{\"id\":\"0\", \"name\":\"Fidea Development\", \"labels\": [\"ABC\", \"DEF\"]}";
    }

}
