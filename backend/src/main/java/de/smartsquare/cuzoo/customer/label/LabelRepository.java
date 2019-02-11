package de.smartsquare.cuzoo.customer.label;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface LabelRepository extends JpaRepository<Label, Long> {

    @Query("SELECT l FROM Label l " +
            "WHERE l.title = :title " +
            "AND (l.contactPointsWithLabels IS NOT EMPTY " +
            "OR l.contactPointsWithTypes IS NOT EMPTY)")
    Optional<Label> findForContactPointByTitle(@Param("title") String title);

    @Query("SELECT l FROM Label l " +
            "WHERE FUNCTION('REPLACE', FUNCTION('REPLACE', LOWER(l.title), '-', ''), ' ', '') " +
            "LIKE CONCAT('%', LOWER(:input), '%') " +
            "AND l.contactPointsWithLabels IS NOT EMPTY")
    List<Label> findAllOfContactPointLabelsByPartOfTitle(@Param("input") String input);

    @Query("SELECT l FROM Label l " +
            "WHERE FUNCTION('REPLACE', FUNCTION('REPLACE', LOWER(l.title), '-', ''), ' ', '') " +
            "LIKE CONCAT('%', LOWER(:input), '%') " +
            "AND l.contactPointsWithTypes IS NOT EMPTY")
    List<Label> findAllOfContactPointTypesByPartOfTitle(@Param("input") String input);

    @Transactional
    @Modifying
    @Query("DELETE FROM Label l " +
            "WHERE l.contactPointsWithLabels IS EMPTY " +
            "AND l.contactPointsWithTypes IS EMPTY")
    void deleteAllReferenceless();

}
