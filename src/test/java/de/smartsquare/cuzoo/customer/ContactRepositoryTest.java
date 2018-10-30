package de.smartsquare.cuzoo.customer;

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

    @Test
    public final void  that_saves_contact_correctly() {
        Contact darius = new Contact("Darius Tack", "Tack GmbH", "", "", "", "", "", "", "", "", "", "", "");

        contactRepository.save(darius);

        assertThat(contactRepository.findAll().size()).isEqualTo(1);
    }

}
