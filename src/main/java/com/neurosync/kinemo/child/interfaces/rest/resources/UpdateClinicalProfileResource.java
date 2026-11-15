package com.neurosync.kinemo.child.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(
        name = "UpdateClinicalProfileRequest",
        description = "Request payload for updating a child's clinical profile",
        example = "{\"specialNeeds\":\"Sensory support\","
                + "\"triggers\":\"Loud noises\",\"regulators\":\"Quiet space\"}"
)
public record UpdateClinicalProfileResource(
        @Schema(description = "Child's special needs", example = "Sensory support", maxLength = 255)
        @Size(max = 255)
        String specialNeeds,

        @Schema(description = "Known triggers", example = "Loud noises", maxLength = 255)
        @Size(max = 255)
        String triggers,

        @Schema(description = "Helpful regulators", example = "Quiet space", maxLength = 255)
        @Size(max = 255)
        String regulators
) {
}
