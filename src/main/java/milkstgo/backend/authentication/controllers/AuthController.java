package milkstgo.backend.authentication.controllers;

import milkstgo.backend.authentication.entities.AuthenticationResponse;
import milkstgo.backend.authentication.entities.LoginRequest;
import milkstgo.backend.authentication.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AuthController {
    @Autowired
    AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequest request) {
        AuthenticationResponse authenticationResponse = authenticationService.authenticate(request);
        return ResponseEntity.ok(authenticationResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refresh(@RequestParam("token") String refreshToken, @RequestParam("correo") String correo) {
        AuthenticationResponse authenticationResponse = authenticationService.refreshAuth(refreshToken, correo);
        return ResponseEntity.ok(authenticationResponse);
    }
}
