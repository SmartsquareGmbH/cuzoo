package de.smartsquare.cuzoo.csv.contact;

import net.sf.jsefa.Deserializer;
import net.sf.jsefa.common.lowlevel.filter.HeaderAndFooterFilter;
import net.sf.jsefa.csv.CsvIOFactory;
import net.sf.jsefa.csv.config.CsvConfiguration;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ContactImportTest {

    private static final List<Contact> contacts = new ArrayList<>();

    @Test
    public void deserializer_imports_correctly() {
        CsvConfiguration config = new CsvConfiguration();
        config.setLineFilter(new HeaderAndFooterFilter(1, false, true));
        config.getDefaultNoValueString();

        Deserializer deserializer = CsvIOFactory
                .createFactory(config, Contact.class)
                .createDeserializer();

        InputStream stream = ContactImportTest.class.getResourceAsStream("/TestContacts.csv");
        InputStreamReader streamReader = new InputStreamReader(stream);

        deserializer.open(streamReader);

        while(deserializer.hasNext()) {
            Contact nextContact = deserializer.next();

            if (nextContact != null) {
                contacts.add(nextContact);
            }
        }

        contacts.stream()
                .map(Contact::getName)
                .forEach(System.out::println);

        assertThat(contacts.size()).isEqualTo(3);
    }

}
