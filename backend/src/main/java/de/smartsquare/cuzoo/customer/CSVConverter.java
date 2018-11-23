package de.smartsquare.cuzoo.customer;

import de.smartsquare.cuzoo.csv.CSVCompany;
import de.smartsquare.cuzoo.csv.CSVImporter;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
class CSVConverter {
    private final CSVImporter csvImporter;
    private List<Company> convertedCompanies;

    private final CompanyRepository companyRepository;

    CSVConverter(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;

        csvImporter = new CSVImporter();
        convertedCompanies = new ArrayList<>();
    }

    List<Company> getConvertedCompanies(InputStream file) {
        InputStream inputFile = new BufferedInputStream(file);

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