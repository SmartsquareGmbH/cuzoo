package de.smartsquare.cuzoo.customer;

import de.smartsquare.cuzoo.csv.CSVImporter;
import de.smartsquare.cuzoo.csv.CSVImporterTest;
import de.smartsquare.cuzoo.csv.CSVCompany;
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
public class CompanyControllerTest {

    @Mock
    private CompanyRepository mockCompanyRepository;
    @InjectMocks
    private CompanyController companyController;

    @Test
    public void that_imported_companies_getting_inserted_correctly() {
        CSVImporter csvImporter = new CSVImporter();

        List<CSVCompany> companies = csvImporter.importFrom(CSVImporterTest.class.getResourceAsStream("/TestCompanies.csv"), CSVCompany.class);

        companyController.insertImportedCompanies(companies);

        verify(mockCompanyRepository, times(3)).save(any(Company.class));
    }

}
