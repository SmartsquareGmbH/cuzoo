package de.smartsquare.cuzoo.customer.contact;

import de.smartsquare.cuzoo.customer.company.Company;
import org.junit.Test;

import java.io.File;

public class ContactExporterTest {

    @Test
    public void that_file_of_contact_is_getting_created() {
        ContactExporter contactExporter = new ContactExporter();
        Company smartsquare = new Company("Smartsquare GmbH", "", "", "", "", "", "");
        Contact darius = new Contact("Darius Tack", smartsquare, "Azubi",  "darius@tack.de", "012345678910", "", "", "Kommentar");

        File file = contactExporter.exportContactToTxt(darius);

        System.out.println(file.getName());
        assert file.getName().equals("darius_tack.txt");
    }

}
