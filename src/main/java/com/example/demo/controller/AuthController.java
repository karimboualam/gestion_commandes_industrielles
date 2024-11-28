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
                    // Récupérez le rôle de l'utilisateur
                    String role = utilisateur.getRole();

                    // Générez le token avec email et rôle
                    String token = jwtTokenProvider.generateToken(email, role);

                    return ResponseEntity.ok(Map.of("token", token));
                })
                .orElse(ResponseEntity.status(401).body(Map.of("error", "Invalid email or password")));
    }

}
