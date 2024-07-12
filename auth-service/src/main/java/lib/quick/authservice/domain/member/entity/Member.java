package lib.quick.authservice.domain.member.entity;

import jakarta.persistence.*;
import lib.quick.authservice.global.util.StringListConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String password;

    @Convert(converter = StringListConverter.class)
    private List<Role> roles;
}
