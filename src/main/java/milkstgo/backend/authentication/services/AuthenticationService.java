package milkstgo.backend.authentication.services;

import milkstgo.backend.authentication.entities.AuthenticationResponse;
import milkstgo.backend.authentication.entities.LoginRequest;
import milkstgo.backend.authentication.entities.User;
import milkstgo.backend.authentication.entities.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    @Autowired
    JwtService jwtService;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserService userService;

    public AuthenticationResponse authenticate(LoginRequest request) {
        User user = userService.loadUserByUsername(request.getCorreo());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getCorreo(),
                        request.getPassword()
                )
        );

        return buildResponse(user);
    }

    public AuthenticationResponse refreshAuth(String refreshToken, String correo) {
        if(jwtService.isTokenExpired(refreshToken)) {
            throw new IllegalArgumentException("El token no es valido");
        }

        if(correo == null) {
            correo = jwtService.getUsername(refreshToken);
        }

        User user = userService.loadUserByUsername(correo);

        return buildResponse(user);
    }

    private AuthenticationResponse buildResponse(User user) {
        String jwtToken = jwtService.generateToken(user);
        String jwtRefreshToken = jwtService.generateRefreshToken(user);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .accessExpiration(jwtService.getExpiration(jwtToken).getTime())
                .refreshToken(jwtRefreshToken)
                .refreshExpiration(jwtService.getExpiration(jwtRefreshToken).getTime())
                .user(new UserResponse(user))
                .build();
    }
}
