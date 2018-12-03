package de.smartsquare.cuzoo.customer;

import de.smartsquare.cuzoo.customer.company.Company;
import de.smartsquare.cuzoo.customer.company.CompanyRepository;
import de.smartsquare.cuzoo.customer.contact.Contact;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class CSVConverterTest {

    @Mock
    private CompanyRepository mockCompanyRepository;
    private CSVConverter csvConverter;

    @Before
    public void initialize() {
        csvConverter = new CSVConverter(mockCompanyRepository);
    }

    @Test
    public void that_csv_companies_getting_converted_correctly() {
        List<Company> companiesToConvert = this.csvConverter.getConvertedCompanies(CSVConverterTest.class.getResourceAsStream("/TestCompanies.csv"));

        assertThat(companiesToConvert, containsInAnyOrder(
                hasProperty("name", is("Anders GmbH")),
                hasProperty("name", is("Ben & Biggs AG")),
                hasProperty("name", is("Chlor Claud"))
        ));
    }

    @Test
    public void that_csv_contacts_getting_converted_correctly_when_company_exists() {
        when(mockCompanyRepository.existsByName("Anders GmbH")).thenReturn(true);
        when(mockCompanyRepository.existsByName("Ben & Biggs AG")).thenReturn(true);
        when(mockCompanyRepository.existsByName("Chlor Claud")).thenReturn(true);

        List<Contact> contactsToConvert = this.csvConverter.getConvertedContacts(CSVConverterTest.class.getResourceAsStream("/TestContacts.csv"));

        assertThat(contactsToConvert, containsInAnyOrder(
                hasProperty("name", is("Alfred Anders")),
                hasProperty("name", is("Ben Big")),
                hasProperty("name", is("Claudia Chlor"))
        ));
    }

    @Test
    public void that_csv_contacts_getting_converted_correctly_when_company_do_not_exists() {
        List<Contact> contactsToConvert = this.csvConverter.getConvertedContacts(CSVConverterTest.class.getResourceAsStream("/TestContacts.csv"));

        assertThat(contactsToConvert, containsInAnyOrder(
                hasProperty("name", is("Alfred Anders")),
                hasProperty("name", is("Ben Big")),
                hasProperty("name", is("Claudia Chlor"))
        ));
    }

    @Test
    public void that_missing_companies_of_csv_contacts_getting_inserted_correctly() {
        List<Contact> contactsToConvert = csvConverter.getConvertedContacts(CSVConverterTest.class.getResourceAsStream("/TestContacts.csv"));

        verify(mockCompanyRepository, times(2)).save(any(Company.class));
    }
}
