package de.smartsquare.cuzoo.customer.contactpoint.attachment;

import de.smartsquare.cuzoo.customer.contactpoint.ContactPoint;
import de.smartsquare.cuzoo.customer.contactpoint.ContactPointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/file")
public class AttachmentController {
    private final ContactPointRepository contactPointRepository;
    private final AttachmentRepository attachmentRepository;

    @Autowired
    public AttachmentController(final ContactPointRepository contactPointRepository,
                                final AttachmentRepository attachmentRepository) {
        this.contactPointRepository = contactPointRepository;
        this.attachmentRepository = attachmentRepository;
    }

    @PostMapping("/upload/{contactPointId}")
    public final ResponseEntity<?> uploadFile(@PathVariable Long contactPointId,
                                              @RequestParam("file") MultipartFile file) throws IOException {
        Optional<ContactPoint> fileDestinationPoint = contactPointRepository.findById(contactPointId);

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Die hoch zu ladende Datei ist leer!");
        }
        if (!fileDestinationPoint.isPresent()) {
            return ResponseEntity.badRequest().body("Der Kontaktpunkt existiert nicht!");
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
    public final ResponseEntity<?> downloadFile(@PathVariable Long contactPointId,
                                                     @PathVariable String fileName) {
        Optional<ContactPoint> fileOriginPoint = contactPointRepository.findById(contactPointId);

        if (!fileOriginPoint.isPresent()) {
            return ResponseEntity.badRequest().body("Der Kontaktpunkt existiert nicht!");
        }

        if (fileOriginPoint
                .get().getFiles()
                .stream()
                .noneMatch(file -> file.getFilename().equals(fileName))) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Die Datei wurde nicht gefunden!");
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

    @DeleteMapping("/delete/{contactPointId}/{fileName}")
    public final ResponseEntity<?> deleteFileOfContactPoint(@PathVariable Long contactPointId,
                                                            @PathVariable String fileName) {
        Optional<ContactPoint> fileOriginPoint = contactPointRepository.findById(contactPointId);

        if (!fileOriginPoint.isPresent()) {
            return ResponseEntity.badRequest().body("Der Kontaktpunkt existiert nicht!");
        }

        if (fileOriginPoint
                .get().getFiles()
                .stream()
                .noneMatch(file -> file.getFilename().equals(fileName))) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Die Datei wurde nicht gefunden!");
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

    @GetMapping("/get/names/{contactPointId}")
    public final ResponseEntity<?> getAllFileNamesOfContactPoint(@PathVariable Long contactPointId) {
        Optional<ContactPoint> fileOriginPoint = contactPointRepository.findById(contactPointId);

        if (!fileOriginPoint.isPresent()) {
            return ResponseEntity.badRequest().body("Der Kontaktpunkt existiert nicht!");
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
}
