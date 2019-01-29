package de.smartsquare.cuzoo.customer.contactpoint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactPointRepository extends JpaRepository<ContactPoint, Long> {

}
