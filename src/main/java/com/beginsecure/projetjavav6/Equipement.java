package com.beginsecure.projetjavav6;

import java.util.Objects;

// 7. Classe Equipement
public class Equipement {
    private String nom;
    private String description;

    // Constructeur
    public Equipement(String nom, String description) {
        this.nom = nom;
        this.description = description;
    }

    // Getters
    public String getNom() { return nom; }
    public String getDescription() { return description; }

    // Setters
    public void setNom(String nom) { this.nom = nom; }
    public void setDescription(String description) { this.description = description; }

    // Pour comparer deux objets Equipement (utile dans les listes).
    // Un équipement est considéré comme unique par son nom.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        // Utilisation de getClass() pour s'assurer que les objets sont de la même classe exacte
        if (o == null || getClass() != o.getClass()) return false;
        Equipement that = (Equipement) o;
        return Objects.equals(nom, that.nom); // Utilise Objects.equals pour gérer les cas de nom null
    }

    // La méthode hashCode() a été supprimée comme demandé.
    // Soyez conscient des implications mentionnées précédemment concernant les collections basées sur le hachage.

    // Représentation textuelle de l'objet Equipement
    @Override
    public String toString() {
        return "Équipement: " + nom + " (" + description + ")";
    }
}