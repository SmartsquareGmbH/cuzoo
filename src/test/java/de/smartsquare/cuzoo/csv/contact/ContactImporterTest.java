package de.smartsquare.cuzoo.csv.contact;

import org.junit.Test;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ContactImporterTest {

    @Test
    public void that_deserializer_imports_correctly() {
        ContactImporter contactImporter = new ContactImporter();

        List<Contact> contacts = contactImporter.importFrom(ContactImporterTest.class.getResourceAsStream("/TestContacts.csv"));

        assertThat(contacts.size()).isEqualTo(3);
    }

}
