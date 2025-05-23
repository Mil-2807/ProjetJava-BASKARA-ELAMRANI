package com.beginsecure.projetjavav6;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects; // NOUVEAU: Importation pour Objects.hash et Objects.equals

public class Etudiant extends Utilisateur implements GestionEDT {
    private String numEtudiant; // Identifiant unique pour l'étudiant
    private String formation;
    private List<Cours> coursSuivis; // Liste des cours auxquels cet étudiant est inscrit

    // Constructeur
    public Etudiant(String numEtudiant, String formation, String nom, String prenom, String email, String telephone, String motDePasse) {
        // Appel au constructeur de la classe parente Utilisateur
        super(nom, prenom, email, telephone, motDePasse, "Etudiant");
        this.numEtudiant = numEtudiant;
        this.formation = formation;
        this.coursSuivis = new ArrayList<>(); // Initialise la liste des cours suivis
    }

    // Getters
    public String getNumEtudiant() { return numEtudiant; }
    public String getFormation() { return formation; }
    public List<Cours> getCoursSuivis() { return new ArrayList<>(coursSuivis); } // Retourne une copie

    // Setters
    public void setFormation(String formation) { this.formation = formation; }
    // Setter pour numEtudiant n'est généralement pas fourni car c'est un identifiant unique

    // Méthodes spécifiques à l'étudiant
    public void ajouterCoursSuivi(Cours cours) {
        if (cours != null && !coursSuivis.contains(cours)) {
            coursSuivis.add(cours);
            System.out.println("ETUDIANT " + getNom() + ": Cours " + cours.getNom() + " ajouté à votre emploi du temps.");
        } else {
            System.out.println("ETUDIANT " + getNom() + ": Impossible d'ajouter le cours " + (cours != null ? cours.getNom() : "nul") + " (déjà présent ou nul).");
        }
    }

    public void retirerCoursSuivi(Cours cours) {
        if (cours != null) {
            if (coursSuivis.remove(cours)) {
                System.out.println("ETUDIANT " + getNom() + ": Cours " + cours.getNom() + " retiré de votre emploi du temps.");
            } else {
                System.out.println("ETUDIANT " + getNom() + ": Le cours " + cours.getNom() + " n'était pas dans votre emploi du temps.");
            }
        } else {
            System.out.println("ETUDIANT " + getNom() + ": Cours à retirer est nul.");
        }
    }

    // Implémentation de l'interface GestionEDT
    @Override
    public void consulterEmploiDuTemps() {
        System.out.println("\n--- ETUDIANT " + getNom() + " " + getPrenom() + ": Votre Emploi du Temps ---");
        if (coursSuivis.isEmpty()) {
            System.out.println("Vous n'êtes inscrit à aucun cours pour le moment.");
            return;
        }
        // Tri des cours pour un affichage clair
        coursSuivis.sort((c1, c2) -> {
            int dateCompare = c1.getDateCours().compareTo(c2.getDateCours());
            if (dateCompare != 0) return dateCompare;
            return c1.getHeureDebut().compareTo(c2.getHeureDebut());
        });

        for (Cours cours : coursSuivis) {
            System.out.println(cours);
        }
    }

    // Implémentation de la méthode abstraite recevoirNotification()
    @Override
    public void recevoirNotification(String message) { // NOUVEAU: Signature mise à jour
        System.out.println("ETUDIANT " + getNom() + " " + getPrenom() + " a reçu une notification: " + message);
        // Vous pouvez ajouter une logique spécifique ici si l'étudiant doit traiter les notifications différemment.
    }

    // Implémentation des méthodes equals et hashCode pour une comparaison correcte des objets Etudiant
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false; // Compare les attributs de la classe parente
        Etudiant etudiant = (Etudiant) o;
        return Objects.equals(numEtudiant, etudiant.numEtudiant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), numEtudiant);
    }

    @Override
    public String toString() {
        return "Etudiant [numEtudiant=" + numEtudiant + ", formation=" + formation + ", " + super.toString() + "]";
    }
}
