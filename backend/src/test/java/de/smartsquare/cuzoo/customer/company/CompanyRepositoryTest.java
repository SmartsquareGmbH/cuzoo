package de.smartsquare.cuzoo.customer.company;

import de.smartsquare.cuzoo.customer.company.Company;
import de.smartsquare.cuzoo.customer.company.CompanyRepository;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CompanyRepositoryTest {

    @Autowired
    private CompanyRepository companyRepository;

    @After
    public void tearDown() throws Exception {
        companyRepository.deleteAll();
    }

    @Test
    public final void that_saves_company_correctly() {
        Company darius = new Company("Tack GmbH",  "", "", "", "", "", "");

        companyRepository.save(darius);

        assertThat(companyRepository.findAll().size()).isEqualTo(1);
    }

}
