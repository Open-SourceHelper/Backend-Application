package com.neurosync.kinemo.child.interfaces.rest.resources;

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
                + "\"role\":\"CAREGIVER\",\"permissions\":[\"READ_PROFILE\"]}"
)
public record InviteCaregiverResource(
        @Schema(description = "Email address of the invited caregiver",
                example = "caregiver@example.com", maxLength = 254)
        @NotBlank
        @Email
        @Size(max = 254)
        String inviteeEmail,

        @Schema(description = "Role assigned to the invitee", example = "CAREGIVER")
        @NotNull
        CareNetworkRole role,

        @Schema(description = "Permissions granted to the invitee", example = "[\"READ_PROFILE\"]")
        @NotNull
        @Size(max = 20)
        List<@NotBlank @Size(max = 50) String> permissions
) {
    public InviteCaregiverResource {
        if (inviteeEmail == null || inviteeEmail.isBlank()) {
            throw new IllegalArgumentException("Invitee email cannot be null or blank");
        }
        inviteeEmail = inviteeEmail.trim();

        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        if (permissions == null) {
            throw new IllegalArgumentException("Permissions cannot be null");
        }
        if (permissions.stream().anyMatch(permission -> permission == null || permission.isBlank())) {
            throw new IllegalArgumentException("Permissions cannot contain null or blank values");
        }

        permissions = List.copyOf(permissions);
    }
}
