package de.smartsquare.cuzoo.customer.contact;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

class ContactExporter {
    private List<String> linesToWrite;
    private File file;
    private FileWriter fileWriter;

    ContactExporter() {
        linesToWrite = new ArrayList<>();
    }

    File exportContactToTxt(Contact contact) {
        String filename = contact.getName()
                .replace(' ', '_')
                .toLowerCase() + ".txt";

        file = new File(filename);

        return file;
    }

}
