package com.boarhat.infrastructure.adapter.in.web.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record AccountCreatedResponse(
        @Schema(description = "Identifier of the newly created account",
                example = "b3f1c2d4-5e6f-7a8b-9c0d-1e2f3a4b5c6d")
        UUID accountId
) {
}
