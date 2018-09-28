package de.smartsquare.cuzoo.csv;

import net.sf.jsefa.csv.annotation.CsvDataType;
import net.sf.jsefa.csv.annotation.CsvField;

@CsvDataType()
public class Company {
    @CsvField(pos = 1)
    private String unternehmen;

    @CsvField(pos = 2)
    private String strasse;

    @CsvField(pos = 3)
    private String postleitzahl;

    @CsvField(pos = 4)
    private String ort;

    @CsvField(pos = 5)
    private String homepage;

    @CsvField(pos = 6)
    private String unternehmenszweck;

    @CsvField(pos = 7)
    private String sonstiges;

    public String getUnternehmen() {
        return unternehmen;
    }

    public void setUnternehmen(String unternehmen) {
        this.unternehmen = unternehmen;
    }

    public String getStrasse() {
        return strasse;
    }

    public void setStrasse(String strasse) {
        this.strasse = strasse;
    }

    public String getPostleitzahl() {
        return postleitzahl;
    }

    public void setPostleitzahl(String postleitzahl) {
        this.postleitzahl = postleitzahl;
    }

    public String getOrt() {
        return ort;
    }

    public void setOrt(String ort) {
        this.ort = ort;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public String getUnternehmenszweck() {
        return unternehmenszweck;
    }

    public void setUnternehmenszweck(String unternehmenszweck) {
        this.unternehmenszweck = unternehmenszweck;
    }

    public String getSonstiges() {
        return sonstiges;
    }

    public void setSonstiges(String sonstiges) {
        this.sonstiges = sonstiges;
    }
}
