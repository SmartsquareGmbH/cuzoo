package de.smartsquare.cuzoo.customer.points;

import de.smartsquare.cuzoo.customer.company.CompanyRepository;
import de.smartsquare.cuzoo.customer.contact.Contact;
import de.smartsquare.cuzoo.customer.contact.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/point")
public class CPointController {
    private final CPointRepository cPointRepository;
    private final AttachmentRepository attachmentRepository;
    private final ContactRepository contactRepository;
    private final CompanyRepository companyRepository;

    @Autowired
    public CPointController(final CPointRepository cPointRepository, final AttachmentRepository attachmentRepository,
                            final ContactRepository contactRepository, final CompanyRepository companyRepository) {
        this.cPointRepository = cPointRepository;
        this.attachmentRepository = attachmentRepository;
        this.contactRepository = contactRepository;
        this.companyRepository = companyRepository;
    }

    @PutMapping("/submit")
    public final ResponseEntity<?> submitContactPoint(@RequestBody @Valid CPoint cPoint,
                                                      @RequestParam("contactName") String contactName,
                                                      BindingResult bindingResult) {
        if (bindingResult.hasErrors() || !contactRepository.existsByName(contactName)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Contact cPointsContact = contactRepository.findByName(contactName);
        cPoint.setContact(cPointsContact);

        Long cPointIdBeforeSaving = cPoint.getId();

        try {
            cPointRepository.save(cPoint);
        } catch (DataAccessException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (cPointIdBeforeSaving == null) {
            return new ResponseEntity<>(HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @DeleteMapping("/delete/{cPointId}")
    public final ResponseEntity<?> deleteContactPoint(@PathVariable Long cPointId) {
        if (!cPointRepository.existsById(cPointId)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        try {
            cPointRepository.deleteById(cPointId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/upload/{companyName}/{contactPointId}")
    public final ResponseEntity<?> uploadFile(@PathVariable String companyName, @PathVariable Long contactPointId,
                                              @RequestParam("file") MultipartFile file) throws IOException {
        if (!companyRepository.existsByName(companyName) || file.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Optional<List<CPoint>> contactPointsOfCompany = cPointRepository.findCPointsByCompanyName(companyName);

        contactPointsOfCompany
                .map(it -> it.get(contactPointId.intValue()))
                .ifPresent(this::checkFiles);

        Attachment attachment = new Attachment(file.getOriginalFilename(), file.getBytes());

        contactPointsOfCompany
                .map(it -> it.get(contactPointId.intValue()))
                .ifPresent(attachment::setContactPoint);

        try {
            attachmentRepository.save(attachment);

            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (DataAccessException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void checkFiles(CPoint fileDestinationPoint) {
        if (fileDestinationPoint.getFiles() == null) {
            fileDestinationPoint.setFiles(new ArrayList<>());
        }
    }

    @GetMapping("/download/{companyName}/{contactPointId}/{filename}")
    public final ResponseEntity<byte[]> downloadFile(@PathVariable String companyName, @PathVariable Long contactPointId,
                                                     @PathVariable String filename) {
        if (!companyRepository.existsByName(companyName)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Optional<List<CPoint>> contactPointsOfCompany = cPointRepository.findCPointsByCompanyName(companyName);

        return contactPointsOfCompany
                .map(it -> ResponseEntity.ok(Objects.requireNonNull(getFile(it.get(contactPointId.intValue()).getFiles(), filename)).getContent()))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    private Attachment getFile(List<Attachment> files, String filename) {
        for (Attachment file : files) {
            if (file.getFilename().equals(filename)) {
                return file;
            }
        }

        return null;
    }

    @GetMapping("/get")
    public final ResponseEntity<List<CPoint>> getAllCPoints() {
        return ResponseEntity.ok(cPointRepository.findAll());
    }

    @GetMapping("/get/{companyName}")
    public final ResponseEntity<List<CPoint>> getCPointsOfCompany(@PathVariable String companyName) {
        if (!companyRepository.existsByName(companyName)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Optional<List<CPoint>> contactPointsOfCompany = cPointRepository.findCPointsByCompanyName(companyName);

        if (contactPointsOfCompany.isPresent()) {
            return ResponseEntity.ok(contactPointsOfCompany.get());
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
