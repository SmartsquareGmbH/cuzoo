package de.smartsquare.cuzoo.customer.contactpoint;

import de.smartsquare.cuzoo.customer.label.Label;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactPointRepository extends JpaRepository<ContactPoint, Long> {

    @Query("SELECT c.labels FROM ContactPoint c")
    List<Label> findAllLabels();

}
