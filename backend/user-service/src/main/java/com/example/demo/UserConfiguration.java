package com.example.demo;

import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class UserConfiguration {
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http, JwtRequestFilter jwtRequestFilter) throws Exception {
	    http
	        .csrf(csrf -> csrf.disable())
	        .authorizeHttpRequests(auth -> auth
	            .requestMatchers("/users/register", "/users/login").permitAll()
	            .requestMatchers("/users/admin").hasRole("ADMIN") // dedicated admin endpoint
	            .anyRequest().authenticated()
	        )
	        .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

	    return http.build();
	}

	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	
}
