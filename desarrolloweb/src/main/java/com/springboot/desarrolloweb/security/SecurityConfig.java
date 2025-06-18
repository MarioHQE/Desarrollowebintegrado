package com.springboot.desarrolloweb.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private filter filter;

    @Bean
    public PasswordEncoder PasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.cors(t -> t.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.ignoringRequestMatchers("/pago/webhook")
                        .ignoringRequestMatchers("/pago/webhook")
                        .ignoringRequestMatchers("/v3/api-docs/**")
                        .ignoringRequestMatchers("/swagger-ui/**")
                        .ignoringRequestMatchers("/swagger-ui.html").disable())
                .authorizeHttpRequests(
                        request -> request
                                .requestMatchers("/index", "/login", "/api/usuario/signup", "/api/usuario/login",
                                        "/productosucursal/**", "/producto/**", "/sucursal/**", "/productohtml",
                                        "/categoria/**", "/pedido/**", "/api/usuario/verificar", "/api/usuario/all",
                                        "/pago/**", "/pago/url/**", "/pedido/update/**", "/pago/webhook")
                                .permitAll()
                                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")
                                .permitAll()
                                .requestMatchers("/api/usuario/user").hasAuthority("ROLE_USER")
                                .requestMatchers("/api/usuario/admin").hasAuthority("ROLE_ADMIN")
                                .anyRequest()
                                .authenticated())
                .sessionManagement(sesion -> sesion.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:3600"); // Agregar el puerto de tu app
        configuration.addAllowedOrigin("http://localhost:5173");
        configuration.addAllowedOrigin("http://127.0.0.1:*"); // Para Stripe CLI
        configuration.addAllowedOrigin("https://api.stripe.com"); // Para Stripe
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationmanager(AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();

    }
}
