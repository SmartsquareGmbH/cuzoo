package de.smartsquare.cuzoo.customer.contact;

import com.sun.org.apache.xml.internal.serialize.LineSeparator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class ContactExporter {
    private static final String LINE_SEPARATOR = LineSeparator.Web;

    Path exportContactToTxt(Contact contact) {
        String filename = contact.getName()
                .replace(' ', '_')
                .toLowerCase() + ".txt";

        Path path = Paths.get("src/main/resources/" + filename);
        Charset charset = Charset.forName("UTF-8");
        String content = getContactContent(contact);

        try (BufferedWriter writer = Files.newBufferedWriter(path, charset)) {
            writer.write(content);
        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }

        return path;
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
