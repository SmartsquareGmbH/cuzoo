package de.smartsquare.cuzoo.customer.contact;

import de.smartsquare.cuzoo.customer.contactpoint.ContactPoint;
import de.smartsquare.cuzoo.customer.contactpoint.ContactPointRepository;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
class ContactExporter {
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    List<String> getContactContent(Contact contact, ContactPointRepository contactPointRepository) {
        List<String> lines = new ArrayList<>();
        List<ContactPoint> contactsContactPoints = contactPointRepository
                .findAll()
                .stream()
                .filter(contactPoint -> contactPoint.getContact().getId().equals(contact.getId()))
                .collect(Collectors.toList());

        lines.add("Name:\t\t " + contact.getName());
        lines.add("Unternehmen:\t " + contact.getCompany().getName());
        lines.add("Rolle:\t\t " + contact.getRole());
        lines.add("E-Mail:\t\t " + contact.getMail());
        lines.add("Telefon:\t " + contact.getTelephone());
        lines.add("Mobil:\t\t " + contact.getMobile() + "\n");
        lines.add("Kontaktpunkte:");

        contactsContactPoints.forEach(contactPoint ->
                lines.add(contactPoint.getTitle() + " am " + dateFormat.format(contactPoint.getDate()))
        );

        return lines;
    }
}
