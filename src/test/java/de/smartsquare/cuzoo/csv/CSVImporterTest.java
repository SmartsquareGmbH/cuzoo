package de.smartsquare.cuzoo.csv;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertThat;

public class CSVImporterTest {

    @Test
    public void that_deserializer_imports_as_much_as_needed(){
        CSVImporter<Company> csvImporter = new CSVImporter<>(Company.class);

        List<Company> companies = csvImporter.importFrom(CSVImporterTest.class.getResourceAsStream("/TestCompanies.csv"));

        assertThat(companies.size()).isEqualTo(3);
    }

    @Test
    public void that_deserializer_imports_companies_correctly() {
        CSVImporter<Company> csvImporter = new CSVImporter<>(Company.class);

        List<Company> companies = csvImporter.importFrom(CSVImporterTest.class.getResourceAsStream("/TestCompanies.csv"));

        companies.forEach(d -> System.out.println(d.getUnternehmen()));

        assertThat(companies, containsInAnyOrder(
                hasProperty("unternehmen", is("Anders GmbH")),
                hasProperty("unternehmen", is("Ben & Biggs AG")),
                hasProperty("unternehmen", is("Chlor Claud"))
        ));
    }

    @Test
    public void that_deserializer_imports_contacts_correctly() {
        CSVImporter<Contact> csvImporter = new CSVImporter<>(Contact.class);

        List<Contact> contacts = csvImporter.importFrom(CSVImporterTest.class.getResourceAsStream("/TestContacts.csv"));

        assertThat(contacts, containsInAnyOrder(
                hasProperty("name", is("Alfred Anders")),
                hasProperty("name", is("Ben Big")),
                hasProperty("name", is("Claudia Chlor"))
        ));
    }
}
