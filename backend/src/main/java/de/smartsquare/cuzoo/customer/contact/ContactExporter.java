package de.smartsquare.cuzoo.customer.contact;

import com.sun.org.apache.xml.internal.serialize.LineSeparator;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
class ContactExporter {
    private static final String LINE_SEPARATOR = LineSeparator.Web;
    private final ContactRepository contactRepository;
    private String filename;
    private Path file;
    private Charset charset;
    private String content;

    ContactExporter(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    Path exportContactToTxt(Long contactId) {
        contactRepository.findById(contactId).ifPresent(contact -> {
            filename = contact.getName()
                    .replace(' ', '_')
                    .toLowerCase() + ".txt";

            file = Paths.get("src/main/resources/" + filename);
            charset = Charset.forName("UTF-8");
            content = getContactContent(contact);
        });

        try (BufferedWriter writer = Files.newBufferedWriter(file, charset)) {
            writer.write(content);
        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }

        return file;
    }

    private String getContactContent(Contact contact) {
        return "Name:\t\t " + contact.getName() + LINE_SEPARATOR +
                "Unternehmen: " + contact.getCompany().getName() + LINE_SEPARATOR +
                "Rolle:\t\t " + contact.getRole() + LINE_SEPARATOR +
                "E-Mail:\t\t " + contact.getMail() + LINE_SEPARATOR +
                "Telefon:\t " + contact.getTelephone() + LINE_SEPARATOR +
                "Kommentar:\t " + contact.getComment() + LINE_SEPARATOR;
    }
}
