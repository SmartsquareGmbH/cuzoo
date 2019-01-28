package de.smartsquare.cuzoo.customer.contactpoint.attachment;

import de.smartsquare.cuzoo.customer.contact.Contact;
import de.smartsquare.cuzoo.customer.contact.ContactControllerTest;
import de.smartsquare.cuzoo.customer.contactpoint.ContactPoint;
import de.smartsquare.cuzoo.customer.contactpoint.ContactPointRepository;
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
public class AttachmentControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ContactPointRepository contactPointRepository;
    @Autowired
    private AttachmentRepository attachmentRepository;

    private Contact contact;
    private ContactPoint contactPoint;
    private MockMultipartFile file;

    @Before
    public void initialize() throws Exception {
        URL url = ContactControllerTest.class.getClassLoader().getResource("TestCompanies.csv");
        URI uri = Objects.requireNonNull(url).toURI();
        Path path = new File(uri).toPath();

        file = new MockMultipartFile("file", "TestCompanies.csv", "text/plain", Files.readAllBytes(path));
        contactPoint = new ContactPoint("Besprechung", "Social Media", 0L, contact, "");

        contactPointRepository.save(contactPoint);
    }

    @After
    public void tearDown() throws Exception {
        contactPointRepository.deleteAll();
        attachmentRepository.deleteAll();
    }

    @Test
    public void that_file_is_getting_uploaded() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/api/file/upload/" + contactPoint.getId())
                        .file(file);

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isCreated())
                .andDo(MockMvcResultHandlers.print());

        assertThat(attachmentRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    public void that_uploading_file_without_valid_contact_point_id_is_bad_request() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/api/file/upload/999")
                        .file(file);

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void that_downloading_file_is_successfully() throws Exception {
        MockHttpServletRequestBuilder uploadBuilder =
                MockMvcRequestBuilders.multipart("/api/file/upload/" + contactPoint.getId())
                        .file(file);

        MockHttpServletRequestBuilder downloadBuilder =
                MockMvcRequestBuilders.get("/api/file/download/" + contactPoint.getId() + "/" + file.getOriginalFilename())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8");

        this.mockMvc.perform(uploadBuilder);
        this.mockMvc.perform(downloadBuilder)
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void that_downloading_file_without_valid_contact_point_id_is_bad_request() throws Exception {
        MockHttpServletRequestBuilder downloadBuilder =
                MockMvcRequestBuilders.get("/api/file/download/999/" + file.getOriginalFilename())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8");

        this.mockMvc.perform(downloadBuilder)
                .andExpect(MockMvcResultMatchers.status()
                        .isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void that_non_existing_filename_will_not_be_found() throws Exception {
        MockHttpServletRequestBuilder downloadBuilder =
                MockMvcRequestBuilders.get("/api/file/download/" + contactPoint.getId() + "/elch.png")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8");

        this.mockMvc.perform(downloadBuilder)
                .andExpect(MockMvcResultMatchers.status()
                        .isNotFound())
                .andDo(MockMvcResultHandlers.print());
    }

}
