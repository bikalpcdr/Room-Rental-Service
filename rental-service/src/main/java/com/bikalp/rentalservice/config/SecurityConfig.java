package com.bikalp.rentalservice.config;

import com.bikalp.rentalservice.security.JwtAuthenticationEntryPoint;
import com.bikalp.rentalservice.security.JwtAuthenticationFilter;
import jakarta.servlet.ServletException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint unauthorizedHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("Configuring security filter chain...");
        
        http
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .ignoringRequestMatchers("/auth/logout")
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                .maximumSessions(1)
                .expiredUrl("/auth/login?expired=true")
            )
            .authorizeHttpRequests(auth -> {
                log.info("Configuring authorization rules...");
                auth
                    .requestMatchers("/", "/auth/**", "/css/**", "/js/**", "/images/**", 
                                   "/search", "/room/**", "/api/rooms/**", "/api/search/**",
                                   "/home", "/home/**", "/static/**", "/webjars/**",
                                   "/favicon.ico", "/error","about","contact","browse-rooms").permitAll()
                    .requestMatchers("/superadmin/**").hasAuthority("ROLE_SUPER_ADMIN")
                    .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                    .requestMatchers("/landlord/**").hasAuthority("ROLE_LANDLORD")
                    .requestMatchers("/customer/**").hasAuthority("ROLE_CUSTOMER")
                    .anyRequest().authenticated();
            })
            .formLogin(form -> {
                log.info("Configuring form login...");
                form
                    .loginPage("/auth/login")
                    .loginProcessingUrl("/auth/login")
                    .usernameParameter("emailOrUsername")
                    .passwordParameter("password")
                    .successHandler(authenticationSuccessHandler())
                    .failureHandler((request, response, exception) -> {
                        log.warn("Authentication failed: {}", exception.getMessage());
                        request.getSession().setAttribute("error", "Invalid email/username or password");
                        response.sendRedirect("/auth/login");
                    })
                    .permitAll();
            })
            .logout(logout -> {
                log.info("Configuring logout...");
                logout
                    .logoutUrl("/auth/logout")
                    .logoutSuccessHandler(logoutSuccessHandler())
                    .invalidateHttpSession(true)
                    .clearAuthentication(true)
                    .deleteCookies("JSESSIONID", "XSRF-TOKEN")
                    .permitAll();
            });
            
        log.info("Security filter chain configuration completed");
        return http.build();
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        SimpleUrlLogoutSuccessHandler handler = new SimpleUrlLogoutSuccessHandler() {
            @Override
            public void onLogoutSuccess(jakarta.servlet.http.HttpServletRequest request,
                                      jakarta.servlet.http.HttpServletResponse response,
                                      org.springframework.security.core.Authentication authentication) throws java.io.IOException {
                log.info("User logged out successfully");
                try {
                    super.onLogoutSuccess(request, response, authentication);
                } catch (ServletException e) {
                    log.error("Error during logout: {}", e.getMessage());
                    response.sendRedirect("/");
                }
            }
        };
        handler.setDefaultTargetUrl("/");
        return handler;
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        SimpleUrlAuthenticationSuccessHandler handler = new SimpleUrlAuthenticationSuccessHandler() {
            @Override
            protected String determineTargetUrl(jakarta.servlet.http.HttpServletRequest request, 
                                              jakarta.servlet.http.HttpServletResponse response, 
                                              org.springframework.security.core.Authentication authentication) {
                log.info("Determining target URL for user: {}", authentication.getName());
                log.info("User authorities: {}", authentication.getAuthorities());
                
                if (authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"))) {
                    log.info("Redirecting super admin to dashboard");
                    return "/superadmin/dashboard";
                }
                if (authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                    log.info("Redirecting admin to dashboard");
                    return "/admin/dashboard";
                }
                if (authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_LANDLORD"))) {
                    log.info("Redirecting landlord to dashboard");
                    return "/landlord/dashboard";
                }
                if (authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER"))) {
                    log.info("Redirecting customer to dashboard");
                    return "/customer/dashboard";
                }
                log.warn("No matching role found for user: {}", authentication.getName());
                return "/";
            }
        };
        handler.setUseReferer(false);
        handler.setAlwaysUseDefaultTargetUrl(false);
        return handler;
    }

    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
} 