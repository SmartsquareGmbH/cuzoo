package de.smartsquare.cuzoo.customer;

import de.smartsquare.cuzoo.csv.CSVCompany;
import de.smartsquare.cuzoo.csv.CSVImporter;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

class CSVConverter {
    private final CSVImporter csvImporter;
    private InputStream inputFile;
    private List<Company> convertedCompanies;

    CSVConverter(MultipartFile file) throws IOException {
        inputFile = new BufferedInputStream(file.getInputStream());
        csvImporter = new CSVImporter();

        convertedCompanies = new ArrayList<>();
    }

    List<Company> getConvertedCompanies() {
        List<CSVCompany> companiesToConvert = csvImporter.importFrom(inputFile, CSVCompany.class);

        for (CSVCompany csvCompany : companiesToConvert) {
            Company company = new Company(
                    csvCompany.getCompany(),
                    csvCompany.getStreet(),
                    csvCompany.getZipCode(),
                    csvCompany.getPlace(),
                    csvCompany.getHomepage(),
                    csvCompany.getPurpose(),
                    csvCompany.getOther());

            company.setStatus("Lead");
            convertedCompanies.add(company);
        }

        return convertedCompanies;
    }
}