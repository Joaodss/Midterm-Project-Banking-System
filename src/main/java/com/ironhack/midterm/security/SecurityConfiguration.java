package com.ironhack.midterm.security;

import com.ironhack.midterm.security.service.ApiGuardService;
import com.ironhack.midterm.security.service.CustomUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private CustomUserDetailService customUserDetailService;

  @Autowired
  private ApiGuardService apiGuardService;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public ApiGuardService apiGuard() {
    return new ApiGuardService();
  }


  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth
        .userDetailsService(customUserDetailService)
        .passwordEncoder(passwordEncoder);
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.httpBasic();
    http.csrf().disable();
    http.authorizeRequests()

        // GET /users
        .mvcMatchers(HttpMethod.GET, "/api/users/{username}")
        .access("@apiGuard.checkUsernameOrIfAdmin(authentication, #username)")
        .mvcMatchers(HttpMethod.GET, "/api/users/", "/api/users/**").hasRole("ADMIN")
        // GET /accounts
        .mvcMatchers(HttpMethod.GET, "/api/accounts").hasAnyRole("ADMIN", "USER")
        .mvcMatchers(HttpMethod.GET, "/api/accounts/{accountId}", "/api/accounts/{accountId}/**")
        .access("@apiGuard.checkUsernameFromAccountIdOrIfAdmin(authentication, #accountId)")
        .mvcMatchers(HttpMethod.GET, "/api/accounts/**").hasRole("ADMIN")


        // POST /users
        .mvcMatchers(HttpMethod.POST, "/api/users/new").permitAll()
        .mvcMatchers(HttpMethod.POST, "/api/users/new_admin").permitAll() // ********** temporary **********
        .mvcMatchers(HttpMethod.POST, "/api/users/**").hasRole("ADMIN")
        // POST /accounts
        .mvcMatchers(HttpMethod.POST, "/api/accounts/transactions/new_third_party_transaction").permitAll()
        .mvcMatchers(HttpMethod.POST, "/api/accounts/{accountId}", "/api/accounts/{accountId}/**")
        .access("@apiGuard.checkUsernameFromAccountIdOrIfAdmin(authentication, #accountId)")
        .mvcMatchers(HttpMethod.POST, "/api/accounts/**").hasRole("ADMIN")

        // PATCH /users
        .mvcMatchers(HttpMethod.PATCH, "/api/users/{username}/change_password")
        .access("@apiGuard.checkUsernameOrIfAdmin(authentication, #username)")
        .mvcMatchers(HttpMethod.PATCH, "/api/users/**}").hasRole("ADMIN")

        // DELETE /users
        .mvcMatchers(HttpMethod.DELETE, "/api/users/**}").hasRole("ADMIN")




        .anyRequest().authenticated();
  }

}

