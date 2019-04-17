package de.smartsquare.cuzoo.customer.contact;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    Contact findByName(String name);

    Optional<Contact> findMaybeByName(String name);

    Optional<Contact> findMaybeById(Long id);

    boolean existsByName(String contactName);
}
