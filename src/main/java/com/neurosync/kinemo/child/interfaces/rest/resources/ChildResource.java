package com.neurosync.kinemo.child.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(name = "ChildResponse", description = "Child resource")
public record ChildResource(
        @Schema(example = "123")
        Long id,

        @Schema(example = "42")
        Long parentId,

        @Schema(example = "Alex")
        String firstName,

        @Schema(example = "Morgan")
        String lastName,

        @Schema(example = "2020-05-15", format = "date")
        LocalDate birthDate
) {
}
