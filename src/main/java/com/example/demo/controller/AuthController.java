package com.example.demo.controller;

import com.example.demo.security.JwtTokenProvider;
import com.example.demo.service.UtilisateurService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UtilisateurService utilisateurService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(UtilisateurService utilisateurService, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.utilisateurService = utilisateurService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        return utilisateurService.findByEmail(email)
                .filter(utilisateur -> passwordEncoder.matches(password, utilisateur.getPassword()))
                .map(utilisateur -> {
                    // Convertir l'enum Role en String
                    String role = utilisateur.getRole().name(); // Utilisez .name() pour obtenir la valeur String de l'enum
                    String token = jwtTokenProvider.generateToken(email, role); // Générez le token

                    // Incluez le rôle dans la réponse
                    return ResponseEntity.ok(Map.of("token", token, "role", role));
                })
                .orElse(ResponseEntity.status(401).body(Map.of("error", "Invalid email or password")));
    }
}
