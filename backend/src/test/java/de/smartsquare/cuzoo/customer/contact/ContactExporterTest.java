package de.smartsquare.cuzoo.customer.contact;

import de.smartsquare.cuzoo.customer.company.Company;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ContactExporterTest {
    private Path file;
    private Contact darius;

    private ContactExporter contactExporter;

    @Before
    public void initialize() {
        contactExporter = new ContactExporter();
        Company smartsquare = new Company("Smartsquare GmbH", "", "", "", "", "", "");
        darius = new Contact("Darius Tack", smartsquare, "Azubi", "darius@tack.de", "012345678910", "", "", "Kommentar");
    }

    @After
    public void delete_file() throws IOException {
        Files.delete(Paths.get("src/main/resources/darius_tack.txt"));
    }

    @Test
    public void that_file_of_contact_is_getting_created() {
        file = contactExporter.exportContactToTxt(darius);

        assertThat(file.getFileName().toString()).isEqualTo("darius_tack.txt");
    }

    @Test
    public void that_information_is_getting_written_to_text_file() throws IOException {
        file = contactExporter.exportContactToTxt(darius);

        List<String> contents = Files.readAllLines(file);

        assertThat(contents.get(0).contains(darius.getName())).isTrue();
        assertThat(contents.get(1).contains(darius.getCompany().getName())).isTrue();
        assertThat(contents.get(2).contains(darius.getRole())).isTrue();
        assertThat(contents.get(3).contains(darius.getMail())).isTrue();
        assertThat(contents.get(4).contains(darius.getTelephone())).isTrue();
        assertThat(contents.get(5).contains(darius.getComment())).isTrue();
    }
}
