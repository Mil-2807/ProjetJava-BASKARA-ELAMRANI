package com.beginsecure.projetjavav6;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// 5. Classe Cours
public class Cours {
    private String nom;
    private String duree; // ex: "2h", "1h30m"
    private String heureDebut; // ex: "09:00"
    private String heureFin;   // ex: "11:00"
    // private String filiere; // Attribut 'filiere' supprimé
    private String dateCours; // Format "AAAA-MM-JJ"
    private Enseignant enseignant;
    private Salle salle;
    private List<Etudiant> etudiantsInscrits;

    // Constructeur mis à jour : 'filiere' a été retiré des paramètres
    public Cours(String nom, String duree, String heureDebut, String heureFin, String dateCours, Enseignant enseignant, Salle salle) {
        this.nom = nom;
        this.duree = duree;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        // this.filiere = filiere; // Initialisation de 'filiere' supprimée
        this.dateCours = dateCours;
        this.enseignant = enseignant;
        this.salle = salle;
        this.etudiantsInscrits = new ArrayList<>();
        // Quand un cours est créé, il est automatiquement ajouté à l'emploi du temps de l'enseignant
        if (enseignant != null) {
            enseignant.ajouterCoursDonne(this); // Assurez-vous que cette méthode est bien implémentée dans Enseignant
        }
    }

    // Ajoute un étudiant à ce cours
    public void ajouterEtudiant(Etudiant etudiant) {
        if (etudiant != null) {
            if (!etudiantsInscrits.contains(etudiant)) {
                etudiantsInscrits.add(etudiant);
                etudiant.ajouterCoursSuivi(this); // Ajoute ce cours à l'emploi du temps de l'étudiant
                System.out.println("COURS " + this.nom + ": Étudiant " + etudiant.getNom() + " inscrit avec succès.");
            } else {
                System.out.println("COURS " + this.nom + ": L'étudiant " + etudiant.getNom() + " est déjà inscrit à ce cours.");
            }
        } else {
            System.out.println("COURS " + this.nom + ": Impossible d'inscrire un étudiant nul.");
        }
    }

    // Retire un étudiant de ce cours
    public void retirerEtudiant(Etudiant etudiant) {
        if (etudiant != null) {
            // Utilise la méthode remove de List, qui se base sur equals() d'Etudiant.
            // Si Etudiant.equals() est bien implémentée, cela fonctionne.
            if (etudiantsInscrits.remove(etudiant)) {
                etudiant.retirerCoursSuivi(this); // Retire ce cours de l'emploi du temps de l'étudiant
                System.out.println("COURS " + this.nom + ": Étudiant " + etudiant.getNom() + " retiré avec succès.");
            } else {
                System.out.println("COURS " + this.nom + ": L'étudiant " + etudiant.getNom() + " n'était pas inscrit à ce cours.");
            }
        } else {
            System.out.println("COURS " + this.nom + ": L'étudiant à retirer est nul.");
        }
    }

    // Getters (accès aux attributs)
    public String getNom() { return nom; }
    public String getDuree() { return duree; }
    public String getHeureDebut() { return heureDebut; }
    public String getHeureFin() { return heureFin; }
    // public String getFiliere() { return filiere; } // Getter pour 'filiere' supprimé
    public String getDateCours() { return dateCours; }
    public Enseignant getEnseignant() { return enseignant; }
    public Salle getSalle() { return salle; }
    public List<Etudiant> getEtudiantsInscrits() { return new ArrayList<>(etudiantsInscrits); } // Retourne une copie

    // Setters (modification des attributs)
    public void setNom(String nom) { this.nom = nom; }
    public void setDuree(String duree) { this.duree = duree; }
    public void setHeureDebut(String heureDebut) { this.heureDebut = heureDebut; }
    public void setHeureFin(String heureFin) { this.heureFin = heureFin; }
    // public void setFiliere(String filiere) { this.filiere = filiere; } // Setter pour 'filiere' supprimé
    public void setDateCours(String dateCours) { this.dateCours = dateCours; }
    public void setEnseignant(Enseignant enseignant) {
        // Logique pour potentiellement retirer le cours de l'ancien enseignant
        if (this.enseignant != null && !this.enseignant.equals(enseignant)) {
            this.enseignant.retirerCoursDonne(this);
        }
        this.enseignant = enseignant;
        // Et l'ajouter au nouvel enseignant
        if (enseignant != null) {
            enseignant.ajouterCoursDonne(this);
        }
    }
    public void setSalle(Salle salle) {
        // Logique pour potentiellement libérer l'ancienne salle si elle est différente
        if (this.salle != null && !this.salle.equals(salle)) {
            //this.salle.liberer(this.dateCours, this.heureDebut, this.heureFin); // Décommenter si Salle.liberer est adaptée
            System.out.println("COURS: Logique de libération de l'ancienne salle à implémenter lors du changement de salle.");
        }
        this.salle = salle;
        // Et réserver la nouvelle salle
        if (salle != null) {
            //salle.reserver(this.dateCours, this.heureDebut, this.heureFin); // Décommenter si Salle.reserver est adaptée
            System.out.println("COURS: Logique de réservation de la nouvelle salle à implémenter lors du changement de salle.");
        }
    }

    // Implémentation de la méthode equals pour définir l'égalité entre deux objets Cours.
    // Un cours est considéré comme le même si son nom, sa date, ses heures, son enseignant et sa salle sont identiques.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        // Utilisation de getClass() pour s'assurer que les objets sont de la même classe exacte
        if (o == null || getClass() != o.getClass()) return false;
        Cours cours = (Cours) o;

        // Comparaison de tous les attributs clés qui définissent l'unicité d'un cours
        // 'filiere' n'est plus utilisé ici
        return Objects.equals(nom, cours.nom) &&
                Objects.equals(dateCours, cours.dateCours) &&
                Objects.equals(heureDebut, cours.heureDebut) &&
                Objects.equals(heureFin, cours.heureFin) &&
                Objects.equals(salle != null ? salle.getNumSalle() : null, cours.salle != null ? cours.salle.getNumSalle() : null) &&
                Objects.equals(enseignant != null ? enseignant.getNumEnseignant() : null, cours.enseignant != null ? cours.enseignant.getNumEnseignant() : null);
    }

    // La méthode hashCode() est délibérément absente comme demandé.

    // Représentation textuelle de l'objet Cours
    @Override
    public String toString() {
        String enseignantInfo = (enseignant != null) ? enseignant.getNom() + " (" + enseignant.getNumEnseignant() + ")" : "Non assigné";
        String salleInfo = (salle != null) ? salle.getNumSalle() : "Non assignée";
        return "Cours: " + nom +
                " (" + duree + ")" +
                " le " + dateCours +
                " de " + heureDebut + " à " + heureFin +
                // " | Filière: " + filiere + // Ligne pour 'filiere' supprimée
                " | Enseignant: " + enseignantInfo +
                " | Salle: " + salleInfo +
                " | Étudiants inscrits: " + etudiantsInscrits.size();
    }
}