package com.example.demo.controller;

import com.example.demo.model.Commande;
import com.example.demo.model.Utilisateur;
import com.example.demo.repository.UtilisateurRepository;
import com.example.demo.service.CommandeService;
import org.springframework.http.ResponseEntity;
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

    // Récupérer toutes les commandes
    @GetMapping
    public ResponseEntity<List<Commande>> getAllCommandes() {
        List<Commande> commandes = commandeService.getAllCommandes();
        return ResponseEntity.ok(commandes);
    }

    // Créer une commande pour l'utilisateur connecté
    @PostMapping
    public ResponseEntity<Commande> createCommande(@RequestBody Commande commande) {
        Commande savedCommande = commandeService.saveCommande(commande);
        return ResponseEntity.ok(savedCommande);
    }

    // Supprimer une commande par ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommande(@PathVariable Long id) {
        commandeService.deleteCommande(id);
        return ResponseEntity.noContent().build();
    }

    // Créer une commande pour un utilisateur spécifique (par ID)
    @PostMapping("/user/{userId}")
    public ResponseEntity<Commande> createCommandeForUser(@PathVariable Long userId, @RequestBody Commande commande) {
        Utilisateur utilisateur = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Associer l'utilisateur à la commande
        commande.setUtilisateur(utilisateur);

        // Enregistrer la commande
        Commande savedCommande = commandeService.saveCommande(commande);
        return ResponseEntity.ok(savedCommande);
    }
}
