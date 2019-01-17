package de.smartsquare.cuzoo.customer.points.attachment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface AttachmentRepository extends JpaRepository<Attachment, Long> {
}
