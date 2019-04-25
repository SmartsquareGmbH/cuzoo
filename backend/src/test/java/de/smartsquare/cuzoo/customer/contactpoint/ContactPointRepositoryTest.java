package de.smartsquare.cuzoo.customer.contactpoint;

import de.smartsquare.cuzoo.customer.contact.Contact;
import de.smartsquare.cuzoo.customer.contact.ContactRepository;
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
public class ContactPointRepositoryTest {

    @Autowired
    private ContactPointRepository contactPointRepository;
    @Autowired
    private ContactRepository contactRepository;
    @Autowired
    private UserRepository userRepository;

    private User user;
    private Contact contact;
    private ContactPoint contactPoint;

    @Before
    public void initialize() {
        userRepository.deleteAll();
        user = new User("user", "1234", "", "");
        userRepository.save(user);

        contact = new Contact("Darius Tack", "", "", "", "", "");
        contact.setManager(user);
        contactRepository.save(contact);
    }

    @After
    public void tearDown() throws Exception {
        contactPointRepository.deleteAll();
        contactRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public final void that_saves_contact_point_correctly() {
        ContactPoint contactPoint = new ContactPoint("Beratung", 0L, contact, "", "", "");
        contactPoint.setCreator(user);

        contactPointRepository.save(contactPoint);

        assertThat(contactPointRepository.findAll().size()).isEqualTo(1);
    }
}
