package de.smartsquare.cuzoo.customer.opportunity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class ProgressForm {

    @NotNull
    @NotBlank
    private String opportunityState;

    private String progressText;

    public ProgressForm(@NotNull @NotBlank String opportunityState, String progressText) {
        this.opportunityState = opportunityState;
        this.progressText = progressText;
    }

    public String getOpportunityState() {
        return opportunityState;
    }

    public String getProgressText() {
        return progressText;
    }
}
