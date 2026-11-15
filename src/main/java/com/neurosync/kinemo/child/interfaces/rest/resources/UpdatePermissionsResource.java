package com.neurosync.kinemo.child.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

@Schema(
        name = "UpdatePermissionsRequest",
        description = "Request payload for updating caregiver permissions",
        example = "{\"permissions\":[\"READ_PROFILE\"]}"
)
public record UpdatePermissionsResource(
        @Schema(description = "Permissions assigned to the caregiver", example = "[\"READ_PROFILE\"]")
        @NotNull
        @Size(max = 20)
        List<@NotBlank @Size(max = 50) String> permissions
) {
    public UpdatePermissionsResource {
        if (permissions == null) {
            throw new IllegalArgumentException("Permissions cannot be null");
        }
        if (permissions.stream().anyMatch(permission -> permission == null || permission.isBlank())) {
            throw new IllegalArgumentException("Permissions cannot contain null or blank values");
        }

        permissions = List.copyOf(permissions);
    }
}
