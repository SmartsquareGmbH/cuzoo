package de.smartsquare.cuzoo.customer.opportunity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class OpportunityForm {

  private Long id;
  @NotNull
  @NotBlank
  private String title;

  @NotNull
  @NotBlank
  private String state;
  private String description;

  public Long getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getState() {
    return state;
  }

  public String getDescription() {
    return description;
  }
}
