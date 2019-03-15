package de.smartsquare.cuzoo.customer.contact;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

public class ContactForm {
    private Long id;

    @NotNull
    @NotBlank
    private String name;

    @NotNull
    @NotBlank
    private String manager;

    private String role;
    private String mail;
    private String telephone;
    private String mobile;
    private String comment;

    private List<String> labels;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getManager() {
        return manager;
    }

    public String getRole() {
        return role;
    }

    public String getMail() {
        return mail;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getMobile() {
        return mobile;
    }

    public String getComment() {
        return comment;
    }

    public List<String> getLabels() {
        return labels;
    }
}
