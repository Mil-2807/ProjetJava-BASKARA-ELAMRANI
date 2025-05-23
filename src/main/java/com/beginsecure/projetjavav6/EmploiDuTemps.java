package com.beginsecure.projetjavav6;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

// 9. Classe EmploiDuTemps (pour la consultation)
public class EmploiDuTemps {
    private String dateReference; // Date à partir de laquelle on veut afficher l'emploi du temps

    // Constructeur
    public EmploiDuTemps(String dateReference) {
        this.dateReference = dateReference;
    }

    // Affiche les couars pour un jour spécifique (la date de référence)
    public void afficherJour() {
        System.out.println("\n--- Emploi du Temps du " + dateReference + " ---");
        boolean coursTrouve = false;
        List<Cours> tousLesCours = Administrateur.getTousLesCours(); // Récupère la liste globale
        for (int i = 0; i < tousLesCours.size(); i++) {
            Cours cours = tousLesCours.get(i);
            if (cours.getDateCours().equals(dateReference)) {
                System.out.println(cours);
                coursTrouve = true;
            }
        }
        if (!coursTrouve) {
            System.out.println("Aucun cours prévu ce jour-là.");
        }
    }

    // Affiche les cours pour la semaine à partir de la date de référence
    public void afficherSemaine() {
        System.out.println("\n--- Emploi du Temps de la semaine du " + dateReference + " ---");
        LocalDate debutSemaine = LocalDate.parse(dateReference); // Convertit la String en objet Date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Format pour l'affichage

        for (int i = 0; i < 7; i++) { // Pour chaque jour de la semaine (7 jours)
            LocalDate dateCourante = debutSemaine.plusDays(i); // Ajoute des jours à la date de début
            String dateFormatee = dateCourante.format(formatter); // Formate la date pour l'affichage

            System.out.println("\n--- Jour: " + dateFormatee + " ---");
            boolean coursCeJour = false;
            List<Cours> tousLesCours = Administrateur.getTousLesCours(); // Récupère la liste globale
            for (int j = 0; j < tousLesCours.size(); j++) {
                Cours cours = tousLesCours.get(j);
                if (cours.getDateCours().equals(dateFormatee)) {
                    System.out.println(cours);
                    coursCeJour = true;
                }
            }
            if (!coursCeJour) {
                System.out.println("Aucun cours.");
            }
        }
    }

    // Affiche les cours pour le mois de la date de référence
    public void afficherMois() {
        // Obtient l'année et le mois de la date de référence (ex: "2025-06")
        String anneeMois = dateReference.substring(0, 7);
        System.out.println("\n--- Emploi du Temps du mois de " + anneeMois + " ---");

        LocalDate dateRef = LocalDate.parse(dateReference);
        LocalDate debutMois = dateRef.withDayOfMonth(1); // Premier jour du mois de la date de référence
        LocalDate finMois = debutMois.plusMonths(1).minusDays(1); // Dernier jour du mois (plus simple que lengthOfMonth)

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Boucler sur tous les jours du mois
        LocalDate dateCourante = debutMois;
        while (!dateCourante.isAfter(finMois)) {
            String dateFormatee = dateCourante.format(formatter);
            System.out.println("\n--- Jour: " + dateFormatee + " ---");
            boolean coursCeJour = false;
            List<Cours> tousLesCours = Administrateur.getTousLesCours(); // Récupère la liste globale
            for (int i = 0; i < tousLesCours.size(); i++) {
                Cours cours = tousLesCours.get(i);
                if (cours.getDateCours().equals(dateFormatee)) {
                    System.out.println(cours);
                    coursCeJour = true;
                }
            }
            if (!coursCeJour) {
                System.out.println("Aucun cours.");
            }
            dateCourante = dateCourante.plusDays(1);
        }
    }

    // Recherche des cours par critère (nom du cours, nom de l'enseignant, numéro de salle)
    public void rechercherCours(String critere) {
        System.out.println("\n--- Recherche de cours avec le critère '" + critere + "' ---");
        boolean coursTrouve = false;
        String critereMinuscule = critere.toLowerCase(); // Pour une recherche insensible à la casse

        List<Cours> tousLesCours = Administrateur.getTousLesCours(); // Récupère la liste globale
        for (int i = 0; i < tousLesCours.size(); i++) {
            Cours cours = tousLesCours.get(i);
            boolean match = false;

            // Vérifier le nom du cours
            if (cours.getNom().toLowerCase().contains(critereMinuscule)) {
                match = true;
            }

            // Vérifier le nom de l'enseignant
            if (cours.getEnseignant() != null && cours.getEnseignant().getNom().toLowerCase().contains(critereMinuscule)) {
                match = true;
            }

            // Vérifier le numéro de salle
            if (cours.getSalle() != null && cours.getSalle().getNumSalle().toLowerCase().contains(critereMinuscule)) {
                match = true;
            }

            if (match) {
                System.out.println(cours);
                coursTrouve = true;
            }
        }
        if (!coursTrouve) {
            System.out.println("Aucun cours trouvé correspondant à ce critère.");
        }
    }
}