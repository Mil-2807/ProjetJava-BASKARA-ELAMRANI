package com.beginsecure.projetjavav6;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.stage.Modality;

import java.io.File;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.function.Consumer;

public class AdminDashboardController {

    @FXML
    private Label welcomeLabel;

    // Ces champs sont maintenant spécifiques à l'onglet "Consulter Emploi du Temps"
    @FXML
    private Label edtResultLabel;
    @FXML
    private VBox edtDisplayArea;

    // NOUVEAUX CHAMPS POUR L'ONGLET "Statistiques & Signalisations"
    @FXML
    private Label statsSignalsResultLabel;
    @FXML
    private VBox statsSignalsDisplayArea;

    @FXML
    private Label courseResultLabel;
    @FXML
    private VBox courseDisplayArea;

    @FXML
    private Label salleEquipementResultLabel;
    @FXML
    private VBox salleEquipementDisplayArea;

    @FXML
    private Label userResultLabel;
    @FXML
    private VBox userDisplayArea;

    // CHAMPS POUR LA RECHERCHE DE COURS
    @FXML
    private TextField CoursReferenceField;
    @FXML
    private Label RechercheResultLabel;
    // NOUVEAU: Champs pour l'onglet Notifications
    @FXML
    private Label notificationResultLabel;
    @FXML
    private VBox notificationDisplayArea;



    private Administrateur loggedInAdmin;

    /**
     * Définit l'utilisateur Administrateur actuellement connecté.
     * @param admin L'instance de l'Administrateur connecté.
     */
    public void setLoggedInUser(Administrateur admin) {
        this.loggedInAdmin = admin;
        welcomeLabel.setText("Bienvenue, " + admin.getPrenom() + " " + admin.getNom() + " !");
    }

    /**
     * Gère l'action de déconnexion de l'administrateur.
     * @param event L'événement d'action.
     */
    @FXML
    private void handleLogout(ActionEvent event) {
        if (loggedInAdmin != null) {
            loggedInAdmin.seDeconnecter();
            System.out.println("L'administrateur s'est déconnecté.");
        }

        try {
            // Chemin FXML corrigé pour être absolu
            Parent loginRoot = FXMLLoader.load(getClass().getResource("LoginView.fxml"));
            Scene loginScene = new Scene(loginRoot);
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(loginScene);
            stage.setTitle("Connexion au Système");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            CustomAlertDialog.showError("Erreur de déconnexion", "Erreur lors du chargement de l'écran de connexion.");
        }
    }

    /**
     * Efface le contenu d'une zone d'affichage et met à jour son label de résultat.
     * @param resultLabel Le label de résultat.
     * @param displayArea La VBox d'affichage.
     * @param message Le message à afficher dans le label.
     */
    private void clearAndSetMessage(Label resultLabel, VBox displayArea, String message) {
        if (displayArea != null) {
            displayArea.getChildren().clear();
        }
        if (resultLabel != null) {
            resultLabel.setText(message);
        }
    }

    /**
     * Affiche les statistiques du système.
     */
    @FXML
    private void showStatistics() {
        if (loggedInAdmin == null) {
            CustomAlertDialog.showError("Erreur", "Administrateur non connecté.");
            return;
        }

        try {
            clearAndSetMessage(statsSignalsResultLabel, statsSignalsDisplayArea, "Affichage des statistiques...");

            // Capture la sortie console de consulterStatistiques()
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            PrintStream old = System.out;
            System.setOut(ps);

            loggedInAdmin.consulterStatistiques();

            System.out.flush();
            System.setOut(old); // Restaurer la sortie standard

            String statistics = baos.toString().trim();

            // Affiche le contenu capturé dans statsSignalsDisplayArea
            displayResultsInArea(statsSignalsResultLabel, statsSignalsDisplayArea, statistics, "Statistiques");
            statsSignalsResultLabel.setText("Statistiques affichées."); // Met à jour le label principal
        } catch (Exception e) {
            e.printStackTrace();
            CustomAlertDialog.showError("Erreur d'affichage", "Une erreur inattendue est survenue lors de l'affichage des statistiques : " + e.getMessage());
        }
    }

    /**
     * Affiche les signalisations de conflits du système.
     */
    @FXML
    private void showSignalisations() {
        if (loggedInAdmin == null) {
            CustomAlertDialog.showError("Erreur", "Administrateur non connecté.");
            return;
        }

        try {
            clearAndSetMessage(statsSignalsResultLabel, statsSignalsDisplayArea, "Affichage des signalisations...");

            // Capture la sortie console de consulterSignalisations()
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            PrintStream old = System.out;
            System.setOut(ps);

            loggedInAdmin.consulterSignalisations();

            System.out.flush();
            System.setOut(old); // Restaurer la sortie standard

            String signalisations = baos.toString().trim();

            // Affiche le contenu capturé dans statsSignalsDisplayArea
            displayResultsInArea(statsSignalsResultLabel, statsSignalsDisplayArea, signalisations, "Signalisations");
            statsSignalsResultLabel.setText("Signalisations affichées."); // Met à jour le label principal
        } catch (Exception e) {
            e.printStackTrace();
            CustomAlertDialog.showError("Erreur d'affichage", "Une erreur inattendue est survenue lors de l'affichage des signalisations : " + e.getMessage());
        }
    }

    /**
     * Affiche tous les cours enregistrés dans le système.
     */
    @FXML
    private void showAllCourses() {
        clearAndSetMessage(courseResultLabel, courseDisplayArea, "Affichage de tous les cours...");
        List<Cours> allCourses = Administrateur.getTousLesCours();
        if (allCourses.isEmpty()) {
            courseDisplayArea.getChildren().add(new Label("Aucun cours enregistré."));
        } else {
            courseDisplayArea.getChildren().clear();
            for (Cours cours : allCourses) {
                courseDisplayArea.getChildren().add(new Label(cours.toString()));
            }
        }
        courseResultLabel.setText("Tous les cours affichés.");
    }

    @FXML
    private void modifyCourse() {
        // Demander à l'utilisateur de sélectionner un cours à modifier
        // Pour cet exemple, nous allons afficher une boîte de dialogue simple
        // Idéalement, vous auriez une TableView des cours et l'utilisateur sélectionnerait une ligne.
        List<Cours> allCourses = Administrateur.getTousLesCours();
        if (allCourses.isEmpty()) {
            CustomAlertDialog.showInformation("Modification de Cours", "Aucun cours n'est enregistré pour être modifié.");
            return;
        }

        // Simuler la sélection d'un cours (pour un vrai cas, utiliser une TableView ou ComboBox)
        // Pour la démonstration, nous allons simplement prendre le premier cours disponible
        // ou demander à l'utilisateur de saisir l'ID/Nom du cours.
        // Ici, nous allons ouvrir un formulaire avec une ComboBox pour choisir le cours.
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ModifyCourseForm.fxml"));
            Parent root = loader.load();
            ModifyCourseController controller = loader.getController();
            controller.setAdminInstance(loggedInAdmin); // Passe l'instance admin
            controller.loadCoursesIntoComboBox(allCourses); // Charge les cours dans la ComboBox du formulaire

            Stage stage = new Stage();
            stage.setTitle("Modifier un Cours");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(((Node)courseDisplayArea).getScene().getWindow());
            stage.showAndWait();
            showAllCourses(); // Actualiser la liste des cours après la modification
        } catch (IOException e) {
            e.printStackTrace();
            CustomAlertDialog.showError("Erreur de chargement", "Impossible de charger le formulaire de modification de cours.");
        }
    }

    @FXML
    private void handleExportCoursesToCsv(ActionEvent event) {
        // Récupérer tous les cours disponibles
        List<Cours> allCourses = Administrateur.getTousLesCours();

        if (allCourses.isEmpty()) {
            CustomAlertDialog.showInformation("Exportation CSV", "Aucun cours à exporter.");
            return;
        }

        // Ouvrir une boîte de dialogue de sauvegarde de fichier
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter les cours en CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers CSV", "*.csv"));
        fileChooser.setInitialFileName("emploidutemps_complet.csv"); // Nom de fichier par défaut

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            // Appeler la méthode d'exportation de CsvExporter
            boolean success = CsvExporter.exportCoursesToCsv(allCourses, file.getAbsolutePath());
            if (success) {
                CustomAlertDialog.showInformation("Exportation CSV", "Les cours ont été exportés avec succès vers :\n" + file.getAbsolutePath());
            } else {
                CustomAlertDialog.showError("Erreur d'exportation CSV", "Échec de l'exportation des cours vers le fichier CSV.");
            }
        } else {
            CustomAlertDialog.showInformation("Exportation CSV", "Exportation annulée.");
        }
    }

    @FXML
    private void handleSearchAvailability(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AvailabilityFinderForm.fxml"));
            Parent root = loader.load();
            // AvailabilityFinderController controller = loader.getController();
            // Si le contrôleur a besoin de l'instance admin, passez-la ici:
            // controller.setAdminInstance(loggedInAdmin);

            Stage stage = new Stage();
            stage.setTitle("Rechercher des Créneaux Disponibles");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(((Node)salleEquipementDisplayArea).getScene().getWindow()); // Définit la fenêtre parente
            stage.showAndWait(); // Affiche et attend que le formulaire soit fermé
        } catch (IOException e) {
            e.printStackTrace();
            CustomAlertDialog.showError("Erreur de chargement", "Impossible de charger le formulaire de recherche de disponibilités.");
        }
    }



    /**
     * Ouvre un formulaire pour créer un nouveau cours.
     */
    @FXML
    private void createCourse() {
        // Ajout d'une vérification pour s'assurer que l'administrateur est connecté
        if (loggedInAdmin == null) {
            CustomAlertDialog.showError("Erreur", "Administrateur non connecté. Veuillez vous connecter pour créer un cours.");
            return;
        }

        try {
            // Chemin FXML corrigé pour être absolu
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CreateCourseForm.fxml"));
            Parent root = loader.load();

            // Assurez-vous que CreateCourseController existe et a une méthode setAdminInstance
            // Cette ligne a été décommentée pour corriger l'erreur "Instance Administrateur non définie."
            CreateCourseController controller = loader.getController();
            controller.setAdminInstance(loggedInAdmin); // Passe l'instance de l'admin au contrôleur du formulaire

            Stage stage = new Stage();
            stage.setTitle("Créer un Nouveau Cours");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Rend la fenêtre modale
            // Vérifiez si courseDisplayArea n'est pas null avant d'appeler getScene().getWindow()
            if (courseDisplayArea != null) {
                stage.initOwner(((Node)courseDisplayArea).getScene().getWindow()); // Définit la fenêtre parente
            }
            stage.showAndWait(); // Affiche et attend que le formulaire soit fermé
            showAllCourses(); // Rafraîchit la liste des cours après la fermeture du formulaire
        } catch (IOException e) {
            e.printStackTrace();
            CustomAlertDialog.showError("Erreur de chargement", "Impossible de charger le formulaire de création de cours.");
        }
    }

    /**
     * Ouvre un formulaire pour inscrire un étudiant à un cours.
     * NOUVEAU: Méthode pour gérer l'action du bouton "Inscrire un étudiant à un cours"
     * @param event L'événement d'action.
     */
    @FXML
    private void handleEnrollStudentToCourse(ActionEvent event) {
        if (loggedInAdmin == null) {
            CustomAlertDialog.showError("Erreur", "Administrateur non connecté. Veuillez vous connecter pour inscrire un étudiant.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("EnrollStudentToCourseForm.fxml"));
            Parent root = loader.load();

            EnrollStudentToCourseController controller = loader.getController();
            controller.setAdminInstance(loggedInAdmin); // Passe l'instance de l'administrateur

            Stage stage = new Stage();
            stage.setTitle("Inscrire un Étudiant à un Cours");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            if (courseDisplayArea != null) {
                stage.initOwner(((Node)courseDisplayArea).getScene().getWindow());
            }
            stage.showAndWait(); // Affiche et attend que le formulaire soit fermé
            showAllCourses(); // Rafraîchit la liste des cours après l'inscription
        } catch (IOException e) {
            e.printStackTrace();
            CustomAlertDialog.showError("Erreur de chargement", "Impossible de charger le formulaire d'inscription d'étudiant.");
        }
    }


    /**
     * Affiche toutes les salles enregistrées.
     */
    @FXML
    private void showAllSalles() {
        clearAndSetMessage(salleEquipementResultLabel, salleEquipementDisplayArea, "Affichage de toutes les salles...");
        List<Salle> allSalles = Administrateur.getToutesLesSalles();
        if (allSalles.isEmpty()) {
            salleEquipementDisplayArea.getChildren().add(new Label("Aucune salle enregistrée."));
        } else {
            salleEquipementDisplayArea.getChildren().clear();
            for (Salle salle : allSalles) {
                salleEquipementDisplayArea.getChildren().add(new Label(salle.toString()));
            }
        }
        salleEquipementResultLabel.setText("Toutes les salles affichées.");
    }

    /**
     * Ouvre un formulaire pour créer une nouvelle salle.
     */
    @FXML
    private void createSalle() {
        try {
            // Chemin FXML corrigé pour être absolu
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CreateSalleForm.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Créer une Nouvelle Salle");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            if (salleEquipementDisplayArea != null) {
                stage.initOwner(((Node)salleEquipementDisplayArea).getScene().getWindow());
            }
            stage.showAndWait();
            showAllSalles(); // Rafraîchit la liste des salles après la fermeture du formulaire
        } catch (IOException e) {
            e.printStackTrace();
            CustomAlertDialog.showError("Erreur de chargement", "Impossible de charger le formulaire de création de salle.");
        }
    }

    /**
     * Ouvre un formulaire pour ajouter un équipement à une salle existante.
     */
    @FXML
    private void addEquipementToSalle() {
        try {
            // Chemin FXML corrigé pour être absolu
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddEquipementToSalleForm.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ajouter un Équipement à une Salle");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            if (salleEquipementDisplayArea != null) {
                stage.initOwner(((Node)salleEquipementDisplayArea).getScene().getWindow());
            }
            stage.showAndWait();
            showAllSalles(); // Rafraîchit la liste des salles (pour voir les équipements mis à jour si toString() les affiche)
        } catch (IOException e) {
            e.printStackTrace();
            CustomAlertDialog.showError("Erreur de chargement", "Impossible de charger le formulaire d'ajout d'équipement.");
        }
    }

    /**
     * Affiche tous les enseignants enregistrés.
     */
    @FXML
    private void showAllEnseignants() {
        clearAndSetMessage(userResultLabel, userDisplayArea, "Affichage de tous les enseignants...");
        List<Enseignant> allEnseignants = Administrateur.getEnseignantsGlobaux();
        if (allEnseignants.isEmpty()) {
            userDisplayArea.getChildren().add(new Label("Aucun enseignant enregistré."));
        } else {
            userDisplayArea.getChildren().clear();
            for (Enseignant enseignant : allEnseignants) {
                userDisplayArea.getChildren().add(new Label(enseignant.toString()));
            }
        }
        userResultLabel.setText("Tous les enseignants affichés.");
    }

    /**
     * Ouvre un formulaire pour créer un nouvel enseignant.
     */
    @FXML
    private void createEnseignant() {
        try {
            // Chemin FXML corrigé pour être absolu
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CreateEnseignantForm.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Créer un Nouvel Enseignant");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            if (userDisplayArea != null) {
                stage.initOwner(((Node)userDisplayArea).getScene().getWindow());
            }
            stage.showAndWait();
            showAllEnseignants();
        } catch (IOException e) {
            e.printStackTrace();
            CustomAlertDialog.showError("Erreur de chargement", "Impossible de charger le formulaire de création d'enseignant.");
        }
    }

    /**
     * Affiche tous les étudiants enregistrés.
     */
    @FXML
    private void showAllEtudiants() {
        clearAndSetMessage(userResultLabel, userDisplayArea, "Affichage de tous les étudiants...");
        List<Etudiant> allEtudiants = Administrateur.getEtudiantsGlobaux();
        if (allEtudiants.isEmpty()) {
            userDisplayArea.getChildren().add(new Label("Aucun étudiant enregistré."));
        } else {
            userDisplayArea.getChildren().clear();
            for (Etudiant etudiant : allEtudiants) {
                userDisplayArea.getChildren().add(new Label(etudiant.toString()));
            }
        }
        userResultLabel.setText("Tous les étudiants affichés.");
    }

    /**
     * Ouvre un formulaire pour créer un nouvel étudiant.
     */
    @FXML
    private void createEtudiant() {
        try {
            // Chemin FXML corrigé pour être absolu
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CreateEtudiantForm.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Créer un Nouvel Étudiant");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            if (userDisplayArea != null) {
                stage.initOwner(((Node)userDisplayArea).getScene().getWindow());
            }
            stage.showAndWait();
            showAllEtudiants();
        } catch (IOException e) {
            e.printStackTrace();
            CustomAlertDialog.showError("Erreur de chargement", "Impossible de charger le formulaire de création d'étudiant.");
        }
    }

    /**
     * Affiche l'emploi du temps global en capturant la sortie console de la méthode de l'administrateur.
     */
    @FXML
    private void afficherEdtGlobal() {
        if (loggedInAdmin == null) {
            CustomAlertDialog.showError("Erreur", "Administrateur non connecté.");
            return;
        }

        try {
            clearAndSetMessage(edtResultLabel, edtDisplayArea, "Affichage de l'emploi du temps global...");

            // Capture la sortie console de consulterEmploiDuTemps()
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            PrintStream old = System.out;
            System.setOut(ps);

            loggedInAdmin.consulterEmploiDuTemps(); // Appel de la méthode de l'administrateur

            System.out.flush();
            System.setOut(old); // Restaurer la sortie standard

            String globalEdt = baos.toString().trim();

            // Affiche le contenu capturé dans edtDisplayArea
            displayResultsInArea(edtResultLabel, edtDisplayArea, globalEdt, "Emploi du temps global");
            edtResultLabel.setText("Emploi du temps global affiché.");
        } catch (Exception e) {
            e.printStackTrace();
            CustomAlertDialog.showError("Erreur d'affichage", "Une erreur inattendue est survenue lors de l'affichage de l'emploi du temps global : " + e.getMessage());
        }
    }

    /**
     * Méthode générique pour afficher des résultats dans une VBox et mettre à jour un Label de résultat.
     * @param resultLabel Le Label à mettre à jour avec le message de résultat.
     * @param displayArea La VBox où afficher le contenu.
     * @param content Le contenu à afficher (chaîne de caractères, souvent issue de la capture de System.out).
     * @param typeAffichage Le type d'affichage (ex: "Statistiques", "Signalisations", "recherche").
     */
    private void displayResultsInArea(Label resultLabel, VBox displayArea, String content, String typeAffichage) {
        if (displayArea != null && resultLabel != null) {
            displayArea.getChildren().clear();
            resultLabel.setText(typeAffichage + " :");

            // Les conditions de contenu vide doivent être adaptées à chaque type d'affichage
            if (content == null || content.trim().isEmpty() ||
                    (typeAffichage.equals("Statistiques") && (content.contains("Aucun cours planifié dans le système.") || content.contains("Aucun enseignant") && content.contains("Aucun étudiant"))) ||
                    (typeAffichage.equals("Signalisations") && content.contains("Aucune nouvelle signalisation.")) ||
                    (typeAffichage.equals("Emploi du temps global") && content.contains("Aucun cours planifié dans le système.")) ||
                    (typeAffichage.equals("recherche") && content.contains("Aucun cours trouvé correspondant à ce critère."))) {

                displayArea.getChildren().add(new Label("Aucun résultat trouvé pour cette période/critère."));
            } else {
                String[] lines = content.split(System.lineSeparator());
                for (String line : lines) {
                    displayArea.getChildren().add(new Label(line));
                }
            }
        } else {
            System.err.println("ERREUR: La zone d'affichage ou le label de résultat est nul. Impossible d'afficher les résultats.");
            CustomAlertDialog.showError("Erreur d'affichage", "La zone d'affichage des résultats n'est pas disponible.");
        }
    }

    /**
     * Recherche des cours en fonction d'un critère donné et affiche les résultats.
     */
    @FXML
    private void rechercherCours() {
        // Utilise le nouveau champ CoursReferenceField pour le critère de recherche
        String critere = CoursReferenceField.getText();
        if (critere == null || critere.isEmpty()) {
            // Utilise le nouveau label RechercheResultLabel pour le message d'erreur
            RechercheResultLabel.setText("Veuillez entrer un critère de recherche.");
            return;
        }

        EmploiDuTemps edt = new EmploiDuTemps("2000-01-01"); // Date arbitraire, car la recherche ne dépend pas de la date de référence de l'objet EmploiDuTemps

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);

        edt.rechercherCours(critere); // Cette méthode doit être adaptée pour prendre la liste des cours si elle ne le fait pas déjà

        System.out.flush();
        System.setOut(old);

        String output = baos.toString();
        // Appelle la méthode d'affichage générique avec les labels et VBox appropriés
        displayResultsInArea(RechercheResultLabel, edtDisplayArea, output, "recherche");
        // Met à jour le label de résultat spécifique à la recherche
        RechercheResultLabel.setText("Résultats de la recherche affichés.");
    }

    /**
     * Affiche toutes les notifications collectées par le NotificationManager.
     */
    @FXML
    private void showNotifications() {
        clearAndSetMessage(notificationResultLabel, notificationDisplayArea, "Chargement des notifications...");
        List<String> notifications = NotificationManager.getAllNotifications(); // NOUVEAU: Utilise NotificationManager

        if (notifications.isEmpty()) {
            notificationDisplayArea.getChildren().add(new Label("Aucune nouvelle notification."));
        } else {
            notificationDisplayArea.getChildren().clear();
            for (String notif : notifications) {
                notificationDisplayArea.getChildren().add(new Label("- " + notif));
            }
        }
        notificationResultLabel.setText("Notifications affichées.");
    }

    @FXML
    private void sendNotification(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SendNotificationForm.fxml"));
            Parent root = loader.load();
            SendNotificationController controller = loader.getController();
            controller.setSender(loggedInAdmin); // L'administrateur est l'expéditeur

            Stage stage = new Stage();
            stage.setTitle("Envoyer une Notification");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(((Node)event.getSource()).getScene().getWindow());
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            CustomAlertDialog.showError("Erreur de chargement", "Impossible de charger le formulaire d'envoi de notification.");
        }
    }
}
