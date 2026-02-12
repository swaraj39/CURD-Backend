package com.example.demo.Config;

// Spring annotations for defining configuration classes and security setup
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

// Custom service used to load user details from the database
import com.example.demo.Service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration // Marks this class as a Spring configuration class
@EnableWebSecurity // Enables Spring Security for the application
public class SecurityConfig {

    // Service that implements UserDetailsService for authentication
    private final CustomerService customerService;

    // Constructor-based dependency injection for CustomerService
    public SecurityConfig(CustomerService customerService) {
        this.customerService = customerService;
    }

    // Bean definition for password encoding using BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Bean definition for authentication provider using DAO pattern
    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        // Sets the custom user details service
        provider.setUserDetailsService(customerService);
        // Sets the password encoder for password comparison
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
    

    // Main security filter chain configuration
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // Disables CSRF protection (commonly done for stateless APIs or during development)
                .csrf(csrf -> csrf.disable())

                // Configures CORS settings
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    // Allows requests from the specified frontend origin
                    config.addAllowedOrigin("https://crud-frontend-yb8t.vercel.app/");
                    // Allows all HTTP methods (GET, POST, PUT, DELETE, etc.)
                    config.addAllowedMethod("*");
                    // Allows all headers in requests
                    config.addAllowedHeader("*");
                    // Allows cookies and authentication credentials
                    config.setAllowCredentials(true);
                    return config;
                }))

                // Configures authorization rules for HTTP requests
                .authorizeHttpRequests(auth -> auth
                        // Allows unauthenticated access to login endpoints
                        .requestMatchers("/login", "/signin").permitAll()
                        // Requires authentication for all other endpoints
                        .anyRequest().authenticated()
                )

                // Configures form-based login
                .formLogin(form -> form
                        // URL where login requests are processed
                        .loginProcessingUrl("/login")
                        // Request parameter name for username
                        .usernameParameter("username")
                        // Request parameter name for password
                        .passwordParameter("password")
                        // Handler executed when login is successful
                        .successHandler((req, res, auth) -> {
                            System.out.println("=== LOGIN SUCCESS ===");
                            res.setStatus(200);
                        })
                        // Handler executed when login fails
                        .failureHandler((req, res, ex) -> {
                            System.out.println("=== LOGIN FAILED === " + ex.getMessage());
                            res.setStatus(401);
                        })
                )

                // Configures logout behavior
                .logout(logout -> logout
                        // URL used to trigger logout
                        .logoutUrl("/logout")
                        // Handler executed after successful logout
                        .logoutSuccessHandler((req, res, auth) -> {
                            System.out.println("=== LOGOUT SUCCESS ===");
                            res.setStatus(200);
                        })
                        // Prevents invalidation of the HTTP session on logout
                        .invalidateHttpSession(false)
                );

        // Builds and returns the configured security filter chain
        return http.build();
    }
}
