package de.smartsquare.cuzoo.csv.company;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CompanyImporterTest {

    @Test
    public void that_deserializer_imports_correctly() {
        CompanyImporter companyImporter = new CompanyImporter();

        List<Company> companies = companyImporter.importFrom(CompanyImporterTest.class.getResourceAsStream("/TestCompanies.csv"));

        assertThat(companies.size()).isEqualTo(3);
    }
}
