package com.example.demo.controller;

import com.example.demo.model.Role;
import com.example.demo.model.Utilisateur;
import com.example.demo.security.JwtTokenProvider;
import com.example.demo.service.UtilisateurService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;
    private final PasswordEncoder passwordEncoder; // Injection de PasswordEncoder
    private final JwtTokenProvider jwtTokenProvider; // Injection de JwtTokenProvider

    // Constructor avec injection des dépendances
    public UtilisateurController(UtilisateurService utilisateurService,
                                 PasswordEncoder passwordEncoder,
                                 JwtTokenProvider jwtTokenProvider) {
        this.utilisateurService = utilisateurService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

   /* // Méthode de connexion
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        return utilisateurService.findByEmail(email)
                .filter(utilisateur -> passwordEncoder.matches(password, utilisateur.getPassword()))
                .map(utilisateur -> {
                    String role = utilisateur.getRole().name(); // Convertir l'enum Role en String
                    String token = jwtTokenProvider.generateToken(email, role); // Générez le token

                    // Incluez le rôle dans la réponse
                    return ResponseEntity.ok(Map.of("token", token, "role", role));
                })
                .orElse(ResponseEntity.status(401).body(Map.of("error", "Invalid email or password")));
    }*/

    // Méthode d'inscription
    @PostMapping("/register")
    public ResponseEntity<?> registerUtilisateur(@RequestBody Utilisateur utilisateur) {
        // Vérifiez si le rôle est valide
        if (utilisateur.getRole() == null ||
                (!utilisateur.getRole().equals(Role.USER) && !utilisateur.getRole().equals(Role.ADMIN))) {
            return ResponseEntity.badRequest().body("Rôle invalide");
        }

        Utilisateur savedUtilisateur = utilisateurService.registerUtilisateur(utilisateur);
        return ResponseEntity.ok(savedUtilisateur);
    }

    // Méthode pour récupérer un utilisateur par email
    @GetMapping("/{email}")
    public ResponseEntity<Utilisateur> getUtilisateurByEmail(@PathVariable String email) {
        return utilisateurService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
