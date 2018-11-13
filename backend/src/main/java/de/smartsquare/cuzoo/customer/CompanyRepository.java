package de.smartsquare.cuzoo.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
interface CompanyRepository extends JpaRepository<Company, Long> {

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Company c WHERE c.name = :companyName")
    boolean existsByName(@Param("companyName") String companyName);

    @Query("SELECT c FROM Company c WHERE c.name = :companyName ")
    Company findByName(@Param("companyName") String companyName);
}
