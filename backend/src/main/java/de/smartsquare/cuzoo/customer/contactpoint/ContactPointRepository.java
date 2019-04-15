package de.smartsquare.cuzoo.customer.contactpoint;

import de.smartsquare.cuzoo.customer.opportunity.Opportunity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactPointRepository extends JpaRepository<ContactPoint, Long> {

    @Query("SELECT DISTINCT c.opportunity FROM ContactPoint c " +
            "WHERE c.contact.company.name LIKE :companyName")
    List<Opportunity> findAllOpportunitiesOfCompany(@Param("companyName") String companyName);


    @Query("SELECT c FROM ContactPoint c " +
            "WHERE c.opportunity.id = :opportunityId")
    List<ContactPoint> findAllContactPointsOfOpportunity(@Param("opportunityId") Long opportunityId);

    ContactPoint findFirstByOpportunityIdOrderByDateDesc(Long opportunityId);

}
