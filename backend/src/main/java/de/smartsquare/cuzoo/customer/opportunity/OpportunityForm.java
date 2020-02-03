package de.smartsquare.cuzoo.customer.opportunity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

public class OpportunityForm {

  private Long id;
  @NotNull
  @NotBlank
  private String title;

  private String state;
  private String description;

  private List<String> labels;

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

  public List<String> getLabels() {
    return labels;
  }
}
