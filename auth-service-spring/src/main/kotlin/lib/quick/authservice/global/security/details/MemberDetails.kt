package lib.quick.authservice.global.security.details;

import lib.quick.authservice.domain.user.entity.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails

class MemberDetails(
    private val user: User
): UserDetails {

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf(SimpleGrantedAuthority(user.role.name));
    }

    override fun getPassword(): String? {
        return null;
    }

    override fun getUsername(): String {
        return user.email;
    }
}
