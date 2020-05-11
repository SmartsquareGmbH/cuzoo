package de.smartsquare.cuzoo.user;

public class UserInformationForm {

    private String fullname;
    private String mail;

    public UserInformationForm(String fullname, String mail) {
        this.fullname = fullname;
        this.mail = mail;
    }

    public String getFullname() { return fullname; }

    public String getMail() { return mail; }
}
