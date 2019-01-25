package de.smartsquare.cuzoo.customer.label;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LabelRepository extends JpaRepository<Label, Long> {

    @Query("SELECT l FROM Label l WHERE l.title = :title AND l.contactPoints IS NOT EMPTY")
    Optional<Label> findForContactPointByTitle(@Param("title") String title);

}
