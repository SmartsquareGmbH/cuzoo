package de.smartsquare.cuzoo.demo;

import de.smartsquare.cuzoo.user.User;
import de.smartsquare.cuzoo.user.UserRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("demo")
public class DemoDataLoader implements InitializingBean {

    private final UserRepository userRepository;

    public DemoDataLoader(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void afterPropertiesSet() {
        loadData();
    }

    private void loadData() {
        String password = "$2a$10$yRmPPUfZTDcmg32qsYrF5ORqjOVGip.H98Gi8u94u4VIucOYOWvpS";
        User demo = new User("demo", password, "Demo Benutzer", "demo@cuzoo.de");
        userRepository.save(demo);
    }
}
