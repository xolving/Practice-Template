package lib.quick.authservice.global.security.details;

import lib.quick.authservice.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;

@AllArgsConstructor
public class MemberDetails implements UserDetails {
    private Member member;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return member.getRoles().stream().map(role ->
            new SimpleGrantedAuthority(role.getPermission())
        ).toList();
    }

    @Override
    @Deprecated
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return member.getEmail();
    }
}
