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

        List<CSVCompany> companies = csvImporter.importFrom(CSVImporterTest.class.getResourceAsStream("/TestCompanies.csv"), CSVCompany.class);

        assertThat(companies.size()).isEqualTo(3);
    }

    @Test
    public void that_deserializer_imports_companies_correctly() {
        CSVImporter csvImporter = new CSVImporter();

        List<CSVCompany> companies = csvImporter.importFrom(CSVImporterTest.class.getResourceAsStream("/TestCompanies.csv"), CSVCompany.class);

        assertThat(companies, containsInAnyOrder(
                hasProperty("company", is("Anders GmbH")),
                hasProperty("company", is("Ben & Biggs AG")),
                hasProperty("company", is("Chlor Claud"))
        ));
    }

    @Test
    public void that_deserializer_imports_contacts_correctly() {
        CSVImporter csvImporter = new CSVImporter();

        List<CSVContact> CSVContacts = csvImporter.importFrom(CSVImporterTest.class.getResourceAsStream("/TestContacts.csv"), CSVContact.class);

        assertThat(CSVContacts, containsInAnyOrder(
                hasProperty("name", is("Alfred Anders")),
                hasProperty("name", is("Ben Big")),
                hasProperty("name", is("Claudia Chlor"))
        ));
    }
}
