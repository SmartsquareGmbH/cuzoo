package de.smartsquare.cuzoo.customer.contact;

import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
class ContactExporter {
    Path exportContactToTxt(Contact contact) {
        String filename = contact.getName()
                .replace(' ', '_')
                .toLowerCase() + ".txt";

        Path file = Paths.get("src/main/resources/" + filename);
        Charset charset = Charset.forName("UTF-8");
        String content = getContactContent(contact);

        try (BufferedWriter writer = Files.newBufferedWriter(file, charset)) {
            writer.write(content);
        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }

        return file;
    }

    private String getContactContent(Contact contact) {
        return "Name:\t\t " + contact.getName() + System.lineSeparator() +
                "Unternehmen:\t " + contact.getCompany().getName() + System.lineSeparator() +
                "Rolle:\t\t " + contact.getRole() + System.lineSeparator() +
                "E-Mail:\t\t " + contact.getMail() + System.lineSeparator() +
                "Telefon:\t " + contact.getTelephone() + System.lineSeparator() +
                "Kommentar:\t " + contact.getComment() + System.lineSeparator();
    }
}
