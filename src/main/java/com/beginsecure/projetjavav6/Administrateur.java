package com.beginsecure.projetjavav6;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Comparator; // Ajout pour le tri des cours dans consulterEmploiDuTemps

// 2. Classe Administrateur
public class Administrateur extends Utilisateur implements GestionEDT {
    // Listes statiques pour stocker toutes les données du système (comme une petite base de données)
    // 'static' signifie que ces listes appartiennent à la classe Administrateur elle-même.
    // Nous utilisons ArrayList pour toutes les listes.
    private static List<Administrateur> tousLesAdministrateurs = new ArrayList<>(); // NOUVEAU: Liste pour les administrateurs
    private static List<Enseignant> enseignantsGlobaux = new ArrayList<>();
    private static List<Etudiant> etudiantsGlobaux = new ArrayList<>();
    private static List<Salle> sallesGlobales = new ArrayList<>();
    private static List<Cours> tousLesCours = new ArrayList<>();
    private static List<String> signalisationsConflits = new ArrayList<>(); // Pour les messages de conflit

    private String numAdmin; // Attribut spécifique à l'Administrateur, utilisé comme identifiant unique

    // Constructeur : 'id' a été retiré, 'numAdmin' est le premier paramètre
    public Administrateur(String numAdmin, String nom, String prenom, String email, String telephone, String motDePasse) {
        // Appel au constructeur de Utilisateur. Le rôle "Administrateur" est passé explicitement.
        super(nom, prenom, email, telephone, motDePasse, "Administrateur");
        this.numAdmin = numAdmin; // Initialisation de numAdmin
        // Ajoute l'instance actuelle à la liste globale des administrateurs lors de la création
        if (!tousLesAdministrateurs.contains(this)) {
            tousLesAdministrateurs.add(this);
            System.out.println("ADMIN: Administrateur " + this.getNom() + " ajouté globalement.");
        }
    }

    // --- Getters et Setters pour numAdmin ---
    public String getNumAdmin() {
        return numAdmin;
    }

    public void setNumAdmin(String numAdmin) {
        this.numAdmin = numAdmin;
    }

    // --- Méthodes pour gérer les listes globales (inchangées car déjà statiques et correctes) ---

    // NOUVEAU GETTER: Pour accéder à la liste des administrateurs
    public static List<Administrateur> getAdministrateursGlobaux() {
        return new ArrayList<>(tousLesAdministrateurs);
    }

    public static void ajouterEnseignantGlobal(Enseignant enseignant) {
        if (enseignant != null && !enseignantsGlobaux.contains(enseignant)) {
            enseignantsGlobaux.add(enseignant);
            System.out.println("ADMIN: Enseignant " + enseignant.getNom() + " ajouté globalement.");
        } else {
            System.out.println("ADMIN: Impossible d'ajouter l'enseignant (nul ou déjà présent globalement).");
        }
    }

    public static void ajouterEtudiantGlobal(Etudiant etudiant) {
        if (etudiant != null && !etudiantsGlobaux.contains(etudiant)) {
            etudiantsGlobaux.add(etudiant);
            System.out.println("ADMIN: Étudiant " + etudiant.getNom() + " ajouté globalement.");
        } else {
            System.out.println("ADMIN: Impossible d'ajouter l'étudiant (nul ou déjà présent globalement).");
        }
    }

    public static void ajouterSalle(Salle salle) {
        if (salle != null && !sallesGlobales.contains(salle)) {
            sallesGlobales.add(salle);
            System.out.println("ADMIN: Salle " + salle.getNumSalle() + " ajoutée.");
        } else {
            System.out.println("ADMIN: Impossible d'ajouter la salle (nulle ou déjà présente).");
        }
    }

    public static void ajouterCours(Cours cours) {
        if (cours != null && !tousLesCours.contains(cours)) {
            tousLesCours.add(cours);
            System.out.println("ADMIN: Cours " + cours.getNom() + " ajouté.");
        } else {
            System.out.println("ADMIN: Impossible d'ajouter le cours (nul ou déjà présent).");
        }
    }

    public static void supprimerCours(Cours cours) {
        if (cours != null) {
            boolean supprime = false;
            if (tousLesCours.remove(cours)) { // Utilise equals() de Cours
                supprime = true;
                System.out.println("ADMIN: Cours " + cours.getNom() + " supprimé.");

                // Logique de libération de salle et de retrait des emplois du temps des utilisateurs
                if (cours.getSalle() != null) {
                    cours.getSalle().liberer(cours.getDateCours(), cours.getHeureDebut(), cours.getHeureFin());
                }
                if (cours.getEnseignant() != null) {
                    cours.getEnseignant().retirerCoursDonne(cours);
                }
                for (Etudiant etudiant : cours.getEtudiantsInscrits()) {
                    etudiant.retirerCoursSuivi(cours);
                }
            }
            if (!supprime) {
                System.out.println("ADMIN: Impossible de supprimer le cours (non trouvé).");
            }
        } else {
            System.out.println("ADMIN: Impossible de supprimer le cours (nul).");
        }
    }

    // Getters pour accéder aux listes globales depuis l'extérieur (retournent des copies)
    public static List<Enseignant> getEnseignantsGlobaux() {
        return new ArrayList<>(enseignantsGlobaux);
    }
    public static List<Etudiant> getEtudiantsGlobaux() {
        return new ArrayList<>(etudiantsGlobaux);
    }
    public static List<Salle> getToutesLesSalles() {
        return new ArrayList<>(sallesGlobales);
    }
    public static List<Cours> getTousLesCours() {
        return new ArrayList<>(tousLesCours);
    }
    public static List<String> getSignalisationsConflits() {
        return new ArrayList<>(signalisationsConflits);
    }

    // NOUVELLE MÉTHODE : Ajoute une signalisation de conflit à la liste globale
    public static void signalerConflitGlobal(String message) {
        if (message != null && !message.isEmpty()) {
            signalisationsConflits.add(message);
            System.out.println("ADMIN (System): Nouvelle signalisation de conflit enregistrée.");
        }
    }

    // --- Méthodes spécifiques à l'Administrateur ---

    // Créer un nouveau cours
    public Cours creerCours(String nom, String duree, String heureDebut, String heureFin, String dateCours, Enseignant enseignant, Salle salle) {
        if (enseignant == null || salle == null) {
            System.err.println("Erreur: L'enseignant et la salle sont requis pour créer un cours.");
            return null;
        }

        if (!salle.verifierDisponibilite(dateCours, heureDebut, heureFin)) {
            System.out.println("Impossible de créer le cours " + nom + ": La salle " + salle.getNumSalle() + " n'est pas disponible le " + dateCours + " de " + heureDebut + " à " + heureFin + ".");
            return null;
        }

        Cours nouveauCours = new Cours(nom, duree, heureDebut, heureFin, dateCours, enseignant, salle);
        Administrateur.ajouterCours(nouveauCours); // Ajouter le cours à la liste globale
        salle.reserver(dateCours, heureDebut, heureFin); // Réserver le créneau de la salle

        System.out.println("ADMIN: Cours '" + nom + "' créé et planifié.");
        return nouveauCours;
    }

    // Modifier un cours existant
    public void modifierCours(Cours cours, String nouveauNom, String nouvelleDuree, String nouvelleHeureDebut, String nouvelleHeureFin, String nouvelleDateCours, Enseignant nouvelEnseignant, Salle nouvelleSalle) {
        if (cours == null) {
            System.out.println("ADMIN: Erreur: Le cours à modifier est nul.");
            return;
        }

        String ancienneDate = cours.getDateCours();
        String ancienneHeureDebut = cours.getHeureDebut();
        String ancienneHeureFin = cours.getHeureFin();
        Salle ancienneSalle = cours.getSalle();
        Enseignant ancienEnseignant = cours.getEnseignant();

        // Temporairement retirer le cours des emplois du temps pour vérifier la nouvelle disponibilité
        if (ancienneSalle != null) ancienneSalle.liberer(ancienneDate, ancienneHeureDebut, ancienneHeureFin);
        if (ancienEnseignant != null) ancienEnseignant.retirerCoursDonne(cours); // Assurez-vous que cette méthode existe

        // Appliquer les modifications temporairement pour la vérification
        String tempNom = (nouveauNom != null) ? nouveauNom : cours.getNom();
        String tempDuree = (nouvelleDuree != null) ? nouvelleDuree : cours.getDuree();
        String tempHeureDebut = (nouvelleHeureDebut != null) ? nouvelleHeureDebut : cours.getHeureDebut();
        String tempHeureFin = (nouvelleHeureFin != null) ? nouvelleHeureFin : cours.getHeureFin();
        String tempDateCours = (nouvelleDateCours != null) ? nouvelleDateCours : cours.getDateCours();
        Enseignant tempEnseignant = (nouvelEnseignant != null) ? nouvelEnseignant : cours.getEnseignant();
        Salle tempSalle = (nouvelleSalle != null) ? nouvelleSalle : cours.getSalle();

        boolean possible = true;
        // Vérifier la disponibilité de la salle
        if (tempSalle != null && !tempSalle.verifierDisponibilite(tempDateCours, tempHeureDebut, tempHeureFin)) {
            System.out.println("Modification impossible: La salle " + tempSalle.getNumSalle() + " n'est pas disponible pour le nouveau créneau.");
            possible = false;
        }
        // Il faudrait aussi vérifier la disponibilité de l'enseignant de manière plus robuste.

        if (possible) {
            // Appliquer les modifications définitives
            cours.setNom(tempNom);
            cours.setDuree(tempDuree);
            cours.setHeureDebut(tempHeureDebut);
            cours.setHeureFin(tempHeureFin);
            cours.setDateCours(tempDateCours);
            cours.setEnseignant(tempEnseignant); // Le setter gère l'ajout/retrait de l'emploi du temps de l'enseignant
            cours.setSalle(tempSalle); // Le setter gère l'ajout/retrait de la salle

            System.out.println("ADMIN: Cours '" + cours.getNom() + "' modifié avec succès.");
        } else {
            // Rétablir les anciennes valeurs et ré-ajouter aux emplois du temps
            if (ancienneSalle != null) ancienneSalle.reserver(ancienneDate, ancienneHeureDebut, ancienneHeureFin);
            if (ancienEnseignant != null) ancienEnseignant.ajouterCoursDonne(cours);
            System.out.println("Modification du cours " + cours.getNom() + " annulée en raison d'un conflit de disponibilité.");
        }
    }

    // Affecter un enseignant à un cours
    public void affecterEnseignant(Enseignant enseignant, Cours cours) {
        if (enseignant != null && cours != null) {
            if (cours.getEnseignant() != null) {
                cours.getEnseignant().retirerCoursDonne(cours);
            }
            cours.setEnseignant(enseignant); // Affecte l'enseignant au cours
            enseignant.ajouterCoursDonne(cours); // Ajoute le cours à la liste de l'enseignant
            System.out.println("ADMIN: Enseignant " + enseignant.getNom() + " affecté au cours " + cours.getNom() + ".");
        } else {
            System.out.println("ADMIN: Erreur: Enseignant ou Cours est nul, impossible d'affecter.");
        }
    }

    // Réserver une salle pour un usage autre qu'un cours (ex: réunion)
    public void reserverSalle(Salle salle, String date, String heureDebut, String heureFin) {
        if (salle != null && salle.verifierDisponibilite(date, heureDebut, heureFin)) {
            salle.reserver(date, heureDebut, heureFin);
            System.out.println("ADMIN: Salle " + (salle != null ? salle.getNumSalle() : "null") + " réservée pour le " + date + " de " + heureDebut + " à " + heureFin + ".");
        } else {
            System.out.println("ADMIN: Impossible de réserver la salle " + (salle != null ? salle.getNumSalle() : "null") + " pour ce créneau.");
        }
    }

    // Consulter l'emploi du temps global (Implémentation de l'interface GestionEDT)
    @Override
    public void consulterEmploiDuTemps() {
        System.out.println("\n--- ADMIN: Emploi du Temps Général ---");
        if (tousLesCours.isEmpty()) {
            System.out.println("Aucun cours planifié dans le système.");
            return;
        }
        // Pour un affichage plus clair, vous pourriez trier les cours par date et heure
        tousLesCours.sort(Comparator
                .comparing(Cours::getDateCours)
                .thenComparing(Cours::getHeureDebut));

        for (Cours cours : tousLesCours) {
            System.out.println(cours); // Affiche les détails du cours (nécessite un bon toString() dans Cours)
        }
    }

    // Consulter les signalisations de conflits
    public void consulterSignalisations() {
        System.out.println("\n--- ADMIN: Signalisations de Conflits ---");
        if (signalisationsConflits.isEmpty()) {
            System.out.println("Aucune nouvelle signalisation.");
        } else {
            for (String signal : signalisationsConflits) {
                System.out.println("- " + signal);
            }
            signalisationsConflits.clear(); // Efface les signalisations après les avoir consultées
            System.out.println("Signalisations consultées et effacées.");
        }
    }

    // Consulter les statistiques générales
    public void consulterStatistiques() {
        System.out.println("\n--- ADMIN: Statistiques de l'Université ---");
        System.out.println("Total Enseignants : " + enseignantsGlobaux.size());
        System.out.println("Total Étudiants   : " + etudiantsGlobaux.size());
        System.out.println("Total Salles      : " + sallesGlobales.size());
        System.out.println("Total Cours       : " + tousLesCours.size());
        // Vous pourriez ajouter des statistiques plus détaillées ici
        System.out.println("\n--- Heures enseignées par professeur ---");
        for (Enseignant enseignant : enseignantsGlobaux) {
            long heuresTotalesMinutes = 0;
            // Supposons que Enseignant a une méthode getCoursDonnes()
            for (Cours cours : enseignant.getCoursDonnes()) {
                // Conversion de la durée (ex: "2h", "1h30m") en minutes
                String duree = cours.getDuree();
                try {
                    int hours = 0;
                    int minutes = 0;
                    if (duree.contains("h")) {
                        String[] parts = duree.split("h");
                        hours = Integer.parseInt(parts[0].trim());
                        if (parts.length > 1 && parts[1].contains("m")) {
                            minutes = Integer.parseInt(parts[1].replace("m", "").trim());
                        }
                    } else if (duree.contains("m")) {
                        minutes = Integer.parseInt(duree.replace("m", "").trim());
                    }
                    heuresTotalesMinutes += (hours * 60 + minutes);
                } catch (NumberFormatException e) {
                    System.err.println("ADMIN: Erreur de format de durée pour le cours " + cours.getNom() + ": " + duree);
                }
            }
            System.out.printf("- %s %s (%s): %.1f heures\n", enseignant.getPrenom(), enseignant.getNom(), enseignant.getSpecialite(), (double) heuresTotalesMinutes / 60.0);
        }

        System.out.println("\n--- Taux d'occupation des salles (estimation hebdomadaire) ---");
        // Hypothèse: Une salle est disponible 8 heures par jour, 5 jours par semaine.
        // Cela représente 40 heures par semaine, soit 2400 minutes.
        final int TOTAL_MINUTES_AVAILABLE_PER_WEEK = 8 * 60 * 5; // 40 heures * 60 minutes/heure

        for (Salle salle : sallesGlobales) {
            long minutesOccupied = 0;
            for (String[] creneau : salle.getCreneauxOccupes()) { // Cette ligne nécessite le getter dans Salle
                try {
                    LocalTime debut = LocalTime.parse(creneau[1]);
                    LocalTime fin = LocalTime.parse(creneau[2]);
                    minutesOccupied += java.time.Duration.between(debut, fin).toMinutes();
                } catch (DateTimeParseException e) {
                    System.err.println("ADMIN: Erreur de format d'heure pour le créneau de salle " + salle.getNumSalle() + ": " + creneau[1] + "-" + creneau[2] + " - " + e.getMessage());
                }
            }

            double occupationRate = 0.0;
            if (TOTAL_MINUTES_AVAILABLE_PER_WEEK > 0) {
                occupationRate = ((double) minutesOccupied / TOTAL_MINUTES_AVAILABLE_PER_WEEK) * 100.0;
            }

            System.out.printf("- Salle %s: %d minutes occupées (%.1f%% d'occupation)\n", salle.getNumSalle(), minutesOccupied, occupationRate);
        }

    }

    // IMPLÉMENTATION DE LA MÉTHODE ABSTRAITE 'recevoirNotification()'
    @Override
    public void recevoirNotification(String message) { // NOUVEAU: Signature mise à jour
        System.out.println("ADMIN: " + getNom() + " " + getPrenom() + " a reçu une notification: " + message);
        // Ici, vous pourriez ajouter une logique spécifique pour l'administrateur,
        // par exemple, stocker les notifications dans une liste spécifique à l'administrateur
        // si vous ne voulez pas qu'elles soient toutes dans NotificationManager.
    }

    // Implémentation des méthodes equals et hashCode pour une comparaison correcte des objets Administrateur
    // Ceci est crucial si vous voulez que les méthodes comme List.contains() fonctionnent correctement
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        // Super.equals(o) est important si la classe Utilisateur a une logique d'égalité basée sur ses propres attributs.
        // Si Utilisateur n'implémente pas equals, alors 'super.equals(o)' vérifiera l'identité de l'objet (o == this).
        // Dans ce cas, nous baserons l'égalité principalement sur numAdmin.
        if (!super.equals(o)) return false; // Optionnel, dépend de l'implémentation de Utilisateur.equals()
        Administrateur that = (Administrateur) o;
        return Objects.equals(numAdmin, that.numAdmin);
    }

    @Override
    public int hashCode() {
        // Incluez le hash de la classe parente si super.equals() est utilisée.
        return Objects.hash(super.hashCode(), numAdmin);
    }

    @Override
    public String toString() {
        return "Administrateur [numAdmin=" + numAdmin + ", " + super.toString() + "]";
    }
}
