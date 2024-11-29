package com.example.demo.service;

import com.example.demo.model.Commande;
import com.example.demo.model.Utilisateur;
import com.example.demo.repository.CommandeRepository;
import com.example.demo.repository.UtilisateurRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommandeService {
    private final CommandeRepository commandeRepository;
    private final UtilisateurRepository utilisateurRepository;

    public CommandeService(CommandeRepository commandeRepository, UtilisateurRepository utilisateurRepository) {
        this.commandeRepository = commandeRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    /**
     * Retourne toutes les commandes si l'utilisateur est ADMIN.
     * Sinon, retourne uniquement les commandes de l'utilisateur connecté.
     */
    public List<Commande> getCommandesByRole() {
        // Récupérer l'email de l'utilisateur connecté
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        // Trouver l'utilisateur correspondant
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Si l'utilisateur est ADMIN, retourner toutes les commandes
        if ("ADMIN".equals(utilisateur.getRole())) {
            return commandeRepository.findAll();
        }

        // Sinon, retourner uniquement les commandes de l'utilisateur
        return commandeRepository.findByUtilisateur(utilisateur);
    }

    /**
     * Enregistre une commande en l'associant à l'utilisateur connecté.
     */
    public Commande saveCommande(Commande commande) {
        // Récupérer l'email de l'utilisateur connecté
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        // Trouver l'utilisateur correspondant
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Associer l'utilisateur à la commande
        commande.setUtilisateur(utilisateur);

        return commandeRepository.save(commande);
    }

    /**
     * Supprime une commande. Seul l'ADMIN ou le propriétaire de la commande peut la supprimer.
     */
    public void deleteCommande(Long id) {
        // Récupérer l'email de l'utilisateur connecté
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        // Trouver l'utilisateur correspondant
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Trouver la commande à supprimer
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée"));

        // Vérifier les permissions : ADMIN ou propriétaire de la commande
        if (!"ADMIN".equals(utilisateur.getRole()) && !commande.getUtilisateur().equals(utilisateur)) {
            throw new RuntimeException("Vous n'avez pas l'autorisation de supprimer cette commande");
        }

        commandeRepository.deleteById(id);
    }
}
