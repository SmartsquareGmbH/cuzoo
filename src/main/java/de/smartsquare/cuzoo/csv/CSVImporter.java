package de.smartsquare.cuzoo.csv;

import net.sf.jsefa.Deserializer;
import net.sf.jsefa.common.lowlevel.filter.HeaderAndFooterFilter;
import net.sf.jsefa.csv.CsvIOFactory;
import net.sf.jsefa.csv.config.CsvConfiguration;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

class CSVImporter<T> {
    private Deserializer deserializer;

    CSVImporter(Class<T> typeClass) {
        CsvConfiguration config = new CsvConfiguration();

        config.setLineFilter(new HeaderAndFooterFilter(1, false, true));
        config.getDefaultNoValueString();

        deserializer = CsvIOFactory
                .createFactory(config, typeClass)
                .createDeserializer();
    }

    List<T> importFrom(InputStream stream) {
        List<T> results = new ArrayList<>();

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
