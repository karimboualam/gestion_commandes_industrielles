package com.example.demo.service;

import com.example.demo.model.Commande;
import com.example.demo.model.Utilisateur;
import com.example.demo.repository.CommandeRepository;
import com.example.demo.repository.UtilisateurRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

// Classe Métier
@Service
public class CommandeService {
    private final CommandeRepository commandeRepository;
    private final UtilisateurRepository utilisateurRepository; // Ajout de UtilisateurRepository


    public CommandeService(CommandeRepository commandeRepository, UtilisateurRepository utilisateurRepository) {
        this.commandeRepository = commandeRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    public List<Commande> getAllCommandes() {
        return commandeRepository.findAll();
    }

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

    public void deleteCommande(Long id) {
        commandeRepository.deleteById(id);
    }
}
