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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/point")
public class ContactPointController {
    private final ContactPointRepository contactPointRepository;
    private final AttachmentRepository attachmentRepository;
    private final ContactRepository contactRepository;
    private final CompanyRepository companyRepository;

    @Autowired
    public ContactPointController(final ContactPointRepository contactPointRepository, final AttachmentRepository attachmentRepository,
                                  final ContactRepository contactRepository, final CompanyRepository companyRepository) {
        this.contactPointRepository = contactPointRepository;
        this.attachmentRepository = attachmentRepository;
        this.contactRepository = contactRepository;
        this.companyRepository = companyRepository;
    }

    @PutMapping("/submit")
    public final ResponseEntity<?> submitContactPoint(@RequestBody @Valid ContactPoint contactPoint,
                                                      @RequestParam("contactName") String contactName,
                                                      BindingResult bindingResult) {
        if (bindingResult.hasErrors() || !contactRepository.existsByName(contactName)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Contact contactPointsContact = contactRepository.findByName(contactName);
        contactPoint.setContact(contactPointsContact);

        Long contactPointIdBeforeSaving = contactPoint.getId();

        try {
            contactPointRepository.save(contactPoint);
        } catch (DataAccessException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (contactPointIdBeforeSaving == null) {
            return new ResponseEntity<>(HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @DeleteMapping("/delete/{contactPointId}")
    public final ResponseEntity<?> deleteContactPoint(@PathVariable Long contactPointId) {
        if (!contactPointRepository.existsById(contactPointId)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        try {
            contactPointRepository.deleteById(contactPointId);
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

        Optional<ContactPoint> fileDestinationPoint = contactPointRepository.findById(contactPointId);

        if (!fileDestinationPoint.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Attachment attachment = new Attachment(file.getOriginalFilename(), file.getBytes());

        fileDestinationPoint.ifPresent(contactPoint -> {
            checkFiles(contactPoint);
            attachment.setContactPoint(contactPoint);
        });

        try {
            attachmentRepository.save(attachment);

            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (DataAccessException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void checkFiles(ContactPoint fileDestinationPoint) {
        if (fileDestinationPoint.getFiles() == null) {
            fileDestinationPoint.setFiles(new ArrayList<>());
        }
    }

    @GetMapping("/download/{contactPointId}/{fileName}")
    public final ResponseEntity<byte[]> downloadFile(@PathVariable Long contactPointId,
                                                     @PathVariable String fileName) {
        Optional<ContactPoint> fileOriginPoint = contactPointRepository.findById(contactPointId);

        if (!fileOriginPoint.isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (fileOriginPoint
                .get().getFiles()
                .stream()
                .noneMatch(file -> file.getFilename().equals(fileName))) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(getFile(fileOriginPoint.get().getFiles(), fileName).getContent());
    }

    private Attachment getFile(List<Attachment> files, String filename) {
        return files
                .stream()
                .filter(file -> file.getFilename().equals(filename))
                .findFirst()
                .orElse(null);
    }

    @GetMapping("/get/fileNames/{contactPointId}")
    public final ResponseEntity<List<String>> getAllFileNamesOfContactPoint(@PathVariable Long contactPointId) {
        Optional<ContactPoint> fileOriginPoint = contactPointRepository.findById(contactPointId);

        if (!fileOriginPoint.isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (fileOriginPoint.get().getFiles().size() < 1) {
            return ResponseEntity.ok(new ArrayList<>());
        } else {
            return ResponseEntity.ok(getFileNames(fileOriginPoint.get().getFiles()));
        }
    }

    private List<String> getFileNames(List<Attachment> files) {
        if (files.size() > 0) {
            List<String> fileNames = new ArrayList<>();

            files.forEach(file -> fileNames.add(file.getFilename()));

            return fileNames;
        } else {
            return null;
        }
    }

    @DeleteMapping("/file/delete/{contactPointId}/{fileName}")
    public final ResponseEntity<?> deleteFileOfContactPoint(@PathVariable Long contactPointId,
                                                            @PathVariable String fileName) {
        Optional<ContactPoint> fileOriginPoint = contactPointRepository.findById(contactPointId);

        if (!fileOriginPoint.isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (fileOriginPoint
                .get().getFiles()
                .stream()
                .noneMatch(file -> file.getFilename().equals(fileName))) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        fileOriginPoint
                .get().getFiles()
                .stream()
                .filter(file -> file.getFilename()
                        .equals(fileName))
                .findFirst()
                .ifPresent(attachmentRepository::delete);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/get")
    public final ResponseEntity<List<ContactPoint>> getAllContactPoints() {
        return ResponseEntity.ok(contactPointRepository.findAll());
    }

    @GetMapping("/get/{companyName}")
    public final ResponseEntity<List<ContactPoint>> getContactPointsOfCompany(@PathVariable String companyName) {
        if (!companyRepository.existsByName(companyName)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(contactPointRepository.findAll()
                .stream()
                .filter(contactPoint -> contactPoint.getContact().getCompany().getName().equals(companyName))
                .collect(Collectors.toList()));
    }
}
