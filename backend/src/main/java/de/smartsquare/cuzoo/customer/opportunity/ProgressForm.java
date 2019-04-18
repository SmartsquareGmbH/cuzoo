package de.smartsquare.cuzoo.customer.opportunity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class ProgressForm {

    @NotNull
    @NotBlank
    private String opportunityState;

    private String progressText;

    public String getOpportunityState() {
        return opportunityState;
    }

    public String getProgressText() {
        return progressText;
    }
}
