package de.smartsquare.cuzoo.csv.company;

import net.sf.jsefa.Deserializer;
import net.sf.jsefa.common.lowlevel.filter.HeaderAndFooterFilter;
import net.sf.jsefa.csv.CsvIOFactory;
import net.sf.jsefa.csv.config.CsvConfiguration;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

class CompanyImporter {
    private List<Company> companies;
    private Deserializer deserializer;

    CompanyImporter() {
        companies = new ArrayList<>();
        CsvConfiguration config = new CsvConfiguration();

        config.setLineFilter(new HeaderAndFooterFilter(1, false, true));
        config.getDefaultNoValueString();

        deserializer = CsvIOFactory
                .createFactory(config, Company.class)
                .createDeserializer();
    }

    List<Company> importFrom(InputStream stream) {
        try {
            deserializer.open(new InputStreamReader(stream));

            while(deserializer.hasNext()) {
                Company nextCompany = deserializer.next();

                if (nextCompany != null) {
                    companies.add(nextCompany);
                }
            }
        } finally {
            deserializer.close(true);
        }

        return companies;
    }
}
