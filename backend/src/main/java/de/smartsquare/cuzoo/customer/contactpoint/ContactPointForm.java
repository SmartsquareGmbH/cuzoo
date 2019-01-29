package de.smartsquare.cuzoo.customer.contactpoint;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

public class ContactPointForm {

    private Long id;
    @NotNull
    @NotBlank
    private String title;
    @NotNull
    @NotBlank
    private String type;
    @NotNull
    private Long date;
    private String comment;
    private List<String> labels;

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public Long getDate() {
        return date;
    }

    public String getComment() {
        return comment;
    }

    public List<String> getLabels() {
        return labels;
    }
}
