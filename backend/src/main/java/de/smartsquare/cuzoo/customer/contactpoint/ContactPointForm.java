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
    private Long date;
    @NotNull
    private List<String> types;
    private List<String> labels;

    @NotNull
    @NotBlank
    private String creator;
    private String comment;
    private String opportunityState;

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getTypes() {
        return types;
    }

    public Long getDate() {
        return date;
    }

    public String getComment() {
        return comment;
    }

    public String getOpportunityState() { return opportunityState; }

    public String getCreator() {
        return creator;
    }

    public List<String> getLabels() {
        return labels;
    }
}
