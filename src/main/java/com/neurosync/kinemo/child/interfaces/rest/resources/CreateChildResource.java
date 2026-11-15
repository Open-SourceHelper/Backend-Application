package com.neurosync.kinemo.child.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

@Schema(
        name = "CreateChildRequest",
        description = "Request payload for creating a child",
        example = "{\"parentId\":42,\"firstName\":\"Alex\","
                + "\"lastName\":\"Morgan\",\"birthDate\":\"2020-05-15\"}"
)
public record CreateChildResource(
        @Schema(description = "Parent identifier", example = "42")
        Long parentId,

        @Schema(description = "Child's first name", example = "Alex", minLength = 1, maxLength = 100)
        String firstName,

        @Schema(description = "Child's last name", example = "Morgan", minLength = 1, maxLength = 100)
        String lastName,

        @Schema(description = "Child's birth date", example = "2020-05-15", format = "date")
        LocalDate birthDate
) {
    public CreateChildResource {
        if (parentId == null) {
            throw new IllegalArgumentException("Parent ID is required");
        }
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Last name is required");
        }
        if (birthDate == null) {
            throw new IllegalArgumentException("Birth date is required");
        }
    }
}
