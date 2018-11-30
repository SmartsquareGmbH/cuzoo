package de.smartsquare.cuzoo.customer.company;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    boolean existsByName(String name);

    @Query("SELECT c FROM Company c WHERE c.name = :companyName ")
    Company findByName(@Param("companyName") String companyName);
}
