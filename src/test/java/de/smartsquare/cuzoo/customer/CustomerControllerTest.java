package de.smartsquare.cuzoo.customer;

import de.smartsquare.cuzoo.csv.CSVImporter;
import de.smartsquare.cuzoo.csv.CSVImporterTest;
import de.smartsquare.cuzoo.csv.Company;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CustomerControllerTest {

    @Mock
    private CustomerRepository mockCustomerRepository;
    @InjectMocks
    private CustomerController customerController;

    @Test
    public void that_imported_companies_getting_inserted_correctly() {
        CSVImporter csvImporter = new CSVImporter();

        List<Company> companies = csvImporter.importFrom(CSVImporterTest.class.getResourceAsStream("/TestCompanies.csv"), Company.class);

        customerController.insertImportedCompanies(companies);

        verify(mockCustomerRepository, times(3)).save(any(Customer.class));
    }

}
