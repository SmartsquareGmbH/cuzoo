package de.smartsquare.cuzoo.user;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Before
    public void initialize() {
        userRepository.deleteAll();
    }

    @Test
    public final void that_saves_user_successfully() {
        User user = new User("drs", "1234", "drs tck", "drs@t.ck");

        userRepository.save(user);

        assertThat(userRepository.findAll().size()).isEqualTo(1);
    }
}
