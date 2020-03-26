package de.smartsquare.cuzoo.customer.opportunity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.smartsquare.cuzoo.customer.contactpoint.ContactPoint;
import de.smartsquare.cuzoo.customer.label.Label;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Opportunity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "opportunity_generator")
    @SequenceGenerator(name = "opportunity_generator", sequenceName = "opportunity_seq", allocationSize = 1)
    private Long id;

    @NotNull
    @NotBlank
    private String title;

    @NotNull
    @NotBlank
    private String state;

    private String description;
    private Date lastProgress;

    @ElementCollection
    private List<Progress> progress;

    @OneToMany(mappedBy = "opportunity", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<ContactPoint> contactPoints;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            })
    @JoinTable(name = "opportunity_labels",
            joinColumns = {@JoinColumn(name = "opportunity_id")},
            inverseJoinColumns = {@JoinColumn(name = "label_id")})
    private List<Label> labels;

    public Opportunity(@NotNull @NotBlank String title, @NotNull @NotBlank String state, String description) {
        this.title = title;
        this.state = state;
        this.description = description;

        this.contactPoints = new ArrayList<>();
        this.progress = new ArrayList<>();
    }

    public Opportunity() {
        this.contactPoints = new ArrayList<>();
        this.progress = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getLastProgress() {
        return lastProgress;
    }

    public void setLastProgress(Date lastProgress) {
        this.lastProgress = lastProgress;
    }

    public List<ContactPoint> getContactPoints() {
        return contactPoints;
    }

    public void setContactPoints(List<ContactPoint> contactPoints) {
        this.contactPoints = contactPoints;
    }

    public void addContactPoint(ContactPoint contactPoint) {
        this.contactPoints.add(contactPoint);
    }

    public List<Progress> getProgress() {
        return progress;
    }

    public void setProgress(List<Progress> progress) {
        this.progress = progress;
    }

    public void addProgress(Progress progress) {
        this.progress.add(progress);
    }

    public List<Label> getLabels() {
        return labels;
    }

    public void setLabels(List<Label> labels) {
        this.labels = labels;
    }

    public void addLabel(Label label) {
        this.labels.add(label);
    }

    @Embeddable
    public static class Progress {

        @NotNull @NotBlank
        private String opportunityState;

        private Date date;
        private String progressText;

        public Progress() { }

        public Progress(String progressText, @NotNull @NotBlank String opportunityState) {
            this.progressText = progressText;
            this.opportunityState = opportunityState;
            this.date = new Date();
        }

        public String getOpportunityState() { return this.opportunityState; }

        public void setOpportunityState(String opportunityState) { this.opportunityState = opportunityState; }

        public String getProgressText() { return this.progressText; }

        public void setProgressText(String progressText) { this.progressText = progressText; }

        public Date getDate() { return this.date; }

        public void setDate(Date date) { this.date = date; }
    }
}
