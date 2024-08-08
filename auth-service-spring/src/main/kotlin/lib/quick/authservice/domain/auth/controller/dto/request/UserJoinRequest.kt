package lib.quick.authservice.domain.auth.controller.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

class UserJoinRequest (
    @field:Email
    val email: String,

    @field:Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[!#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,64}$",
        message = "비밀번호는 8~64자의 영문 대/소문자, 숫자, 특수문자를 포함해야 합니다."
    )
    val password: String
)
