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
import org.springframework.transaction.support.TransactionTemplate;

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
    private final TransactionTemplate transactionTemplate;

    private Label type;

    private String type1 = "Messe";
    private String type2 = "Telefon";
    private String type3 = "Persönlich";
    private String type4 = "E-Mail";
    private String type5 = "Kontaktformular";

    private String password = "$2a$10$yRmPPUfZTDcmg32qsYrF5ORqjOVGip.H98Gi8u94u4VIucOYOWvpS";
    private User demo = new User("demo", password, "Demo Benutzer", "");

    public DemoDataLoader(
            UserRepository userRepository,
            CompanyRepository companyRepository,
            ContactRepository contactRepository,
            ContactPointRepository contactPointRepository,
            LabelRepository labelRepository,
            OpportunityRepository opportunityRepository,
            TodoRepository todoRepository,
            TransactionTemplate transactionTemplate) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.contactRepository = contactRepository;
        this.contactPointRepository = contactPointRepository;
        this.labelRepository = labelRepository;
        this.opportunityRepository = opportunityRepository;
        this.todoRepository = todoRepository;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public void afterPropertiesSet() {
        transactionTemplate.executeWithoutResult((status) -> loadData());
    }

    private void loadData() {
        userRepository.save(demo);

        this.loadMaSchinenKG();
        this.loadMuellerGmbH();
        this.loadVisionDecideUG();
        this.loadXYZAG();
        this.loadFreedolinGmbH();
        this.loadDRAutomation();
        this.loadSnowGmbH();
        this.loadMadrigalGmbH();
        this.loadFachhochschule();
    }

    private Long getTodayIncrementedBy(int increment) {
        return new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000 * increment).getTime();
    }

    private void loadMaSchinenKG() {
        Company company = new Company("Ma & Schinen KG", "Grüner Weg 123", "45678", "Entenhausen", "http://ma-schinen.kg", "", "");
        companyRepository.save(company);

        Contact contact1 = new Contact("Eduard Schinen", company, "Geschäftsführung", "schinen@ma-schinen.kg", "01234 5678910", "01234 5678910", "");
        contact1.setManager(demo);
        contactRepository.save(contact1);
        Contact contact2 = new Contact("Emilia Ma", company, "Geschäftsführung", "ma@ma-schinen.kg", "09876 543210", "09876 543210", "");
        contact2.setManager(demo);
        contactRepository.save(contact2);

        ContactPoint contactPoint1 = new ContactPoint("Erstkontakt mit Herrn Schinen", this.getTodayIncrementedBy(-14), contact1, "Herr Schinen am Messestand seines Unternehmens kennengelernt, sehr aufgeschlossen gegenüber einen Workshop zur Digitalisierung und Automatisierung von Geschäftsprozessen, Nummern ausgetauscht", "Lead", ":thumbsup:");
        contactPoint1.setCreator(demo);
        type = new Label(type1);
        type.addContactPointTypes(contactPoint1);
        contactPoint1.setTypes(Collections.singletonList(type));

        Opportunity opportunity1 = new Opportunity("Ma & Schinen Digitalisierung", "Lead", "Digitalisierung und Automatisierung von Geschäftsprozessen der Ma & Schinen KG");
        opportunityRepository.save(opportunity1);
        contactPoint1.setOpportunity(opportunity1);
        contactPointRepository.save(contactPoint1);

        ContactPoint contactPoint2 = new ContactPoint("Telefonat mit Frau Ma", this.getTodayIncrementedBy(-12), contact2, "Terminvereinbarung für Workshop zur Digitalisierung", "Prospect", "");
        contactPoint2.setCreator(demo);
        type = new Label(type2);
        type.addContactPointTypes(contactPoint2);
        contactPoint2.setTypes(Collections.singletonList(type));
        contactPoint2.setOpportunity(opportunity1);
        contactPointRepository.save(contactPoint2);

        ContactPoint contactPoint3 = new ContactPoint("Workshop mit Frau Ma", this.getTodayIncrementedBy(-7), contact2, "Workshop gut verlaufen, Frau Ma möchte sich mit Organisationsteam absprechen und dann zurückrufen", "Prospect", ":slightly_smiling_face:");
        contactPoint3.setCreator(demo);
        type = new Label(type3);
        type.addContactPointTypes(contactPoint3);
        contactPoint3.setTypes(Collections.singletonList(type));
        contactPoint3.setOpportunity(opportunity1);
        contactPointRepository.save(contactPoint3);
        opportunity1.setState(contactPoint3.getOpportunityState());
        opportunity1.setLastProgress(contactPoint3.getDate());
        opportunityRepository.save(opportunity1);

        Todo todo1 = new Todo("Frau Ma bzgl. des Worskshops kontaktieren", company, this.getTodayIncrementedBy(-2), this.getTodayIncrementedBy(-1));
        todo1.setCreator(demo);
        todoRepository.save(todo1);
    }

    private void loadMuellerGmbH() {
        Company company = new Company("Mueller GmbH & Co. KG", "Prießallee 14a", "33604", "Bielefeld", "https://mueller-gmbh.kg", "", "");
        companyRepository.save(company);

        Contact contact1 = new Contact("Bernard Mueller", company, "Geschäftsführung", "bm@mueller-gmbh.kg", "09876 543210", "09876 543210", "");
        contact1.setManager(demo);
        contactRepository.save(contact1);
        Contact contact2 = new Contact("Theo Logen", company, "Entwicklung", "tl@ma-schinen.kg", "09876 543210", "09876 543210", "");
        contact2.setManager(demo);
        contactRepository.save(contact2);

        ContactPoint contactPoint1 = new ContactPoint("Erstkontakt mit Herrn Mueller", this.getTodayIncrementedBy(-7), contact1, "Herr Mueller hat in der Firma angerufen und sich zur möglichen Umsetzung seiner Idee für eine Betriebssoftware erkundigt, Nummer des Entwicklungsleiters erhalten.", "Lead", ":thumbsup:");
        contactPoint1.setCreator(demo);
        type = labelRepository.findByTitle(type2).get();
        type.addContactPointTypes(contactPoint1);
        contactPoint1.setTypes(Collections.singletonList(type));

        Opportunity opportunity1 = new Opportunity("Betriebssoftware für Mueller GmbH", "Lead", "Betriebssoftware für internen Betrieb der Mueller GmbH");
        opportunityRepository.save(opportunity1);
        contactPoint1.setOpportunity(opportunity1);
        contactPointRepository.save(contactPoint1);

        ContactPoint contactPoint2 = new ContactPoint("Gesprächstermin vereinbart", this.getTodayIncrementedBy(-5), contact2, "Termin zum persönlichen Gespräch mit Herrn Logen vereinbart.", "Prospect", "");
        contactPoint2.setCreator(demo);
        type = labelRepository.findByTitle(type2).get();
        type.addContactPointTypes(contactPoint2);
        contactPoint2.setTypes(Collections.singletonList(type));
        contactPoint2.setOpportunity(opportunity1);
        contactPointRepository.save(contactPoint2);
        opportunity1.addContactPoint(contactPoint2);
        opportunity1.setState(contactPoint2.getOpportunityState());
        opportunityRepository.save(opportunity1);

        ContactPoint contactPoint3 = new ContactPoint("Gespräch zur Betriebssoftware", this.getTodayIncrementedBy(-3), contact2, "Er _glaubt_ mit uns ist die Umsetzung möglich und ist zuversichtlich, dass wir deren Anforderungen erfüllen können. Nach erneuter Anforderungsanalyse und Bestandsaufnahme möchte er sich melden.", "Quote", ":relaxed:");
        contactPoint3.setCreator(demo);
        type = labelRepository.findByTitle(type3).get();
        type.addContactPointTypes(contactPoint3);
        contactPoint3.setTypes(Collections.singletonList(type));
        contactPoint3.setOpportunity(opportunity1);
        contactPointRepository.save(contactPoint3);

        opportunity1.setState(contactPoint3.getOpportunityState());
        opportunity1.setLastProgress(contactPoint3.getDate());
        opportunityRepository.save(opportunity1);


        Todo todo1 = new Todo("Bei Herrn Logen nachhaken", company, this.getTodayIncrementedBy(3), this.getTodayIncrementedBy(2));
        todo1.setCreator(demo);
        todoRepository.save(todo1);

        Todo todo2 = new Todo("Angebot zur Betriebssoftware für Mueller GmbH erstellen", company, this.getTodayIncrementedBy(9), this.getTodayIncrementedBy(6));
        todo2.setCreator(demo);
        todoRepository.save(todo2);
    }

    private void loadVisionDecideUG() {
        Company company = new Company("Vision-Decide UG", "Theodor-Offen-Weg 3", "12345", "Hausendorf", "https://vision-decide.shop", "", "");
        companyRepository.save(company);

        Contact contact1 = new Contact("Alfred Berrewald", company, "Geschäftsführung", "alfred@decide.shop", "01234 5678910", "01234 5678910", "");
        contact1.setManager(demo);
        contactRepository.save(contact1);

        ContactPoint contactPoint1 = new ContactPoint("Kontaktformular von Herrn Berrewald", this.getTodayIncrementedBy(-21), contact1, "Herr Berrewald möchte seinen Shop \"Vision-Decide\" neu aufsetzen und erkundigt sich über Preiskonzept", "Lead", "");
        contactPoint1.setCreator(demo);
        type = new Label(type5);
        type.addContactPointTypes(contactPoint1);
        contactPoint1.setTypes(Collections.singletonList(type));

        Opportunity opportunity1 = new Opportunity("Vision-Decide Onlineshop", "Lead", "Neuaufsetzung des \"Vision Decide\" Shops");
        opportunity1.setLastProgress(contactPoint1.getDate());
        opportunityRepository.save(opportunity1);
        contactPoint1.setOpportunity(opportunity1);
        contactPointRepository.save(contactPoint1);
    }

    private void loadXYZAG() {
        Company company = new Company("xyz AG", "Alphabetweg 26", "54321", "Oellenbeck", "https://xyz.ag", "", "");
        companyRepository.save(company);

        Contact contact1 = new Contact("Susanne Weger", company, "Geschäftsführung", "weger@xyz.ag", "01234 5678910", "01234 5678910", "");
        contact1.setManager(demo);
        contactRepository.save(contact1);

        ContactPoint contactPoint1 = new ContactPoint("Telefonischer Erstkontakt mit Frau Weger", this.getTodayIncrementedBy(-17), contact1, "Frau Weger vergleicht Preise für Rechnungssoftware", "Lead", "");
        contactPoint1.setCreator(demo);
        type = labelRepository.findByTitle(type2).get();
        type.addContactPointTypes(contactPoint1);
        contactPoint1.setTypes(Collections.singletonList(type));

        Opportunity opportunity1 = new Opportunity("Software für Rechnungen der xyz AG", "Lead", "Software zur Buchhaltung der xyz AG");
        opportunity1.setLastProgress(contactPoint1.getDate());
        opportunityRepository.save(opportunity1);
        contactPoint1.setOpportunity(opportunity1);
        contactPointRepository.save(contactPoint1);
    }

    private void loadFreedolinGmbH() {
        Company company = new Company("Freedolin GmbH", "Torfstraße 2", "91827", "Elysium", "http://freedolin.org", "", "");
        companyRepository.save(company);

        Contact contact1 = new Contact("Fridolin Frei", company, "Geschäftsführung", "ffrei@freedolin.org", "04321 1098765", "04321 1098765", "");
        contact1.setManager(demo);
        contactRepository.save(contact1);

        ContactPoint contactPoint1 = new ContactPoint("Nachfrage von Herrn Frei bzgl. unserer Technologie", this.getTodayIncrementedBy(-9), contact1, "Herr Frei erkundigt sich für ein kompatibles Feature ihrer Java 8 Software, möchte noch andere Firmen befragen.", "Lead", "");
        contactPoint1.setCreator(demo);
        type = labelRepository.findByTitle(type2).get();
        type.addContactPointTypes(contactPoint1);
        contactPoint1.setTypes(Collections.singletonList(type));

        Opportunity opportunity1 = new Opportunity("Freedolin Feature", "Lead", "Feature für Java-8-basierte Bestandssoftware der Freedolin GmbH");
        opportunity1.setLastProgress(contactPoint1.getDate());
        opportunityRepository.save(opportunity1);
        contactPoint1.setOpportunity(opportunity1);
        contactPointRepository.save(contactPoint1);

        Todo todo1 = new Todo("Herrn Frei wegen Freedolin Feature kontaktieren", company, this.getTodayIncrementedBy(14), this.getTodayIncrementedBy(11));
        todo1.setCreator(demo);
        todoRepository.save(todo1);
    }

    private void loadDRAutomation() {
        Company company = new Company("D+R Automation e.V.", "Autonomer Weg 42", "64206", "Arkham City", "http://d&r.de", "", "");
        companyRepository.save(company);

        Contact contact1 = new Contact("Dietrich Drechsel", company, "Geschäftsführung", "drechsel@d&r.de", "01234 5678910", "01234 5678910", "");
        contact1.setManager(demo);
        contactRepository.save(contact1);

        Contact contact2 = new Contact("Rudolf Rummel", company, "Entwicklung", "rummel@d&r.de", "0123 45678910", "0123 45678910", "");
        contact2.setManager(demo);
        contactRepository.save(contact2);

        Contact contact3 = new Contact("Bettina Beschrud", company, "Buchhaltung", "info@d&r.de", "02304 1056789", "02304 1056789", "");
        contact3.setManager(demo);
        contactRepository.save(contact3);

        ContactPoint contactPoint1 = new ContactPoint("Erstkontakt mit Herrn Drechsel", this.getTodayIncrementedBy(-14), contact1, "Herrn Drechsel beim Abendessen auf der Messe kennengelernt, sucht Partner für Automationssoftware", "Lead", "");
        contactPoint1.setCreator(demo);
        type = labelRepository.findByTitle(type1).get();
        type.addContactPointTypes(contactPoint1);
        contactPoint1.setTypes(Collections.singletonList(type));

        Opportunity opportunity1 = new Opportunity("Automationssoftware D+R", "Lead", "Software zur Automation von D+R Maschinen");
        opportunity1.setLastProgress(contactPoint1.getDate());
        opportunityRepository.save(opportunity1);
        contactPoint1.setOpportunity(opportunity1);
        contactPointRepository.save(contactPoint1);

        Opportunity.Progress progress = new Opportunity.Progress("Frau Beschrud hat aufs Band gesprochen, dass Herr Drechsel und Herr Rummel an einem Termin interessiert sind.", "Prospect");
        opportunity1.addProgress(progress);
        opportunity1.setLastProgress(progress.getDate());
        opportunity1.setState(progress.getOpportunityState());
        opportunityRepository.save(opportunity1);

        Todo todo1 = new Todo("Frau Beschrud wegen des Termins zurückrufen", company, this.getTodayIncrementedBy(2), this.getTodayIncrementedBy(1));
        todo1.setCreator(demo);
        todoRepository.save(todo1);
    }

    private void loadSnowGmbH() {
        Company company = new Company("Snow GmbH & Co. KG", "Schneeweg 133", "71337", "Winterfell", "http://snow-gmbh.kg", "", "");
        companyRepository.save(company);

        Contact contact1 = new Contact("Jon Snow", company, "Geschäftsführung", "jon@snow-gmbh.kg", "0432 1098762", "0432 1098762", "");
        contact1.setManager(demo);
        contactRepository.save(contact1);

        Contact contact2 = new Contact("Arya Stark", company, "Buchhaltung", "info@snow-gmbh.kg", "0204 1056789", "0204 1056789", "");
        contact2.setManager(demo);
        contactRepository.save(contact2);

        ContactPoint contactPoint1 = new ContactPoint("Erstkontakt mit Snow GmbH & Co. KG", this.getTodayIncrementedBy(-4), contact2, "Frau Stark rief in der Firma an, sucht Entwicklung für Logisitiksoftware, Gesprächstermin vereinbart", "Lead", "");
        contactPoint1.setCreator(demo);
        type = labelRepository.findByTitle(type2).get();
        type.addContactPointTypes(contactPoint1);
        contactPoint1.setTypes(Collections.singletonList(type));

        Opportunity opportunity1 = new Opportunity("Snow Logisitiksoftware", "Lead", "Logistiksoftware zum Management aller Warenein- und ausgänge der Snow GmbH & Co. KG");
        opportunityRepository.save(opportunity1);
        contactPoint1.setOpportunity(opportunity1);
        contactPointRepository.save(contactPoint1);

        ContactPoint contactPoint2 = new ContactPoint("Persönliches Gespräch mit Herrn Snow", this.getTodayIncrementedBy(-2), contact1, "Persönliches Gespräch verlief gut, Hr. Snow ist angetan von unserem Geschäftskonzept und meldet sich nächste Woche", "Prospect", ":thumbsup:");
        contactPoint2.setCreator(demo);
        type = labelRepository.findByTitle(type3).get();
        type.addContactPointTypes(contactPoint2);
        contactPoint2.setTypes(Collections.singletonList(type));
        contactPoint2.setOpportunity(opportunity1);
        contactPointRepository.save(contactPoint2);
        opportunity1.addContactPoint(contactPoint2);
        opportunity1.setState(contactPoint2.getOpportunityState());
        opportunity1.setLastProgress(contactPoint2.getDate());
        opportunityRepository.save(opportunity1);
    }

    private void loadMadrigalGmbH() {
        Company company = new Company("Madrigal GmbH", "Darkonstraße 3", "34279", "Flaris", "http://madrigal.gmbh", "", "");
        companyRepository.save(company);

        Contact contact1 = new Contact("Dr. Franz Leif", company, "Geschäftsführung", "fleif@madrigal.gmbh", "0321 9998765", "0321 9998765", "");
        contact1.setManager(demo);
        contactRepository.save(contact1);

        ContactPoint contactPoint1 = new ContactPoint("Anfrage zur Umfragen-Auswertung von Dr. Leif", this.getTodayIncrementedBy(-1), contact1, "Herr Leif macht eine Umfrage, möchte Ergebnis-Auswertung automatisieren.", "Lead", "");
        contactPoint1.setCreator(demo);
        type = new Label(type4);
        type.addContactPointTypes(contactPoint1);
        contactPoint1.setTypes(Collections.singletonList(type));

        Opportunity opportunity1 = new Opportunity("Automatisierung von Madrigal Umfragen", "Lead", "Auswertung von Umfrageergebnissen der Madrigal GmbH");
        opportunity1.setLastProgress(contactPoint1.getDate());
        opportunityRepository.save(opportunity1);
        contactPoint1.setOpportunity(opportunity1);
        contactPointRepository.save(contactPoint1);
    }

    private void loadFachhochschule() {
        Company company = new Company("Fachhochschule", "An der Universität 1", "32431", "St. Morgen", "", "", "");
        companyRepository.save(company);

        Contact contact1 = new Contact("Klara Fall", company, "Sekretariat", "", "0221 4994762", "0221 4994762", "");
        contact1.setManager(demo);
        contactRepository.save(contact1);

        ContactPoint contactPoint1 = new ContactPoint("Anfrage zum Programm für Fallstudie", this.getTodayIncrementedBy(-3), contact1, "Frau Fall hört sich um, benötigen Software zur Umsetzung einer Fallstudie.", "Lead", "");
        contactPoint1.setCreator(demo);
        type = labelRepository.findByTitle(type4).get();
        type.addContactPointTypes(contactPoint1);
        contactPoint1.setTypes(Collections.singletonList(type));

        Opportunity opportunity1 = new Opportunity("Programm für Fallstudie", "Lead", "Software zur Umsetzung einer Fallstudie der Fachhochschule");
        opportunity1.setLastProgress(contactPoint1.getDate());
        opportunityRepository.save(opportunity1);
        contactPoint1.setOpportunity(opportunity1);
        contactPointRepository.save(contactPoint1);
    }

}
