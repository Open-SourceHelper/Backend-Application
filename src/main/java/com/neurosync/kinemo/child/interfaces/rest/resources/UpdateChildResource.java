package com.neurosync.kinemo.child.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Schema(
        name = "UpdateChildRequest",
        description = "Request payload for updating a child",
        example = "{\"firstName\":\"Alex\",\"lastName\":\"Morgan\","
                + "\"birthDate\":\"2020-05-15\"}"
)
public record UpdateChildResource(
        @Schema(description = "Child's first name", example = "Alex", minLength = 1, maxLength = 100)
        @NotBlank @Size(max = 100)
        String firstName,

        @Schema(description = "Child's last name", example = "Morgan", minLength = 1, maxLength = 100)
        @NotBlank @Size(max = 100)
        String lastName,

        @Schema(description = "Child's birth date", example = "2020-05-15", format = "date")
        @NotNull
        LocalDate birthDate
) {}
