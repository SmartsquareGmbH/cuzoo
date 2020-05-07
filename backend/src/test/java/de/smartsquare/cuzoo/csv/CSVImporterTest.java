package de.smartsquare.cuzoo.csv;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class CSVImporterTest {

    @Test
    public void that_deserializer_imports_as_much_as_needed(){
        CSVImporter csvImporter = new CSVImporter();

        try (InputStream in = CSVImporterTest.class.getResourceAsStream("/TestCompanies.csv")) {
            List<CSVCompany> companies = csvImporter.importFrom(in, CSVCompany.class);

            assertThat(companies.size()).isEqualTo(3);

        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void that_deserializer_imports_companies_correctly() {
        CSVImporter csvImporter = new CSVImporter();

        try (InputStream in = CSVImporterTest.class.getResourceAsStream("/TestCompanies.csv")) {
            List<CSVCompany> companies = csvImporter.importFrom(in, CSVCompany.class);

            assertThat(companies, containsInAnyOrder(
                    hasProperty("company", is("Anders GmbH")),
                    hasProperty("company", is("Ben & Biggs AG")),
                    hasProperty("company", is("Chlor Claud"))
            ));

        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void that_deserializer_imports_contacts_correctly() {
        CSVImporter csvImporter = new CSVImporter();

        try (InputStream in = CSVImporterTest.class.getResourceAsStream("/TestContacts.csv")) {
            List<CSVContact> CSVContacts = csvImporter.importFrom(in, CSVContact.class);

            assertThat(CSVContacts, containsInAnyOrder(
                    hasProperty("name", is("Alfred Anders")),
                    hasProperty("name", is("Ben Big")),
                    hasProperty("name", is("Claudia Chlor"))
            ));

        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
}
