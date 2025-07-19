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
                                .csrf(csrf -> csrf
                                                // Mantener excepciones específicas para webhooks y documentación
                                                .ignoringRequestMatchers("/pago/webhook")
                                                .ignoringRequestMatchers("/v3/api-docs/**")
                                                .ignoringRequestMatchers("/swagger-ui/**")
                                                .ignoringRequestMatchers("/swagger-ui.html")
                                                .disable())
                                .requiresChannel(chanel -> chanel.anyRequest().requiresSecure())
                                .authorizeHttpRequests(request -> request
                                                // =============================================================
                                                // ENDPOINTS PÚBLICOS (sin autenticación)
                                                // =============================================================

                                                // Autenticación y registro
                                                .requestMatchers("/api/usuario/signup",
                                                                "/api/usuario/login",
                                                                "/api/usuario/verificar")
                                                .permitAll()

                                                // Documentación de API
                                                .requestMatchers("/v3/api-docs/**",
                                                                "/swagger-ui/**",
                                                                "/swagger-ui.html")
                                                .permitAll()

                                                // Webhooks externos (Stripe)
                                                .requestMatchers("/pago/webhook").permitAll()

                                                // Endpoints de vista pública (si tienes frontend integrado)
                                                .requestMatchers("/index", "/login", "/productohtml").permitAll()

                                                // APIs de consulta pública (productos y sucursales para catálogo)
                                                .requestMatchers("/producto/all",
                                                                "/producto/{idProducto}",
                                                                "/sucursal/all",
                                                                "/sucursal/{idSucursal}",
                                                                "/sucursal/usercity",
                                                                "/categoria/all",
                                                                "/categoria/{idcategoria}",
                                                                "/productosucursal/sucursal/{idSucursal}",
                                                                "/ubicacion-usuario/**")
                                                .permitAll()

                                                // =============================================================
                                                // ENDPOINTS PARA USUARIOS AUTENTICADOS (USER o ADMIN)
                                                // =============================================================

                                                // Gestión de pedidos (usuarios pueden crear y ver sus pedidos)
                                                .requestMatchers("/pedido/save").hasAnyRole("USER", "ADMIN")
                                                .requestMatchers("/pedido/{email}").hasAnyRole("USER", "ADMIN")
                                                .requestMatchers("/pedido/pedido/{idpedido}")
                                                .hasAnyRole("USER", "ADMIN")

                                                // Proceso de pago (usuarios pueden iniciar pagos)
                                                .requestMatchers("/pago/url/{idpedido}").hasAnyRole("USER", "ADMIN")

                                                // Perfil de usuario
                                                .requestMatchers("/api/usuario/update/{id_usuario}")
                                                .hasAnyRole("USER", "ADMIN")

                                                // =============================================================
                                                // ENDPOINTS EXCLUSIVOS PARA ADMINISTRADORES
                                                // =============================================================

                                                // Gestión completa de productos
                                                .requestMatchers("/producto/save",
                                                                "/producto/update/{idproducto}",
                                                                "/producto/delete/{idProducto}")
                                                .hasRole("ADMIN")

                                                // Gestión de categorías
                                                .requestMatchers("/categoria/save",
                                                                "/categoria/update/{id}",
                                                                "/categoria/delete")
                                                .hasRole("ADMIN")

                                                // Gestión de sucursales
                                                .requestMatchers("/sucursal/save",
                                                                "/sucursal/update/{idSucursal}",
                                                                "/sucursal/delete")
                                                .hasRole("ADMIN")

                                                // Gestión de productos por sucursal (inventario)
                                                .requestMatchers("/productosucursal/agregar",
                                                                "/productosucursal/actualizar",
                                                                "/productosucursal/eliminar/{idProducto}/{idSucursal}",
                                                                "/productosucursal/stock/actualizar",
                                                                "/productosucursal/stock/disminuir",
                                                                "/productosucursal/stock/aumentar")
                                                .hasRole("ADMIN")

                                                // Gestión administrativa de pedidos
                                                .requestMatchers("/pedido/all",
                                                                "/pedido/update/{idpedido}",
                                                                "/pedido/delete/{idpedido}")
                                                .hasRole("ADMIN")

                                                // Gestión de usuarios (solo lectura de lista para admins)
                                                .requestMatchers("/api/usuario/all").hasRole("ADMIN")

                                                // Endpoints de prueba de roles
                                                .requestMatchers("/api/usuario/user").hasRole("USER")
                                                .requestMatchers("/api/usuario/admin").hasRole("ADMIN")

                                                // =============================================================
                                                // ENDPOINTS CON ACCESO LIMITADO POR LÓGICA DE NEGOCIO
                                                // =============================================================

                                                // Consultas de stock (solo lectura para usuarios, control total para
                                                // admins)
                                                .requestMatchers("/productosucursal/all",
                                                                "/productosucursal/stock")
                                                .hasAnyRole("USER", "ADMIN")

                                                // =============================================================
                                                // CUALQUIER OTRO ENDPOINT REQUIERE AUTENTICACIÓN
                                                // =============================================================
                                                .anyRequest().authenticated())

                                .sessionManagement(sesion -> sesion
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

                http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
                return http.build();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.addAllowedOrigin("http://localhost:3600"); // Agregar el puerto de tu app
                configuration.addAllowedOrigin("http://localhost:5173");
                configuration.addAllowedOrigin("http://localhost:8080");
                configuration.addAllowedOrigin("http://127.0.0.1:*"); // Para Stripe CLI
                configuration.addAllowedOrigin("https://api.stripe.com"); // Para Stripe
                configuration.addAllowedOrigin("https://54.210.224.54");
                configuration.addAllowedOrigin("https://54.210.224.54");
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
