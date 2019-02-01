package de.smartsquare.cuzoo.customer.contact;

import de.smartsquare.cuzoo.customer.company.Company;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ContactExporterTest {
    private Contact darius;
    private ContactExporter contactExporter;

    @Before
    public void initialize() {
        Company smartsquare = new Company("Smartsquare GmbH", "", "", "", "", "", "");
        darius = new Contact("Darius Tack", smartsquare, "Azubi", "darius@tack.de", "012345678910", "", "", "Kommentar");

        contactExporter = new ContactExporter();
    }

    @Test
    public void that_information_is_getting_written() {
        List<String> content = contactExporter.getContactContent(darius);

        assertThat(content.get(0).contains(darius.getName())).isTrue();
        assertThat(content.get(1).contains(darius.getCompany().getName())).isTrue();
        assertThat(content.get(2).contains(darius.getRole())).isTrue();
        assertThat(content.get(3).contains(darius.getMail())).isTrue();
        assertThat(content.get(4).contains(darius.getTelephone())).isTrue();
        assertThat(content.get(5).contains(darius.getComment())).isTrue();
    }
}
