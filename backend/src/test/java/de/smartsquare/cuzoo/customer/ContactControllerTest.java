package de.smartsquare.cuzoo.customer;

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

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CompanyRepository companyRepository;

    @Test
    @WithMockUser(username = "drs", password = "secure")
    public void that_contacts_of_company_getting_registered() throws Exception {
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
                        .isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    private String getContactInJson() {
        return "{\"name\":\"Darius Tack\", \"role\":\"Azubi\"}";
    }

    @Test
    @WithMockUser(username = "drs", password = "secure")
    public void that_freelancers_getting_registered() throws Exception {
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put("/api/contact/submit")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(getFreelancerInJson());

        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    private String getFreelancerInJson() {
        return "{\"name\":\"Darius Tack\", \"role\":\"Freiberufler\"}";
    }

}