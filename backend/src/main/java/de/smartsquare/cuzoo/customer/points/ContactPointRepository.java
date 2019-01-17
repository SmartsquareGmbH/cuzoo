package de.smartsquare.cuzoo.customer.points;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactPointRepository extends JpaRepository<ContactPoint, Long> {

    @Query("SELECT c FROM ContactPoint c WHERE c.contact.company.name = :companyName ORDER BY c.date")
    Optional<List<ContactPoint>> findContactPointsByCompanyName(@Param("companyName") String companyName);

}
