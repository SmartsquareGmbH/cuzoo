package de.smartsquare.cuzoo.customer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class CSVConverterTest {

    @Mock
    private CompanyRepository mockCompanyRepository;

    @Test
    public void that_csv_companies_getting_converted_correctly() {
        CSVConverter csvConverter = new CSVConverter(mockCompanyRepository);

        List<Company> companiesToConvert = csvConverter.getConvertedCompanies(CSVConverterTest.class.getResourceAsStream("/TestCompanies.csv"));

        assertThat(companiesToConvert, containsInAnyOrder(
                hasProperty("name", is("Anders GmbH")),
                hasProperty("name", is("Ben & Biggs AG")),
                hasProperty("name", is("Chlor Claud"))
        ));
    }

}
