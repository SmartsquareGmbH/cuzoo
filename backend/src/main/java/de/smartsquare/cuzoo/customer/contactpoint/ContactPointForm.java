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
    private String rating;

    public ContactPointForm(Long id, @NotNull @NotBlank String title, @NotNull Long date, @NotNull List<String> types,
                            List<String> labels, @NotNull @NotBlank String creator, String comment,
                            String opportunityState, String rating) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.types = types;
        this.labels = labels;
        this.creator = creator;
        this.comment = comment;
        this.opportunityState = opportunityState;
        this.rating = rating;
    }

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

    public String getOpportunityState() {
        return opportunityState;
    }

    public String getRating() {
        return rating;
    }

    public String getCreator() {
        return creator;
    }

    public List<String> getLabels() {
        return labels;
    }
}
