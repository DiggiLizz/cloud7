package cl.duoc.ejemplo.microservicio.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

                http
                        // Desactivar CSRF para API REST
                        .csrf(csrf -> csrf.disable())

                        // Permitir H2 Console (usa frames)
                        .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))

                        .authorizeHttpRequests(auth -> auth

                                // Swagger / OpenAPI
                                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                                // H2 Console (solo para evidencia local)
                                .requestMatchers("/h2-console/**").permitAll()

                                // Eventos públicos (lectura)
                                .requestMatchers("/eventos", "/eventos/*").permitAll()

                                // Todo lo demás requiere JWT
                                .anyRequest().authenticated()
                        )

                        // Resource Server (JWT desde IDaaS)
                        .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

                return http.build();
        }
}