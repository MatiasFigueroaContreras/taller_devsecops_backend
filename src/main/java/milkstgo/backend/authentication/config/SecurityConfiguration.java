package milkstgo.backend.authentication.config;

import milkstgo.backend.authentication.entities.Roles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;
    @Autowired
    private AuthenticationProvider authenticationProvider;
    @Autowired
    private ReferrerPolicyFilter referrerPolicyFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorizeHttpRequests) ->
                        authorizeHttpRequests
                                .requestMatchers("/auth/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/acopios-leche/**", "/laboratorio-leche/**", "/pagos/**", "/proveedores/**").hasAnyAuthority(Roles.USER.getRol(), Roles.ADMIN.getRol())
                                .requestMatchers(HttpMethod.POST, "/acopios-leche/**", "/laboratorio-leche/**", "/pagos/**", "/proveedores/**").hasAuthority(Roles.ADMIN.getRol())
                                .requestMatchers(HttpMethod.PUT, "/acopios-leche/**", "/laboratorio-leche/**", "/pagos/**", "/proveedores/**").hasAuthority(Roles.ADMIN.getRol())
                                .requestMatchers(HttpMethod.DELETE, "/acopios-leche/**", "/laboratorio-leche/**", "/pagos/**", "/proveedores/**").hasAuthority(Roles.ADMIN.getRol())
                                .anyRequest()
                                .authenticated()
                )
                .sessionManagement((sessionManagement) -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(referrerPolicyFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }
}
