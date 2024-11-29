package com.example.demo.controller;

import com.example.demo.model.Commande;
import com.example.demo.model.Utilisateur;
import com.example.demo.repository.UtilisateurRepository;
import com.example.demo.service.CommandeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/commandes")
public class CommandeController {

    private final CommandeService commandeService;
    private final UtilisateurRepository utilisateurRepository;

    public CommandeController(CommandeService commandeService, UtilisateurRepository utilisateurRepository) {
        this.commandeService = commandeService;
        this.utilisateurRepository = utilisateurRepository;
    }

    /**
     * Récupère les commandes en fonction du rôle de l'utilisateur :
     * - ADMIN : Toutes les commandes
     * - USER : Seulement ses propres commandes
     */
    @GetMapping
    public ResponseEntity<List<Commande>> getCommandes() {
        List<Commande> commandes = commandeService.getCommandesByRole();
        return ResponseEntity.ok(commandes);
    }

    /**
     * Crée une commande associée à l'utilisateur connecté.
     */
    @PostMapping
    public ResponseEntity<Commande> createCommande(@RequestBody Commande commande) {
        Commande savedCommande = commandeService.saveCommande(commande);
        return ResponseEntity.ok(savedCommande);
    }

    /**
     * Supprime une commande :
     * - ADMIN : Peut supprimer n'importe quelle commande
     * - USER : Peut supprimer uniquement ses propres commandes
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommande(@PathVariable Long id) {
        commandeService.deleteCommande(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Crée une commande pour un utilisateur spécifique (ADMIN uniquement).
     */
    @PostMapping("/user/{userId}")
    public ResponseEntity<Commande> createCommandeForUser(@PathVariable Long userId, @RequestBody Commande commande) {
        // Vérifier si l'utilisateur connecté est ADMIN
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur adminUser = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur connecté non trouvé"));

        if (!"ADMIN".equals(adminUser.getRole())) {
            return ResponseEntity.status(403).build(); // Retourner 403 si non autorisé
        }

        // Associer l'utilisateur spécifique à la commande
        Utilisateur utilisateur = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        commande.setUtilisateur(utilisateur);

        // Enregistrer la commande
        Commande savedCommande = commandeService.saveCommande(commande);
        return ResponseEntity.ok(savedCommande);
    }
}
