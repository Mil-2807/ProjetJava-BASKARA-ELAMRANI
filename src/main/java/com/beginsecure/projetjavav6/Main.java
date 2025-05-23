package com.beginsecure.projetjavav6;

import java.util.List;

// 10. Classe Main (pour exécuter et tester le programme)
public class Main {
    public static void main(String[] args) {
        System.out.println("--- Démarrage de l'application de gestion d'emploi du temps (Version Très Simple) ---");

        // --- PARTIE 1: Initialisation des Données ---

        System.out.println("\n--- 1. Création des utilisateurs, salles et équipements ---");

        // Création de l'Administrateur
        Administrateur admin = new Administrateur("ADM001", "Dupont", "Jean", "jean.dupont@univ.fr", "0123456789", "adminpass");
        admin.seConnecter();

        // Création des Enseignants
        Enseignant profMath = new Enseignant("ENS001", "Mathématiques", "Martin", "Sophie", "sophie.martin@univ.fr", "0611223344", "profmath");
        profMath.seConnecter();
        Enseignant profPhysique = new Enseignant("ENS002", "Physique", "Lefevre", "Paul", "paul.lefevre@univ.fr", "0655443322", "profphysique");
        Enseignant profInfo = new Enseignant("ENS003", "Informatique", "Bernard", "Marie", "marie.bernard@univ.fr", "0699887766", "profinfo");

        // Création des Étudiants
        Etudiant etu1 = new Etudiant("ETU001", "Licence Info", "Durand", "Alice", "alice.durand@etu.fr", "0711223344", "etualice");
        etu1.seConnecter();
        Etudiant etu2 = new Etudiant("ETU002", "Licence Info", "Petit", "Tom", "tom.petit@etu.fr", "0755443322", "etutom");
        Etudiant etu3 = new Etudiant("ETU003", "Licence Info", "Moreau", "Lea", "lea.moreau@etu.fr", "0799887766", "etulea");

        // L'administrateur ajoute les utilisateurs aux listes globales
        Administrateur.ajouterEnseignantGlobal(profMath);
        Administrateur.ajouterEnseignantGlobal(profPhysique);
        Administrateur.ajouterEnseignantGlobal(profInfo);
        Administrateur.ajouterEtudiantGlobal(etu1);
        Administrateur.ajouterEtudiantGlobal(etu2);
        Administrateur.ajouterEtudiantGlobal(etu3);

        // Création des Salles
        Salle salleA = new Salle("A101", 30, "Bâtiment A");
        Salle salleB = new Salle("B202", 50, "Bâtiment B");
        Salle salleC = new Salle("C303", 20, "Bâtiment C");
        Salle salleD = new Salle("D404", 15, "Bâtiment D");

        // L'administrateur ajoute les salles aux listes globales
        Administrateur.ajouterSalle(salleA);
        Administrateur.ajouterSalle(salleB);
        Administrateur.ajouterSalle(salleC);
        Administrateur.ajouterSalle(salleD);

        // Création des Équipements
        Equipement projo = new Equipement("Vidéoprojecteur", "Projecteur Epson");
        Equipement tableauBlanc = new Equipement("Tableau Blanc Interactif", "Tableau numérique");
        Equipement ordi = new Equipement("Ordinateur Prof", "Ordinateur avec logiciels spécifiques");
        Equipement labo = new Equipement("Matériel Labo", "Équipements pour TP");

        // Ajout d'équipements aux salles
        salleA.ajouterEquipement(projo);
        salleA.ajouterEquipement(tableauBlanc);
        salleB.ajouterEquipement(projo);
        salleC.ajouterEquipement(ordi);
        salleD.ajouterEquipement(labo);
        salleA.ajouterEquipement(projo); // Tentative d'ajout dupliqué

        System.out.println(salleA); // Afficher les équipements de la salle A
        salleA.retirerEquipement(tableauBlanc);
        System.out.println(salleA); // Afficher après retrait

        // --- PARTIE 2: Gestion des Cours ---

        System.out.println("\n--- 2. L'administrateur crée des cours ---");
        String dateJ1 = "2025-06-05"; // Jour 1
        String dateJ2 = "2025-06-06"; // Jour 2
        String dateJ3 = "2025-06-07"; // Jour 3
        String dateJ4 = "2025-06-08"; // Jour 4

        admin.creerCours("Algorithmique", "2h", "09:00", "11:00", dateJ1, profInfo, salleA);
        admin.creerCours("Analyse I", "1h30m", "11:30", "13:00", dateJ1, profMath, salleB);
        admin.creerCours("Mécanique", "2h", "14:00", "16:00", dateJ1, profPhysique, salleC);

        // Correction: profBInfo remplacé par profInfo
        admin.creerCours("Structures de Données", "2h", "09:00", "11:00", dateJ2, profInfo, salleB);
        admin.creerCours("Probabilités", "1h30m", "11:30", "13:00", dateJ2, profMath, salleA);
        admin.creerCours("Electromagnétisme", "2h", "14:00", "16:00", dateJ2, profPhysique, salleD);


        System.out.println("\n--- 3. Inscription des étudiants aux cours ---");
        // Trouver un cours par son nom pour l'inscription (version simple)
        Cours coursAlgo = null;
        List<Cours> tousLesCours = Administrateur.getTousLesCours();
        for (int i = 0; i < tousLesCours.size(); i++) {
            Cours c = tousLesCours.get(i);
            if (c.getNom().equals("Algorithmique")) {
                coursAlgo = c;
                break;
            }
        }
        if (coursAlgo != null) {
            coursAlgo.ajouterEtudiant(etu1);
            coursAlgo.ajouterEtudiant(etu2);
            coursAlgo.ajouterEtudiant(etu1); // Tentative d'ajout dupliqué
        }

        Cours coursAnalyse = null;
        for (int i = 0; i < tousLesCours.size(); i++) {
            Cours c = tousLesCours.get(i);
            if (c.getNom().equals("Analyse I") && c.getDateCours().equals(dateJ1)) {
                coursAnalyse = c;
                break;
            }
        }
        if (coursAnalyse != null) {
            coursAnalyse.ajouterEtudiant(etu2);
            coursAnalyse.ajouterEtudiant(etu3);
        }

        Cours coursStructures = null;
        for (int i = 0; i < tousLesCours.size(); i++) {
            Cours c = tousLesCours.get(i);
            if (c.getNom().equals("Structures de Données") && c.getDateCours().equals(dateJ2)) {
                coursStructures = c;
                break;
            }
        }
        if (coursStructures != null) {
            coursStructures.ajouterEtudiant(etu1);
            coursStructures.ajouterEtudiant(etu3);
        }

        System.out.println("\n--- 4. Consultation des emplois du temps initiaux ---");
        admin.consulterEmploiDuTemps(); // Emploi du temps global

        profInfo.consulterEmploiDuTemps(); // Emploi du temps de l'enseignant Info
        etu1.consulterEmploiDuTemps();     // Emploi du temps de l'étudiant 1

        // Utilisation de la classe EmploiDuTemps pour différentes vues
        EmploiDuTemps edtJour1 = new EmploiDuTemps(dateJ1);
        edtJour1.afficherJour();

        EmploiDuTemps edtJour2 = new EmploiDuTemps(dateJ2);
        edtJour2.afficherJour();

        EmploiDuTemps edtJourInexistant = new EmploiDuTemps("2025-06-10");
        edtJourInexistant.afficherJour(); // Jour sans cours

        edtJour1.afficherSemaine(); // Affiche la semaine à partir de dateJ1

        edtJour1.afficherMois(); // Affiche le mois de dateJ1

        // --- PARTIE 3: Modifications et Scénarios Avancés ---

        System.out.println("\n--- 5. L'administrateur modifie un cours ---");
        Cours coursAModifier = null;
        for (int i = 0; i < tousLesCours.size(); i++) {
            Cours c = tousLesCours.get(i);
            if (c.getNom().equals("Analyse I") && c.getDateCours().equals(dateJ1)) {
                coursAModifier = c;
                break;
            }
        }

        if (coursAModifier != null) {
            System.out.println("Cours original: " + coursAModifier);
            // Déplace le cours Analyse I du J1 en salle B au J3 en salle D, avec de nouvelles heures
            admin.modifierCours(coursAModifier,
                    null, // Ne change pas le nom
                    null, // Ne change pas la durée
                    "10:00",          // Nouvelle heure de début
                    "12:00",          // Nouvelle heure de fin
                    dateJ3,           // Nouvelle date
                    null,             // Ne change pas l'enseignant
                    salleD);          // Nouvelle salle
            System.out.println("Cours modifié: " + coursAModifier);
        } else {
            System.out.println("Cours 'Analyse I' du " + dateJ1 + " non trouvé pour modification.");
        }

        System.out.println("\n--- Re-consultation de l'emploi du temps après modification ---");
        admin.consulterEmploiDuTemps();
        profMath.consulterEmploiDuTemps(); // Le cours "Analyse I" devrait être le 2025-06-07 pour profMath

        System.out.println("\n--- 6. Test d'affectation d'enseignant ---");
        Cours coursMeca = null;
        for (int i = 0; i < tousLesCours.size(); i++) {
            Cours c = tousLesCours.get(i);
            if (c.getNom().equals("Mécanique")) {
                coursMeca = c;
                break;
            }
        }
        if (coursMeca != null) {
            System.out.println("Enseignant actuel de Mécanique: " + coursMeca.getEnseignant().getNom());
            admin.affecterEnseignant(profInfo, coursMeca); // Affecte profInfo à Mécanique
            System.out.println("Nouvel enseignant de Mécanique: " + coursMeca.getEnseignant().getNom());
            profPhysique.consulterEmploiDuTemps(); // Vérifie que Mécanique n'est plus chez profPhysique
            profInfo.consulterEmploiDuTemps();     // Vérifie que Mécanique est chez profInfo
        }


        System.out.println("\n--- 7. Simulation de réservation/libération de salle par l'administrateur ---");
        admin.reserverSalle(salleA, dateJ4, "09:00", "10:00"); // Devrait réussir
        admin.reserverSalle(salleA, dateJ4, "09:30", "10:30"); // Devrait échouer (conflit avec 09:00-10:00)
        admin.reserverSalle(salleA, dateJ4, "10:00", "11:00"); // Devrait échouer (fin de 09:00-10:00 est début de celui-ci)
        admin.reserverSalle(salleA, dateJ4, "11:00", "12:00"); // Devrait réussir

        // Vérifier la disponibilité après réservations
        System.out.println("Disponibilité salle A le " + dateJ4 + " de 09:15 à 09:45: " + salleA.verifierDisponibilite(dateJ4, "09:15", "09:45"));
        System.out.println("Disponibilité salle A le " + dateJ4 + " de 10:00 à 10:30: " + salleA.verifierDisponibilite(dateJ4, "10:00", "10:30"));
        System.out.println("Disponibilité salle A le " + dateJ4 + " de 12:00 à 13:00: " + salleA.verifierDisponibilite(dateJ4, "12:00", "13:00"));

        salleA.liberer(dateJ4, "09:00", "10:00"); // Libérer le premier créneau
        System.out.println("Disponibilité salle A le " + dateJ4 + " de 09:15 à 09:45 après libération: " + salleA.verifierDisponibilite(dateJ4, "09:15", "09:45"));


        System.out.println("\n--- 8. Enseignant signale un conflit ---");
        // Créons un conflit pour profInfo le 2025-06-07
        admin.creerCours("Projet Java", "2h", "10:30", "12:30", dateJ3, profInfo, salleA);
        profInfo.signalerConflit(); // Prof Info devrait détecter le conflit

        System.out.println("\n--- L'administrateur consulte les signalisations ---");
        admin.consulterSignalisations();

        profInfo.signalerConflit(); // Nouvelle signalisation (devrait être vide car l'admin a consulté)

        // --- PARTIE 4: Recherche et Statistiques ---

        System.out.println("\n--- 9. Recherche de cours ---");
        EmploiDuTemps edtRecherche = new EmploiDuTemps(dateJ1); // Objet pour les recherches
        edtRecherche.rechercherCours("Algo");
        edtRecherche.rechercherCours("Physique");
        edtRecherche.rechercherCours("Bernard"); // Recherche par nom d'enseignant
        edtRecherche.rechercherCours("A101");    // Recherche par numéro de salle
        edtRecherche.rechercherCours("Chimie");  // Aucun résultat

        System.out.println("\n--- 10. Consultation des statistiques par l'administrateur ---");
        admin.consulterStatistiques();

        // --- PARTIE 5: Dégagement ---

        System.out.println("\n--- 11. Retrait d'un étudiant d'un cours ---");
        if (coursAlgo != null) {
            coursAlgo.retirerEtudiant(etu2);
            System.out.println("Étudiants inscrits à " + coursAlgo.getNom() + ":");
            for (int i = 0; i < coursAlgo.getEtudiantsInscrits().size(); i++) {
                Etudiant etu = coursAlgo.getEtudiantsInscrits().get(i);
                System.out.println("- " + etu.getNom() + " " + etu.getPrenom());
            }
        }

        System.out.println("\n--- 12. Suppression d'un cours par l'administrateur ---");
        Cours coursASupprimer = null;
        for (int i = 0; i < tousLesCours.size(); i++) {
            Cours c = tousLesCours.get(i);
            if (c.getNom().equals("Electromagnétisme")) {
                coursASupprimer = c;
                break;
            }
        }
        if (coursASupprimer != null) {
            Administrateur.supprimerCours(coursASupprimer); // Supprimer le cours
        }
        System.out.println("\nEmploi du temps après suppression de 'Electromagnétisme':");
        admin.consulterEmploiDuTemps();

        /* Test uniquement pou JavaFX
        // --- PARTIE 6: Test des Notifications ---
        System.out.println("\n--- 13. Test des Notifications ---");

        // Admin envoie une notification à Prof Math
        Notification notif1 = new Notification("Rappel: Réunion générale demain.", "2025-05-19", profMath);
        notif1.envoyer();

        // Prof Info envoie une notification à Etu1
        Notification notif2 = new Notification("Le cours de Bases de Données est annulé.","2025-05-23", etu1);
        notif2.envoyer(); */


        System.out.println("\n--- Fin des tests ---");
        admin.seDeconnecter();
        profMath.seDeconnecter();
        etu1.seDeconnecter();
    }
}