package com.beginsecure.projetjavav6;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
// Les importations de sécurité (MessageDigest, NoSuchAlgorithmException, StandardCharsets, HexFormat) sont supprimées
import java.time.LocalDate;
import java.util.List;
// L'importation de URL est supprimée car elle n'est plus utilisée directement

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorMessageLabel;

    private static final String COMPTES_CSV_FILE = "comptes.csv"; // Nom du fichier CSV

    // Bloc statique pour l'initialisation des données
    // Ce bloc s'exécute une seule fois lorsque la classe LoginController est chargée en mémoire.
    static {
        System.out.println("--- Initialisation des Données Globales ---");

        // Création de l'Administrateur principal
        // Le mot de passe est en clair, comme demandé.
        Administrateur admin = new Administrateur("ADM001", "Dupont", "Jean", "jean.dupont@univ.fr", "0123456789", "adminpass");
        // L'administrateur est automatiquement ajouté à la liste statique dans son constructeur.

        // Création des Enseignants
        // Les mots de passe sont en clair, comme demandé.
        Enseignant profMath = new Enseignant("ENS001", "Mathématiques", "Martin", "Sophie", "sophie.martin@univ.fr", "0611223344", "profmath");
        Enseignant profPhysique = new Enseignant("ENS002", "Physique", "Lefevre", "Paul", "paul.lefevre@univ.fr", "0655443322", "profphysique");
        Enseignant profInfo = new Enseignant("ENS003", "Informatique", "Bernard", "Marie", "marie.bernard@univ.fr", "0699887766", "profinfo");

        // L'administrateur ajoute les enseignants aux listes globales
        Administrateur.ajouterEnseignantGlobal(profMath);
        Administrateur.ajouterEnseignantGlobal(profPhysique);
        Administrateur.ajouterEnseignantGlobal(profInfo);

        // Création des Étudiants
        // Les mots de passe sont en clair, comme demandé.
        Etudiant etu1 = new Etudiant("ETU001", "Licence Informatique", "Durand", "Alice", "alice.durand@etu.fr", "0711223344", "etualice");
        Etudiant etu2 = new Etudiant("ETU002", "Licence Informatique", "Petit", "Tom", "tom.petit@etu.fr", "0755443322", "etutom");
        Etudiant etu3 = new Etudiant("ETU003", "Licence Informatique", "Moreau", "Lea", "lea.moreau@etu.fr", "0799887766", "etulea");
        //Etu4 : ETU004, test12
        // L'administrateur ajoute les étudiants aux listes globales
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

        // Gestion des Cours
        String dateJ1 = "2025-06-05"; // Jour 1
        String dateJ2 = "2025-06-06"; // Jour 2
        String dateJ3 = "2025-06-07"; // Jour 3
        String dateJ4 = "2025-06-08"; // Jour 4

        admin.creerCours("Algorithmique", "2h", "09:00", "11:00", dateJ1, profInfo, salleA);
        admin.creerCours("Analyse I", "1h30m", "11:30", "13:00", dateJ1, profMath, salleB);
        admin.creerCours("Mécanique", "2h", "14:00", "16:00", dateJ1, profPhysique, salleC);

        admin.creerCours("Structures de Données", "2h", "09:00", "11:00", dateJ2, profInfo, salleB);
        admin.creerCours("Probabilités", "1h30m", "11:30", "13:00", dateJ2, profMath, salleA);
        admin.creerCours("Electromagnétisme", "2h", "14:00", "16:00", dateJ2, profPhysique, salleD);

        List<Cours> tousLesCoursInitial = Administrateur.getTousLesCours();

        Cours coursAlgo = findCoursHelper("Algorithmique", dateJ1, tousLesCoursInitial);
        if (coursAlgo != null) {
            coursAlgo.ajouterEtudiant(etu1);
            coursAlgo.ajouterEtudiant(etu2);
        }

        Cours coursAnalyse = findCoursHelper("Analyse I", dateJ1, tousLesCoursInitial);
        if (coursAnalyse != null) {
            coursAnalyse.ajouterEtudiant(etu2);
            coursAnalyse.ajouterEtudiant(etu3);
        }

        Cours coursStructures = findCoursHelper("Structures de Données", dateJ2, tousLesCoursInitial);
        if (coursStructures != null) {
            coursStructures.ajouterEtudiant(etu1);
            coursStructures.ajouterEtudiant(etu3);
        }

        admin.creerCours("Projet Java", "2h", "10:30", "12:30", dateJ3, profInfo, salleA);
        profInfo.signalerConflit(); // Prof Info devrait détecter le conflit

        System.out.println("\n--- L'administrateur consulte les signalisations ---");
        admin.consulterSignalisations();

        // Exemple 1: Admin envoie une notification à Prof Math
        Notification notif1 = new Notification("Rappel: Réunion générale demain à 10h en salle B202.", LocalDate.now().toString(), profMath);
        notif1.envoyer(); // Ceci envoie la notification

        // Exemple 2: Prof Info envoie une notification à Etu1
        Notification notif2 = new Notification("Le cours de Bases de Données est annulé pour cause de grève.", "2025-05-23", etu1);
        notif2.envoyer(); // Ceci envoie la notification

        // Exemple 3: Le système envoie une notification à l'administrateur (simulé ici)
        Notification notif3 = new Notification("Mise à jour système planifiée ce week-end. Impact possible sur l'accès.", LocalDate.now().toString(), admin);
        notif3.envoyer(); // Ceci envoie la notification

        // Exemple 4: Notification pour un autre étudiant
        Notification notif4 = new Notification("Votre inscription au cours de Probabilités a été confirmée.", LocalDate.now().toString(), etu2);
        notif4.envoyer();

        System.out.println("--- Fin de l'initialisation des données ---");

        // Sauvegarde des utilisateurs initiaux dans le CSV après leur création en mémoire
        saveUsersToCsv();
    }

    // Méthode utilitaire pour trouver un cours par son nom et sa date dans une liste donnée.
    // Cette méthode est nécessaire pour l'initialisation des données.
    private static Cours findCoursHelper(String nomCours, String dateCours, List<Cours> tousLesCours) {
        for (Cours c : tousLesCours) {
            if (c.getNom().equals(nomCours) && c.getDateCours().equals(dateCours)) {
                return c;
            }
        }
        return null;
    }

    // La méthode hashPassword() est supprimée car le hachage n'est plus souhaité.

    /**
     * Charge les utilisateurs (Administrateurs, Enseignants, Étudiants) depuis le fichier CSV.
     */
    public static void loadUsersFromCsv() {
        System.out.println("Tentative de chargement des utilisateurs depuis " + COMPTES_CSV_FILE);
        // Efface les listes globales pour éviter les doublons si la méthode est appelée plusieurs fois
        Administrateur.getAdministrateursGlobaux().clear();
        Administrateur.getEnseignantsGlobaux().clear();
        Administrateur.getEtudiantsGlobaux().clear();

        try (BufferedReader br = new BufferedReader(new FileReader(COMPTES_CSV_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 7) { // ID, Nom, Prénom, Email, Téléphone, MotDePasse, Rôle
                    String id = parts[0];
                    String nom = parts[1];
                    String prenom = parts[2];
                    String email = parts[3];
                    String telephone = parts[4];
                    String password = parts[5]; // Lire le mot de passe en clair
                    String role = parts[6];

                    switch (role) {
                        case "Administrateur":
                            // Note: Le constructeur de Administrateur ajoute déjà à la liste globale.
                            // Il faut s'assurer de ne pas créer de doublons si l'ID existe déjà.
                            // Ici, on ne fait que créer l'objet, son constructeur gérera l'ajout.
                            new Administrateur(id, nom, prenom, email, telephone, password);
                            break;
                        case "Enseignant":
                            String specialite = "Non spécifiée"; // Ou à lire depuis le CSV si le format est étendu
                            Administrateur.ajouterEnseignantGlobal(new Enseignant(id, specialite, nom, prenom, email, telephone, password));
                            break;
                        case "Etudiant":
                            String formation = "Non spécifiée"; // Ou à lire depuis le CSV si le format est étendu
                            Administrateur.ajouterEtudiantGlobal(new Etudiant(id, formation, nom, prenom, email, telephone, password));
                            break;
                        default:
                            System.err.println("Rôle inconnu dans le CSV: " + role);
                    }
                } else {
                    System.err.println("Ligne CSV mal formatée: " + line);
                }
            }
            System.out.println("Utilisateurs chargés avec succès depuis " + COMPTES_CSV_FILE);
        } catch (IOException e) {
            System.err.println("Impossible de lire le fichier CSV des comptes. Création d'un fichier vide si inexistant: " + e.getMessage());
            // Si le fichier n'existe pas, il sera créé lors de la première sauvegarde.
        }
    }

    /**
     * Sauvegarde tous les utilisateurs (Administrateurs, Enseignants, Étudiants) dans le fichier CSV.
     */
    public static void saveUsersToCsv() {
        System.out.println("Tentative de sauvegarde des utilisateurs vers " + COMPTES_CSV_FILE);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(COMPTES_CSV_FILE))) {
            // Écrire les administrateurs
            for (Administrateur admin : Administrateur.getAdministrateursGlobaux()) {
                // Format: ID, Nom, Prénom, Email, Téléphone, MotDePasse, Rôle
                bw.write(admin.getNumAdmin() + "," + admin.getNom() + "," + admin.getPrenom() + "," +
                        admin.getEmail() + "," + admin.getTelephone() + "," + admin.getMotDePasse() + "," +
                        admin.getRole() + "\n");
            }
            // Écrire les enseignants
            for (Enseignant enseignant : Administrateur.getEnseignantsGlobaux()) {
                // Note: La spécialité n'est pas sauvegardée ici. Si elle doit l'être, le format CSV doit être étendu.
                bw.write(enseignant.getNumEnseignant() + "," + enseignant.getNom() + "," + enseignant.getPrenom() + "," +
                        enseignant.getEmail() + "," + enseignant.getTelephone() + "," + enseignant.getMotDePasse() + "," +
                        enseignant.getRole() + "\n");
            }
            // Écrire les étudiants
            for (Etudiant etudiant : Administrateur.getEtudiantsGlobaux()) {
                // Note: La formation n'est pas sauvegardée ici. Si elle doit l'être, le format CSV doit être étendu.
                bw.write(etudiant.getNumEtudiant() + "," + etudiant.getNom() + "," + etudiant.getPrenom() + "," +
                        etudiant.getEmail() + "," + etudiant.getTelephone() + "," + etudiant.getMotDePasse() + "," +
                        etudiant.getRole() + "\n");
            }
            System.out.println("Utilisateurs sauvegardés avec succès dans " + COMPTES_CSV_FILE);
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde des utilisateurs dans le fichier CSV: " + e.getMessage());
            CustomAlertDialog.showError("Erreur de sauvegarde", "Impossible de sauvegarder les comptes utilisateurs.");
        }
    }

    @FXML
    private void handleLoginButtonAction(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText(); // Mot de passe en clair entré par l'utilisateur

        if (username.isEmpty() || password.isEmpty()) {
            errorMessageLabel.setText("Veuillez entrer votre identifiant et votre mot de passe.");
            return;
        }

        // Appel à authenticateUser avec le mot de passe en clair
        Utilisateur authenticatedUser = authenticateUser(username, password);

        if (authenticatedUser != null) {
            errorMessageLabel.setText(""); // Efface les messages d'erreur
            System.out.println("Connexion réussie pour " + authenticatedUser.getNom() + " " + authenticatedUser.getPrenom() + " (" + authenticatedUser.getRole() + ")");

            try {
                String fxmlPath = "";
                String title = "";

                if (authenticatedUser.getRole().equals("Administrateur")) {
                    fxmlPath = "/com/beginsecure/projetjavav6/AdminDashboardView.fxml";
                    title = "Tableau de Bord Administrateur";
                } else if (authenticatedUser.getRole().equals("Enseignant")) {
                    fxmlPath = "/com/beginsecure/projetjavav6/EnseignantDashboardView.fxml";
                    title = "Tableau de Bord Enseignant";
                } else if (authenticatedUser.getRole().equals("Etudiant")) {
                    fxmlPath = "/com/beginsecure/projetjavav6/EtudiantDashboardView.fxml";
                    title = "Tableau de Bord Étudiant";
                } else {
                    CustomAlertDialog.showError("Erreur de rôle", "Rôle d'utilisateur inconnu après authentification.");
                    return;
                }

                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Parent root = loader.load();

                // Passer l'utilisateur authentifié au contrôleur de la prochaine scène
                if (authenticatedUser.getRole().equals("Administrateur")) {
                    AdminDashboardController controller = loader.getController();
                    controller.setLoggedInUser((Administrateur) authenticatedUser);
                } else if (authenticatedUser.getRole().equals("Enseignant")) {
                    EnseignantDashboardController controller = loader.getController();
                    controller.setLoggedInUser((Enseignant) authenticatedUser);
                } else if (authenticatedUser.getRole().equals("Etudiant")) {
                    EtudiantDashboardController controller = loader.getController();
                    controller.setLoggedInUser((Etudiant) authenticatedUser);
                }

                Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                stage.setTitle(title);
                stage.setScene(new Scene(root));
                stage.setMaximized(true); // Maximiser la fenêtre
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
                CustomAlertDialog.showError("Erreur de chargement", "Erreur de chargement de l'interface utilisateur: " + e.getMessage());
            }
        } else {
            errorMessageLabel.setText("Identifiant ou mot de passe incorrect.");
            CustomAlertDialog.showError("Échec de l'authentification", "Identifiant ou mot de passe incorrect.");
        }
    }

    /**
     * Authentifie un utilisateur en vérifiant les listes globales d'administrateurs, enseignants et étudiants.
     * @param username L'identifiant (numAdmin, numEnseignant, numEtudiant).
     * @param password Le mot de passe en clair.
     * @return L'objet Utilisateur authentifié, ou null si l'authentification échoue.
     */
    private Utilisateur authenticateUser(String username, String password) {
        // Tenter d'authentifier en tant qu'Administrateur
        for (Administrateur admin : Administrateur.getAdministrateursGlobaux()) {
            if (admin.getNumAdmin().equals(username) && admin.getMotDePasse().equals(password)) {
                return admin;
            }
        }

        // Tenter d'authentifier en tant qu'Enseignant
        for (Enseignant enseignant : Administrateur.getEnseignantsGlobaux()) {
            if (enseignant.getNumEnseignant().equals(username) && enseignant.getMotDePasse().equals(password)) {
                return enseignant;
            }
        }

        // Tenter d'authentifier en tant qu'Étudiant
        for (Etudiant etudiant : Administrateur.getEtudiantsGlobaux()) {
            if (etudiant.getNumEtudiant().equals(username) && etudiant.getMotDePasse().equals(password)) {
                return etudiant;
            }
        }
        return null; // Authentification échouée
    }

    @FXML
    private void handleRegisterButtonAction(ActionEvent event) {
        try {
            // Charger la vue d'inscription
            FXMLLoader loader = new FXMLLoader(getClass().getResource("RegisterView.fxml"));
            Parent root = loader.load();

            Stage registerStage = new Stage();
            registerStage.setTitle("Inscription");
            registerStage.setScene(new Scene(root, 600, 400)); // Taille de la fenêtre d'inscription
            registerStage.initOwner(((Node)event.getSource()).getScene().getWindow()); // Définir la fenêtre parente
            registerStage.initModality(javafx.stage.Modality.APPLICATION_MODAL); // Rendre la fenêtre modale
            registerStage.showAndWait(); // Afficher et attendre que la fenêtre se ferme
            // Après la fermeture de la fenêtre d'inscription, recharger les utilisateurs
            // pour inclure le nouvel inscrit si l'inscription a été réussie.
            loadUsersFromCsv(); // Recharge les utilisateurs après une potentielle nouvelle inscription
        } catch (IOException e) {
            e.printStackTrace();
            CustomAlertDialog.showError("Erreur de chargement", "Erreur lors du chargement de la page d'inscription: " + e.getMessage());
        }
    }
}
