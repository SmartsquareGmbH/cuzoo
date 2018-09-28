package de.smartsquare.cuzoo.csv.contact;

import net.sf.jsefa.Deserializer;
import net.sf.jsefa.common.lowlevel.filter.HeaderAndFooterFilter;
import net.sf.jsefa.csv.CsvIOFactory;
import net.sf.jsefa.csv.config.CsvConfiguration;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

class ContactImporter {
    private List<Contact> contacts;
    private Deserializer deserializer;

    ContactImporter() {
        contacts = new ArrayList<>();
        CsvConfiguration config = new CsvConfiguration();

        config.setLineFilter(new HeaderAndFooterFilter(1, false, true));
        config.getDefaultNoValueString();

        deserializer = CsvIOFactory
                .createFactory(config, Contact.class)
                .createDeserializer();
    }

    List<Contact> importFrom(InputStream stream) {
        try {
            deserializer.open(new InputStreamReader(stream));

            while(deserializer.hasNext()) {
                Contact nextContact = deserializer.next();

                if (nextContact != null) {
                    contacts.add(nextContact);
                }
            }
        } finally {
            deserializer.close(true);
        }

        return contacts;
    }
}
