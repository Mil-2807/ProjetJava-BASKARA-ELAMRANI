package com.beginsecure.projetjavav6;

import java.util.Objects;

// 1. Classe abstraite Utilisateur
public abstract class Utilisateur {
    // Attributs communs à tous les utilisateurs
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String motDePasse;
    private String role; // "Administrateur", "Enseignant", "Etudiant"

    // Constructeur
    public Utilisateur(String nom, String prenom, String email, String telephone, String motDePasse, String role) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.motDePasse = motDePasse; // En production, le mot de passe devrait être haché
        this.role = role;
    }

    // Méthodes abstraites (doivent être implémentées par les sous-classes)
    public abstract void recevoirNotification(String message); // NOUVEAU: Ajout du paramètre message

    // Méthodes concrètes (implémentées ici et héritées par les sous-classes)
    public void seConnecter() {
        System.out.println(this.role + " " + this.prenom + " " + this.nom + " s'est connecté.");
    }

    public void seDeconnecter() {
        System.out.println(this.role + " " + this.prenom + " " + this.nom + " s'est déconnecté.");
    }

    // Getters
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getEmail() { return email; }
    public String getTelephone() { return telephone; }
    public String getMotDePasse() { return motDePasse; } // Attention: ne pas exposer en production
    public String getRole() { return role; }

    // Setters
    public void setNom(String nom) { this.nom = nom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public void setEmail(String email) { this.email = email; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; } // En production, hacher le nouveau mot de passe

    // Implémentation de equals et hashCode basée sur l'email (qui doit être unique pour chaque utilisateur)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Utilisateur that = (Utilisateur) o;
        return Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public String toString() {
        return "Nom: " + nom + ", Prénom: " + prenom + ", Email: " + email + ", Téléphone: " + telephone + ", Rôle: " + role;
    }
}
