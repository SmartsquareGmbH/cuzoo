package de.smartsquare.cuzoo.customer.points.attachment;

import de.smartsquare.cuzoo.customer.points.ContactPoint;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "attachment_generator")
    @SequenceGenerator(name = "attachment_generator", sequenceName = "attachment_seq")
    private Long id;

    @NotNull
    @NotBlank
    private String filename;

    @NotNull
    @Column(length = 9999999)
    private byte[] content;
    private LocalDate uploadDate;

    @ManyToOne
    @JoinColumn(name = "contactPoint_id")
    private ContactPoint contactPoint;

    public Attachment() {

    }

    Attachment(@NotNull @NotBlank String filename, @NotNull byte[] content) {
        this.filename = filename;
        this.content = content;
        this.uploadDate = LocalDate.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public LocalDate getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDate uploadDate) {
        this.uploadDate = uploadDate;
    }

    public ContactPoint getContactPoint() {
        return contactPoint;
    }

    public void setContactPoint(ContactPoint contactPoint) {
        this.contactPoint = contactPoint;
    }
}
