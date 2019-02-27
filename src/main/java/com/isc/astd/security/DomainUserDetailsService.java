package com.isc.astd.security;

import com.isc.astd.domain.User;
import com.isc.astd.service.UserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * @author p.dzeviarylin
 */
@Component
public class DomainUserDetailsService implements UserDetailsService {

    private final UserService userService;

    public DomainUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        try {
            User user = userService.getUser(id);
            if(user.getExpiredDate() != null) {
                throw new Exception("Доступ закрыт");
            }
            return new org.springframework.security.core.userdetails.User(user.getId(), "", Collections.singletonList(new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN)));
        } catch (Exception e){
            throw new UsernameNotFoundException(e.getMessage());
        }
    }
}
