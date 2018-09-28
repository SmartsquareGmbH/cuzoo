package de.smartsquare.cuzoo.csv;

import net.sf.jsefa.Deserializer;
import net.sf.jsefa.common.lowlevel.filter.HeaderAndFooterFilter;
import net.sf.jsefa.csv.CsvIOFactory;
import net.sf.jsefa.csv.config.CsvConfiguration;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

class CSVImporter {
    private CsvConfiguration config;

    CSVImporter() {
        config = new CsvConfiguration();

        config.setLineFilter(new HeaderAndFooterFilter(1, false, true));
        config.getDefaultNoValueString();
    }

    <T> List<T> importFrom(InputStream stream, Class<T> typeClass) {
        List<T> results = new ArrayList<>();

        Deserializer deserializer = CsvIOFactory
                .createFactory(config, typeClass)
                .createDeserializer();

         try {
            deserializer.open(new InputStreamReader(stream));

            while(deserializer.hasNext()) {
                T nextType = deserializer.next();

                if (nextType != null) {
                    results.add(nextType);
                }
            }
        } finally {
            deserializer.close(true);
        }

        return results;
    }
}
