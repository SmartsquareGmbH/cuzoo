package de.smartsquare.cuzoo.customer.points;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
interface CPointRepository extends JpaRepository<CPoint, Long> {

    @Query("SELECT c FROM CPoint c WHERE c.contact.company.name = :companyName ORDER BY c.date")
    Optional<List<CPoint>> findCPointsByCompanyName(@Param("companyName") String companyName);

}
