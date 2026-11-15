package com.neurosync.kinemo.child.interfaces.rest.resources;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.neurosync.kinemo.child.domain.model.valueobjects.CareNetworkRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

@Schema(
        name = "InviteCaregiverRequest",
        description = "Request payload for inviting a caregiver",
        example = "{\"inviteeEmail\":\"caregiver@example.com\","
                + "\"role\":\"CAREGIVER\",\"permissions\":[\"canReadChildProfile\"]}"
)
public record InviteCaregiverResource(
        @Schema(description = "Email address of the invited caregiver",
                example = "caregiver@example.com", maxLength = 254)
        @NotBlank
        @Email
        @Size(max = 254)
        @JsonAlias("email")
        String inviteeEmail,

        @Schema(description = "Role assigned to the invitee", example = "CAREGIVER")
        CareNetworkRole role,

        @Schema(description = "Permissions granted to the invitee", example = "[\"canReadChildProfile\"]")
        @NotNull
        @Size(max = 20)
        List<@NotBlank @Size(max = 50) String> permissions
) {
    public InviteCaregiverResource {
        if (role == null) role = CareNetworkRole.CAREGIVER;
    }
}
