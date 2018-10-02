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
        CSVImporter csvImporter = new CSVImporter();

        List<Company> companies = csvImporter.importFrom(CSVImporterTest.class.getResourceAsStream("/TestCompanies.csv"), Company.class);

        assertThat(companies.size()).isEqualTo(3);
    }

    @Test
    public void that_deserializer_imports_companies_correctly() {
        CSVImporter csvImporter = new CSVImporter();

        List<Company> companies = csvImporter.importFrom(CSVImporterTest.class.getResourceAsStream("/TestCompanies.csv"), Company.class);

        assertThat(companies, containsInAnyOrder(
                hasProperty("unternehmen", is("Anders GmbH")),
                hasProperty("unternehmen", is("Ben & Biggs AG")),
                hasProperty("unternehmen", is("Chlor Claud"))
        ));
    }

    @Test
    public void that_deserializer_imports_contacts_correctly() {
        CSVImporter csvImporter = new CSVImporter();

        List<Contact> contacts = csvImporter.importFrom(CSVImporterTest.class.getResourceAsStream("/TestContacts.csv"), Contact.class);

        assertThat(contacts, containsInAnyOrder(
                hasProperty("name", is("Alfred Anders")),
                hasProperty("name", is("Ben Big")),
                hasProperty("name", is("Claudia Chlor"))
        ));
    }
}
