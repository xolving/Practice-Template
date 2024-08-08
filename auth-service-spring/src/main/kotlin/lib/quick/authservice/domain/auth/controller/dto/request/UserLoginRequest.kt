package lib.quick.authservice.domain.auth.controller.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

class UserLoginRequest (
    @field:Email
    val email: String,

    @field:NotNull
    val password: String
)