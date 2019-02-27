package com.isc.astd.config;

import com.isc.astd.security.DomainUserDetailsService;
import com.isc.astd.security.X509PrincipalSnExtractor;
import com.isc.astd.security.X509WebAuthenticationDetails;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.x509.X509AuthenticationFilter;

import javax.servlet.http.HttpServletRequest;

/**
 * @author p.dzeviarylin
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final DomainUserDetailsService userDetailsService;
    private final X509PrincipalSnExtractor x509PrincipalExtractor;

    public SecurityConfiguration(DomainUserDetailsService userDetailsService, X509PrincipalSnExtractor x509PrincipalExtractor) {
        this.userDetailsService = userDetailsService;
        this.x509PrincipalExtractor = x509PrincipalExtractor;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .headers().frameOptions().sameOrigin()
                .and()
                .authorizeRequests()
               /* .antMatchers("/app/**").permitAll()
                .antMatchers("/ext/**").permitAll()
                .antMatchers("/resources/**").permitAll()
                .antMatchers("/packages/**").permitAll()
                .antMatchers("/*.{js,html,json}").permitAll()
                .antMatchers("/build/**").permitAll()   */
                .antMatchers("/api/**").authenticated()
                .and()
                .x509().x509AuthenticationFilter(authenticationFilter());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers(HttpMethod.OPTIONS, "/**")
                .antMatchers("/app/**")
                .antMatchers("/ext/**")
                .antMatchers("/resources/**")
                .antMatchers("/packages/**")
                .antMatchers("/build/**")
                .antMatchers("/*.{js,html,json}");
    }

    /*@Override
    public void configure(WebSecurity web) {
        web.ignoring()
                .antMatchers("/app/**")
                .antMatchers("/astd-dpg/ext/**")
                .antMatchers("/astd-dpg/resources/**")
                .antMatchers("/astd-dpg/packages/**")
                .antMatchers("/astd-dpg/*.{js,html,json}")
                .antMatchers("/astd-dpg/build/**");
    }*/

    public X509AuthenticationFilter authenticationFilter() throws Exception {
        X509AuthenticationFilter filter = new X509AuthenticationFilter();
        filter.setPrincipalExtractor(x509PrincipalExtractor);
        filter.setAuthenticationManager(authenticationManager());
        filter.setAuthenticationDetailsSource((AuthenticationDetailsSource<HttpServletRequest, X509WebAuthenticationDetails>) X509WebAuthenticationDetails::new);
        return filter;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder) {
        authenticationManagerBuilder.authenticationProvider(authenticationProvider());
    }

    private AuthenticationProvider authenticationProvider() {
        PreAuthenticatedAuthenticationProvider authProvider = new PreAuthenticatedAuthenticationProvider();
        authProvider.setPreAuthenticatedUserDetailsService(new UserDetailsByNameServiceWrapper<>(userDetailsService));
        return authProvider;
    }
    
}
