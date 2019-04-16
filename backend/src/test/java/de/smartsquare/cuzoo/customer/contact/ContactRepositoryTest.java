package de.smartsquare.cuzoo.customer.contact;

import de.smartsquare.cuzoo.customer.company.Company;
import de.smartsquare.cuzoo.customer.company.CompanyRepository;
import de.smartsquare.cuzoo.user.User;
import de.smartsquare.cuzoo.user.UserRepository;
import org.junit.After;
import org.junit.Before;
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
    @Autowired
    private UserRepository userRepository;

    private User manager;
    private Company smartsquare;

    @Before
    public void initialize() {
        userRepository.deleteAll();
        manager = new User("mustername", "1234", "", "");
        userRepository.save(manager);

        smartsquare = new Company("Smartsquare GmbH", "", "", "", "", "", "");
        companyRepository.save(smartsquare);
    }

    @After
    public void tearDown() {
        contactRepository.deleteAll();
        companyRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public final void that_saves_contact_correctly() {
        Contact darius = new Contact("Darius Tack", smartsquare, "", "", "", "", "");
        darius.setManager(manager);

        contactRepository.save(darius);

        assertThat(contactRepository.findAll().size()).isEqualTo(1);
    }

}
