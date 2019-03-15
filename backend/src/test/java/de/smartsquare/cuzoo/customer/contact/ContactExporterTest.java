package de.smartsquare.cuzoo.customer.contact;

import de.smartsquare.cuzoo.customer.company.Company;
import de.smartsquare.cuzoo.customer.company.CompanyRepository;
import de.smartsquare.cuzoo.customer.contactpoint.ContactPoint;
import de.smartsquare.cuzoo.customer.contactpoint.ContactPointRepository;
import de.smartsquare.cuzoo.user.User;
import de.smartsquare.cuzoo.user.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ContactExporterTest {
    private User user;
    private Company smartsquare;
    private Contact darius;
    private ContactPoint firstContactPoint;
    private ContactPoint secondContactPoint;

    private ContactExporter contactExporter;
    private SimpleDateFormat dateFormat;

    @Autowired
    private ContactPointRepository contactPointRepository;
    @Autowired
    private ContactRepository contactRepository;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private UserRepository userRepository;

    @Before
    public void initialize() {
        userRepository.deleteAll();
        user = new User("Test", "Test", "");
        userRepository.save(user);

        smartsquare = new Company("Smartsquare GmbH", "", "", "", "", "", "");
        companyRepository.save(smartsquare);

        darius = new Contact("Darius Tack", smartsquare, "Azubi", "darius@tack.de", "012345678910", "3456356453", "");
        darius.setManager(user);
        contactRepository.save(darius);

        firstContactPoint = new ContactPoint("Besprechung", 1200L, darius, "");
        secondContactPoint = new ContactPoint("Anfrage", 600000L, darius, "");
        firstContactPoint.setCreator(user);
        secondContactPoint.setCreator(user);
        contactPointRepository.save(firstContactPoint);
        contactPointRepository.save(secondContactPoint);

        dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        contactExporter = new ContactExporter();
    }

    @Test
    public void that_information_is_getting_written() {
        List<String> content = contactExporter.getContactContent(darius, contactPointRepository);

        assertThat(content.get(0).contains(darius.getName())).isTrue();
        assertThat(content.get(1).contains(darius.getCompany().getName())).isTrue();
        assertThat(content.get(2).contains(darius.getRole())).isTrue();
        assertThat(content.get(3).contains(darius.getMail())).isTrue();
        assertThat(content.get(4).contains(darius.getTelephone())).isTrue();
        assertThat(content.get(5).contains(darius.getMobile())).isTrue();
        assertThat(content.get(7).contains(firstContactPoint.getTitle())).isTrue();
        assertThat(content.get(7).contains(dateFormat.format(firstContactPoint.getDate()))).isTrue();
        assertThat(content.get(8).contains(secondContactPoint.getTitle())).isTrue();
        assertThat(content.get(8).contains(dateFormat.format(secondContactPoint.getDate()))).isTrue();
    }
}
