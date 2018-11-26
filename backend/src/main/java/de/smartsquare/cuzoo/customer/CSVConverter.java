package de.smartsquare.cuzoo.customer;

import de.smartsquare.cuzoo.csv.CSVCompany;
import de.smartsquare.cuzoo.csv.CSVContact;
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
    private List<Contact> convertedContacts;

    private final CompanyRepository companyRepository;

    CSVConverter(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;

        csvImporter = new CSVImporter();
        convertedCompanies = new ArrayList<>();
        convertedContacts = new ArrayList<>();
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

    List<Contact> getConvertedContacts(InputStream file) {
        InputStream inputFile = new BufferedInputStream(file);

        List<CSVContact> contactsToConvert = csvImporter.importFrom(inputFile, CSVContact.class);

        for (CSVContact csvContact : contactsToConvert) {
            Company companyOfContact;
            Contact contact;

            if (csvContact.getCompany() == null && csvContact.getRole().equals("Freiberufler")) {
                contact = new Contact(
                        csvContact.getName(),
                        csvContact.getRole(),
                        csvContact.getMail(),
                        csvContact.getTelephone(),
                        csvContact.getLastContact(),
                        csvContact.getLastAnswer(),
                        csvContact.getComment());
            } else {
                if (companyRepository != null && !companyRepository.existsByName(csvContact.getCompany())) {
                    companyOfContact = new Company(csvContact.getCompany(), "", "", "", "", "", "");
                    companyOfContact.setStatus("Lead");
                } else {
                    companyOfContact = companyRepository.findByName(csvContact.getCompany());
                }
                companyRepository.save(companyOfContact);
                contact = new Contact(
                        csvContact.getName(),
                        companyOfContact,
                        csvContact.getRole(),
                        csvContact.getMail(),
                        csvContact.getTelephone(),
                        csvContact.getLastContact(),
                        csvContact.getLastAnswer(),
                        csvContact.getComment());
            }

            convertedContacts.add(contact);
        }

        return convertedContacts;
    }
}