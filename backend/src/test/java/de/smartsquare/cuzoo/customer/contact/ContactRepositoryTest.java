package de.smartsquare.cuzoo.customer.contact;

import de.smartsquare.cuzoo.customer.company.Company;
import de.smartsquare.cuzoo.customer.company.CompanyRepository;
import de.smartsquare.cuzoo.customer.contact.Contact;
import de.smartsquare.cuzoo.customer.contact.ContactRepository;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ContactRepositoryTest {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @After
    public void tearDown() {
        contactRepository.deleteAll();
        companyRepository.deleteAll();
    }

    @Test
    public final void that_saves_contact_correctly() {
        Company smartsquare = new Company("Smartsquare GmbH", "", "", "", "", "", "");
        Contact darius = new Contact("Darius Tack", smartsquare, "", "", "", "", "", "");

        companyRepository.save(smartsquare);
        contactRepository.save(darius);

        assertThat(contactRepository.findAll().size()).isEqualTo(1);
    }

}
