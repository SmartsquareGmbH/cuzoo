package de.smartsquare.cuzoo.customer;

import de.smartsquare.cuzoo.csv.CSVContact;
import de.smartsquare.cuzoo.csv.CSVImporter;
import de.smartsquare.cuzoo.csv.CSVImporterTest;
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
public class ContactControllerTest {

    @Mock
    private ContactRepository mockContactRepository;
    @Mock
    private CompanyRepository mockCompanyRepository;
    @InjectMocks
    private ContactController contactController;

    @Test
    public void that_imported_contacts_getting_inserted_correctly() {
        CSVImporter csvImporter = new CSVImporter();

        List<CSVContact> contacts = csvImporter.importFrom(CSVImporterTest.class.getResourceAsStream("/TestContacts.csv"), CSVContact.class);

        contactController.insertImportedContactsWithMissingCompanies(contacts);

        verify(mockContactRepository, times(3)).save(any(Contact.class));
    }

}
