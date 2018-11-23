package de.smartsquare.cuzoo.customer;

import de.smartsquare.cuzoo.csv.CSVCompany;
import de.smartsquare.cuzoo.csv.CSVContact;
import de.smartsquare.cuzoo.csv.CSVImporter;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

class CustomerInserter {
    private final CSVImporter csvImporter;
    private InputStream inputFile;
    private List<Company> insertedCompanies;
    private List<Contact> insertedContacts;

    CustomerInserter(MultipartFile file) throws IOException {
        inputFile = new BufferedInputStream(file.getInputStream());
        csvImporter = new CSVImporter();

        insertedCompanies = new ArrayList<>();
        insertedContacts = new ArrayList<>();
    }

    List<Company> getInsertedCompanies() {
        List<CSVCompany> companiesToInsert = csvImporter.importFrom(inputFile, CSVCompany.class);

        for (CSVCompany csvCompany : companiesToInsert) {
            Company company = new Company(
                    csvCompany.getCompany(),
                    csvCompany.getStreet(),
                    csvCompany.getZipCode(),
                    csvCompany.getPlace(),
                    csvCompany.getHomepage(),
                    csvCompany.getPurpose(),
                    csvCompany.getOther());

            company.setStatus("Lead");
            insertedCompanies.add(company);
        }

        return insertedCompanies;
    }
}