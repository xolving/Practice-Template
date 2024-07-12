package lib.quick.authservice.domain.member.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {
    ROLE_MEMBER("ROLE_MEMBER");

    private final String permission;
}
