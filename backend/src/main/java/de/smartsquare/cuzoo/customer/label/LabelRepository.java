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
            "OR l.contactPointsWithTypes IS NOT EMPTY " +
            "OR l.companies IS NOT EMPTY)")
    Optional<Label> findByTitle(@Param("title") String title);

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

    @Query("SELECT l FROM Label l " +
            "WHERE FUNCTION('REPLACE', FUNCTION('REPLACE', LOWER(l.title), '-', ''), ' ', '') " +
            "LIKE CONCAT('%', LOWER(:input), '%') " +
            "AND l.companies IS NOT EMPTY")
    List<Label> findAllOfCompanyByPartOfTitle(@Param("input") String input);

    @Query("SELECT l FROM Label l " +
            "WHERE FUNCTION('REPLACE', FUNCTION('REPLACE', LOWER(l.title), '-', ''), ' ', '') " +
            "LIKE CONCAT('%', LOWER(:input), '%') " +
            "AND l.contacts IS NOT EMPTY")
    List<Label> findAllOfContactByPartOfTitle(@Param("input") String input);

    @Query("SELECT l FROM Label l WHERE l.contactPointsWithLabels IS NOT EMPTY")
    List<Label> findAllOfContactPointLabels();

    @Query("SELECT l FROM Label l WHERE l.contactPointsWithTypes IS NOT EMPTY")
    List<Label> findAllOfContactPointTypes();

    @Query("SELECT l FROM Label l WHERE l.companies IS NOT EMPTY")
    List<Label> findAllOfCompany();

    @Query("SELECT l FROM Label l WHERE l.contacts IS NOT EMPTY")
    List<Label> findAllOfContact();

    @Transactional
    @Modifying
    @Query("DELETE FROM Label l " +
            "WHERE l.contactPointsWithLabels IS EMPTY " +
            "AND l.contactPointsWithTypes IS EMPTY " +
            "AND l.companies IS EMPTY " +
            "AND l.contacts IS EMPTY")
    void deleteAllReferenceless();

}
