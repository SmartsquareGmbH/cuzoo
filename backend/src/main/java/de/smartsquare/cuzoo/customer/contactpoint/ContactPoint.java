package de.smartsquare.cuzoo.customer.contactpoint;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.smartsquare.cuzoo.customer.contact.Contact;
import de.smartsquare.cuzoo.customer.contactpoint.attachment.Attachment;
import de.smartsquare.cuzoo.customer.label.Label;

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
    @SequenceGenerator(name = "contactPoint_generator", sequenceName = "contactPoint_seq")
    private Long id;

    @NotNull
    @NotBlank
    private String title;

    @NotNull
    @NotBlank
    private String type;

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

    public ContactPoint() {
        this.files = new ArrayList<>();
        this.labels = new ArrayList<>();
    }

    public ContactPoint(@NotNull @NotBlank String title, @NotNull @NotBlank String type,
                        @NotNull Long date, @NotNull Contact contact, String comment) {
        this.title = title;
        this.type = type;
        this.contact = contact;
        this.date = new Date(date);
        this.comment = comment;

        this.files = new ArrayList<>();
        this.labels = new ArrayList<>();
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public void setDate(Date date) {
        this.date = date;
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
}