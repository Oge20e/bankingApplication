package com.example.BankingApplication.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig  {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf((csrf) -> csrf.ignoringRequestMatchers("/no-csrf"))
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()  // Allow static resources
                        .requestMatchers("/login").permitAll()  // Custom login page
                        .requestMatchers("/", "/home").permitAll()  // Allow access to home page without authentication
                        .requestMatchers("/viewAllAccounts").hasAnyRole("USER", "ADMIN")  // Require USER or ADMIN role to access /viewAllAccounts
                        .requestMatchers("/**").hasRole("ADMIN")  // Only ADMIN can access all other endpoints
                        .anyRequest().authenticated()  // All other requests need to be authenticated
                )
                .formLogin((form) -> form
                        .loginPage("/login")  // Custom login page
                        .permitAll()
                )
                .logout(LogoutConfigurer::permitAll
                );
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.inMemoryAuthentication()
                .passwordEncoder(passwordEncoder())
                .withUser("admin").password(passwordEncoder().encode("adminpass")).roles("ADMIN")
                .and()
                .withUser("user").password(passwordEncoder().encode("userpass")).roles("USER");
        return authenticationManagerBuilder.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username)
                .map(user -> new org.springframework.security.core.userdetails.User(
                        user.getUsername(),
                        user.getPassword(),
                        user.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}


