package de.smartsquare.cuzoo.demo;

import de.smartsquare.cuzoo.customer.company.Company;
import de.smartsquare.cuzoo.customer.company.CompanyRepository;
import de.smartsquare.cuzoo.customer.contact.Contact;
import de.smartsquare.cuzoo.customer.contact.ContactRepository;
import de.smartsquare.cuzoo.customer.contactpoint.ContactPoint;
import de.smartsquare.cuzoo.customer.contactpoint.ContactPointRepository;
import de.smartsquare.cuzoo.customer.label.Label;
import de.smartsquare.cuzoo.customer.label.LabelRepository;
import de.smartsquare.cuzoo.customer.opportunity.Opportunity;
import de.smartsquare.cuzoo.customer.opportunity.OpportunityRepository;
import de.smartsquare.cuzoo.customer.todo.Todo;
import de.smartsquare.cuzoo.customer.todo.TodoRepository;
import de.smartsquare.cuzoo.user.User;
import de.smartsquare.cuzoo.user.UserRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;

@Service
@Profile("demo")
public class DemoDataLoader implements InitializingBean {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final ContactRepository contactRepository;
    private final ContactPointRepository contactPointRepository;
    private final LabelRepository labelRepository;
    private final OpportunityRepository opportunityRepository;
    private final TodoRepository todoRepository;

    public DemoDataLoader(
            UserRepository userRepository,
            CompanyRepository companyRepository,
            ContactRepository contactRepository,
            ContactPointRepository contactPointRepository,
            LabelRepository labelRepository,
            OpportunityRepository opportunityRepository,
            TodoRepository todoRepository) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.contactRepository = contactRepository;
        this.contactPointRepository = contactPointRepository;
        this.labelRepository = labelRepository;
        this.opportunityRepository = opportunityRepository;
        this.todoRepository = todoRepository;
    }

    @Override
    public void afterPropertiesSet() {
        loadData();
    }

    private void loadData() {
        Long yesterday = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000).getTime();
        Long tomorrow = new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000).getTime();
        Long inTwoDays = new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000 * 2).getTime();

        String password = "$2a$10$yRmPPUfZTDcmg32qsYrF5ORqjOVGip.H98Gi8u94u4VIucOYOWvpS";
        User demoUser = new User("demoUser", password, "Demo Benutzer", "demo@cuzoo.de");
        userRepository.save(demoUser);

        Company company1 = new Company("Ma & Schinen KG", "Grüner Weg 123", "45678", "Entenhausen", "http://ma-schinen.kg", "~", "~");
        companyRepository.save(company1);

        Contact contact1 = new Contact("Eduard Schinen", company1, "Geschäftsführung", "schinen@ma-schinen.kg", "01234 5678910", "01234 5678910", "~");
        contact1.setManager(demoUser);
        contactRepository.save(contact1);
        Contact contact2 = new Contact("Emilia Ma", company1, "Geschäftsführung", "ma@ma-schinen.kg", "09876 543210", "09876 543210", "~");
        contact2.setManager(demoUser);
        contactRepository.save(contact2);

        ContactPoint contactPoint1 = new ContactPoint("Erstkontakt mit Herrn Schinen", yesterday, contact1, "~", "Lead", ":thumbsup:");
        contactPoint1.setCreator(demoUser);
        Label type1 = new Label("Messe");
        type1.addContactPointTypes(contactPoint1);
        labelRepository.save(type1);
        contactPoint1.setTypes(Collections.singletonList(type1));
        contactPointRepository.save(contactPoint1);

        Opportunity opportunity1 = new Opportunity("Ma & Schinen Digitalisierung", "Lead", "~");
        opportunityRepository.save(opportunity1);
        contactPoint1.setOpportunity(opportunity1);
        contactPointRepository.save(contactPoint1);

        ContactPoint contactPoint2 = new ContactPoint("Workshop mit Frau Ma", yesterday, contact2, "~", "Prospect", "");
        contactPoint2.setCreator(demoUser);
        Label type2 = new Label("Persönlich");
        type2.addContactPointTypes(contactPoint2);
        labelRepository.save(type2);
        contactPoint1.setTypes(Collections.singletonList(type2));
        contactPoint2.setOpportunity(opportunity1);
        contactPointRepository.save(contactPoint2);

        opportunity1.setState(contactPoint2.getOpportunityState());
        opportunityRepository.save(opportunity1);

        Todo todo1 = new Todo("Frau Ma bzgl. des Worskshops zurückrufen", company1, inTwoDays, tomorrow);
        todo1.setCreator(demoUser);
        todoRepository.save(todo1);
    }
}
