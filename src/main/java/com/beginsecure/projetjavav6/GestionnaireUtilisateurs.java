package com.beginsecure.projetjavav6;

import java.util.ArrayList;
import java.util.List;

public class GestionnaireUtilisateurs {
    private List<Utilisateur> utilisateurs;

    // Constructeur
    public GestionnaireUtilisateurs() {
        this.utilisateurs = new ArrayList<>();
    }

    // Ajouter un utilisateur (vérifie qu’il n’existe pas déjà avec le même username)
    public boolean ajouterUtilisateur(Utilisateur utilisateur) {
        if (utilisateur == null || utilisateur.getNom() == null) {
            return false;
        }
        if (trouverUtilisateurParUsername(utilisateur.getNom()) != null) {
            return false; // utilisateur déjà existant
        }
        utilisateurs.add(utilisateur);
        return true;
    }

    // Trouver un utilisateur par son username
    public Utilisateur trouverUtilisateurParUsername(String username) {
        for (Utilisateur u : utilisateurs) {
            if (u.getNom().equals(username)) {
                return u;
            }
        }
        return null;
    }

    // Authentifier un utilisateur par username + mot de passe
    public boolean authentifier(String username, String password) {
        Utilisateur user = trouverUtilisateurParUsername(username);
        if (user != null) {
            return user.getMotDePasse().equals(password);
        }
        return false;
    }

    // Supprimer un utilisateur par username
    public boolean supprimerUtilisateur(String username) {
        Utilisateur user = trouverUtilisateurParUsername(username);
        if (user != null) {
            utilisateurs.remove(user);
            return true;
        }
        return false;
    }

    // Liste des utilisateurs (copie pour éviter modification externe)
    public List<Utilisateur> getUtilisateurs() {
        return new ArrayList<>(utilisateurs);
    }
}
