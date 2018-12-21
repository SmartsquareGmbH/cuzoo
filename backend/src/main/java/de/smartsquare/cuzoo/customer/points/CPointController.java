package de.smartsquare.cuzoo.customer.points;

import de.smartsquare.cuzoo.customer.company.CompanyRepository;
import de.smartsquare.cuzoo.customer.contact.Contact;
import de.smartsquare.cuzoo.customer.contact.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/point")
public class CPointController {
    private final CPointRepository cPointRepository;
    private final AttachmentRepository attachmentRepository;
    private final ContactRepository contactRepository;
    private final CompanyRepository companyRepository;

    private final Comparator<CPoint> compareDates = (firstPoint, secondPoint) -> {
        if (firstPoint.getDate().equals(secondPoint.getDate())) {
            if (firstPoint.getId() > secondPoint.getId()) {
                return -1;
            } else {
                return 0;
            }
        } else {
            return firstPoint.getDate().compareTo(secondPoint.getDate());
        }
    };

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

        CPoint fileDestinationPoint = cPointRepository.findAll()
                .stream()
                .filter(contactPoint -> contactPoint.getContact().getCompany().getName().equals(companyName))
                .sorted(compareDates)
                .collect(Collectors.toList())
                .get(contactPointId.intValue());

        if (fileDestinationPoint.getFiles() == null) {
            fileDestinationPoint.setFiles(new ArrayList<>());
        }

        Attachment attachment = new Attachment(file.getOriginalFilename(), file.getBytes());
        attachment.setContactPoint(fileDestinationPoint);

        try {
            attachmentRepository.save(attachment);

            cPointRepository.findAll().forEach(point -> point.getFiles().forEach(att -> System.out.println(att.getFilename())));

            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (DataAccessException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/download/{companyName}/{contactPointId}/{filename}")
    public final ResponseEntity<Resource> downloadFile(@PathVariable String companyName, @PathVariable Long contactPointId,
                                                       @PathVariable String filename) {
        if (!companyRepository.existsByName(companyName)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        cPointRepository.findAll().forEach(point -> point.getFiles().forEach(att -> System.out.println(att.getFilename())));

        CPoint fileOriginPoint = cPointRepository.findAll()
                .stream()
                .filter(contactPoint -> contactPoint.getContact().getCompany().getName().equals(companyName))
                .sorted(compareDates)
                .collect(Collectors.toList())
                .get(contactPointId.intValue());

        for (Attachment file : fileOriginPoint.getFiles()) {
            if (file.getFilename().equals(filename)) {
                ByteArrayResource resource = new ByteArrayResource(file.getContent());
                return ResponseEntity.ok(resource);
            }
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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

        return ResponseEntity.ok(cPointRepository
                .findAll()
                .stream()
                .filter(cPoint -> cPoint.getContact().getCompany().getName().equals(companyName))
                .collect(Collectors.toList()));
    }
}
