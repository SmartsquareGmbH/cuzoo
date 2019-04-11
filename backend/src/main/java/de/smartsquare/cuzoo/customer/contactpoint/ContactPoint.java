package de.smartsquare.cuzoo.customer.contactpoint;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.smartsquare.cuzoo.customer.contact.Contact;
import de.smartsquare.cuzoo.customer.contactpoint.attachment.Attachment;
import de.smartsquare.cuzoo.customer.label.Label;
import de.smartsquare.cuzoo.customer.opportunity.Opportunity;
import de.smartsquare.cuzoo.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class ContactPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "contactPoint_generator")
    @SequenceGenerator(name = "contactPoint_generator", sequenceName = "contactPoint_seq", allocationSize = 1)
    private Long id;

    @NotNull
    @NotBlank
    private String title;

    @NotNull
    private Date date;

    @ManyToOne
    @JoinColumn(name = "contact_id")
    private Contact contact;

    @Column(length = 510)
    private String comment;

    @OneToMany(mappedBy = "contactPoint", cascade = CascadeType.REMOVE)
    @JsonIgnore
    @Column(length = 9999999)
    private List<Attachment> files;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            })
    @JoinTable(name = "contactPoint_labels",
            joinColumns = {@JoinColumn(name = "contactPoint_id")},
            inverseJoinColumns = {@JoinColumn(name = "label_id")})
    private List<Label> labels;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            })
    @JoinTable(name = "contactPoint_types",
            joinColumns = {@JoinColumn(name = "contactPoint_id")},
            inverseJoinColumns = {@JoinColumn(name = "label_id")})
    private List<Label> types;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    @ManyToOne
    @JoinColumn(name = "opportunity_id")
    private Opportunity opportunity;

    public ContactPoint() {
        this.files = new ArrayList<>();
        this.labels = new ArrayList<>();
        this.types = new ArrayList<>();
    }

    public ContactPoint(@NotNull @NotBlank String title, @NotNull Long date,
                        @NotNull Contact contact, String comment) {
        this.title = title;
        this.contact = contact;
        this.date = new Date(date);
        this.comment = comment;

        this.files = new ArrayList<>();
        this.labels = new ArrayList<>();
        this.types = new ArrayList<>();
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

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = new Date(date);
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<Attachment> getFiles() {
        return files;
    }

    public void setFiles(List<Attachment> files) {
        this.files = files;
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

    public List<Label> getTypes() {
        return types;
    }

    public void setTypes(List<Label> types) {
        this.types = types;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public Opportunity getOpportunity() {
        return opportunity;
    }

    public void setOpportunity(Opportunity opportunity) {
        this.opportunity = opportunity;
    }
}