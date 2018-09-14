package de.smartsquare.cuzoo.customer;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @NotNull
    @NotBlank
    private String name;
    @NotNull
    @NotBlank
    private String firstname;

    public Customer() {
    }

    Customer(@NotNull @NotBlank final String name,
             @NotNull @NotBlank final String firstname) {
        this.name = name;
        this.firstname = firstname;
    }

    public final Long getId() {
        return id;
    }

    public final String getName() {
        return name;
    }

    public final String getFirstname() {
        return firstname;
    }
}
