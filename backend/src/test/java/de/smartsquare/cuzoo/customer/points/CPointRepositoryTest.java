package de.smartsquare.cuzoo.customer.points;

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
public class CPointRepositoryTest {

    @Autowired
    private CPointRepository cPointRepository;

    @Autowired
    private ContactRepository contactRepository;

    @After
    public void tearDown() throws Exception {
        cPointRepository.deleteAll();
        contactRepository.deleteAll();
    }

    @Test
    public final void that_saves_contact_point_correctly() {
        Contact contact = new Contact("Darius Tack", "", "", "", "", "", "");
        CPoint cPoint = new CPoint("Beratung", "Telefon", contact, "", "");

        contactRepository.save(contact);
        cPointRepository.save(cPoint);

        assertThat(cPointRepository.findAll().size()).isEqualTo(1);
    }
}
