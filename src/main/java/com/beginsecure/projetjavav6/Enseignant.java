package com.beginsecure.projetjavav6;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects; // NOUVEAU: Importation pour Objects.hash et Objects.equals

public class Enseignant extends Utilisateur implements GestionEDT {
    private String numEnseignant; // Identifiant unique pour l'enseignant
    private String specialite;
    private List<Cours> coursDonnes; // Liste des cours que cet enseignant donne

    // Constructeur
    public Enseignant(String numEnseignant, String specialite, String nom, String prenom, String email, String telephone, String motDePasse) {
        // Appel au constructeur de la classe parente Utilisateur
        super(nom, prenom, email, telephone, motDePasse, "Enseignant");
        this.numEnseignant = numEnseignant;
        this.specialite = specialite;
        this.coursDonnes = new ArrayList<>(); // Initialise la liste des cours donnés
    }

    // Getters
    public String getNumEnseignant() { return numEnseignant; }
    public String getSpecialite() { return specialite; }
    public List<Cours> getCoursDonnes() { return new ArrayList<>(coursDonnes); } // Retourne une copie

    // Setters
    public void setSpecialite(String specialite) { this.specialite = specialite; }
    // Setter pour numEnseignant n'est généralement pas fourni car c'est un identifiant unique

    // Méthodes spécifiques à l'enseignant
    public void ajouterCoursDonne(Cours cours) {
        if (cours != null && !coursDonnes.contains(cours)) {
            coursDonnes.add(cours);
            System.out.println("ENSEIGNANT " + getNom() + ": Cours " + cours.getNom() + " ajouté à votre emploi du temps.");
        } else {
            System.out.println("ENSEIGNANT " + getNom() + ": Impossible d'ajouter le cours " + (cours != null ? cours.getNom() : "nul") + " (déjà présent ou nul).");
        }
    }

    public void retirerCoursDonne(Cours cours) {
        if (cours != null) {
            if (coursDonnes.remove(cours)) {
                System.out.println("ENSEIGNANT " + getNom() + ": Cours " + cours.getNom() + " retiré de votre emploi du temps.");
            } else {
                System.out.println("ENSEIGNANT " + getNom() + ": Le cours " + cours.getNom() + " n'était pas dans votre emploi du temps.");
            }
        } else {
            System.out.println("ENSEIGNANT " + getNom() + ": Cours à retirer est nul.");
        }
    }

    // Implémentation de l'interface GestionEDT
    @Override
    public void consulterEmploiDuTemps() {
        System.out.println("\n--- ENSEIGNANT " + getNom() + " " + getPrenom() + ": Votre Emploi du Temps ---");
        if (coursDonnes.isEmpty()) {
            System.out.println("Vous n'avez aucun cours assigné pour le moment.");
            return;
        }
        // Tri des cours pour un affichage clair
        coursDonnes.sort((c1, c2) -> {
            int dateCompare = c1.getDateCours().compareTo(c2.getDateCours());
            if (dateCompare != 0) return dateCompare;
            return c1.getHeureDebut().compareTo(c2.getHeureDebut());
        });

        for (Cours cours : coursDonnes) {
            System.out.println(cours);
        }
    }

    // Méthode pour afficher la liste des étudiants par cours donnés par cet enseignant
    public String afficherListeEtudiantsParCours() { // Changé en String pour retour UI
        StringBuilder sb = new StringBuilder();
        if (coursDonnes.isEmpty()) {
            sb.append("Vous n'avez aucun cours pour lequel afficher les étudiants.");
            return sb.toString();
        }

        sb.append("Liste des étudiants par cours pour l'enseignant : ").append(getNom()).append(" ").append(getPrenom()).append("\n");
        for (Cours cours : coursDonnes) {
            sb.append("\n--- Cours: ").append(cours.getNom()).append(" (").append(cours.getDateCours()).append(" ").append(cours.getHeureDebut()).append("-").append(cours.getHeureFin()).append(") ---\n");
            if (cours.getEtudiantsInscrits() == null || cours.getEtudiantsInscrits().isEmpty()) {
                sb.append("  Aucun étudiant inscrit à ce cours.\n");
            } else {
                sb.append("  Étudiants inscrits :\n");
                for (Etudiant etudiant : cours.getEtudiantsInscrits()) {
                    sb.append("    - ").append(etudiant.getNom()).append(" ").append(etudiant.getPrenom()).append(" (").append(etudiant.getNumEtudiant()).append(")\n");
                }
            }
        }
        return sb.toString();
    }


    // Méthode pour signaler un conflit d'horaire ou de salle
    public void signalerConflit() {
        System.out.println("\n--- ENSEIGNANT " + getNom() + " " + getPrenom() + ": Vérification des conflits ---");
        boolean conflitTrouve = false;

        // Vérifier les conflits d'horaire entre les propres cours de l'enseignant
        for (int i = 0; i < coursDonnes.size(); i++) {
            for (int j = i + 1; j < coursDonnes.size(); j++) {
                Cours c1 = coursDonnes.get(i);
                Cours c2 = coursDonnes.get(j);

                // Si les cours sont le même jour
                if (c1.getDateCours().equals(c2.getDateCours())) {
                    try {
                        LocalTime debut1 = LocalTime.parse(c1.getHeureDebut());
                        LocalTime fin1 = LocalTime.parse(c1.getHeureFin());
                        LocalTime debut2 = LocalTime.parse(c2.getHeureDebut());
                        LocalTime fin2 = LocalTime.parse(c2.getHeureFin());

                        // Condition de chevauchement: (Début1 < Fin2) ET (Début2 < Fin1)
                        if (debut1.isBefore(fin2) && debut2.isBefore(fin1)) {
                            String message = "Conflit d'horaire pour l'enseignant " + getNom() + " " + getPrenom() +
                                    " entre le cours '" + c1.getNom() + "' (" + c1.getDateCours() + " " + c1.getHeureDebut() + "-" + c1.getHeureFin() +
                                    ") et le cours '" + c2.getNom() + "' (" + c2.getDateCours() + " " + c2.getHeureDebut() + "-" + c2.getHeureFin() + ").";
                            System.out.println(message);
                            Administrateur.signalerConflitGlobal(message); // Signaler à l'administrateur
                            conflitTrouve = true;
                        }
                    } catch (DateTimeParseException e) {
                        System.err.println("Erreur de format d'heure lors de la vérification des conflits pour " + getNom() + ": " + e.getMessage());
                    }
                }
            }
        }

        // Vérifier les conflits de salle (si une salle est réservée par un cours de cet enseignant et un autre cours)
        // Cette vérification est plus complexe et nécessiterait d'accéder à tous les cours du système.
        // Pour l'instant, nous nous concentrons sur les conflits d'horaire de l'enseignant.
        // La classe Salle gère déjà les conflits lors de la réservation.

        if (!conflitTrouve) {
            System.out.println("Aucun conflit d'horaire détecté pour " + getNom() + " " + getPrenom() + ".");
        }
    }

    // Implémentation de la méthode abstraite recevoirNotification()
    @Override
    public void recevoirNotification(String message) { // NOUVEAU: Signature mise à jour
        System.out.println("ENSEIGNANT " + getNom() + " " + getPrenom() + " a reçu une notification: " + message);
        // Vous pouvez ajouter une logique spécifique ici si l'enseignant doit traiter les notifications différemment.
    }

    // Implémentation des méthodes equals et hashCode pour une comparaison correcte des objets Enseignant
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false; // Compare les attributs de la classe parente
        Enseignant that = (Enseignant) o;
        return Objects.equals(numEnseignant, that.numEnseignant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), numEnseignant);
    }

    @Override
    public String toString() {
        return "Enseignant [numEnseignant=" + numEnseignant + ", specialite=" + specialite + ", " + super.toString() + "]";
    }
}
