package lib.quick.authservice.domain.member.entity;

import jakarta.persistence.*;
import lib.quick.authservice.global.util.StringListConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;
import java.util.UUID;

@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    @Id
    @GeneratedValue(generator = "uuid2")
    private UUID id;

    private String email;

    private String password;

    private Role role;
}
