package de.smartsquare.cuzoo.customer.contact;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
class ContactExporter {
    List<String> getContactContent(Contact contact) {
        List<String> lines = new ArrayList<>();

        lines.add("Name:\t\t " + contact.getName());
        lines.add("Unternehmen:\t " + contact.getCompany().getName());
        lines.add("Rolle:\t\t " + contact.getRole());
        lines.add("E-Mail:\t\t " + contact.getMail());
        lines.add("Telefon:\t " + contact.getTelephone());
        lines.add("Kommentar:\t " + contact.getComment());

        return lines;
    }
}
