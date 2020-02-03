package de.smartsquare.cuzoo.customer.opportunity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface OpportunityRepository extends JpaRepository<Opportunity, Long> {

    @Query("SELECT o FROM Opportunity o WHERE o.title = :opportunityTitle ")
    Opportunity findByName(@Param("opportunityTitle") String opportunityTitle);

    Optional<Opportunity> findMaybeByTitle(String opportunityTitle);
    Optional<Opportunity> findMaybeById(Long opportunityId);

    @Query("SELECT p.date FROM Opportunity o JOIN o.progress p " +
            "WHERE o.id = :opportunityId ORDER BY p.date DESC")
    List<Date> findAllProgressDatesByOpportunityIdOrderByDateDesc(@Param("opportunityId") Long opportunityId);

}
