package com.beginsecure.projetjavav6;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.function.Consumer; // Importation nécessaire pour la méthode générique

public class EnseignantDashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private TextField edtDateReferenceField;

    @FXML
    private Label edtResultLabel;

    @FXML
    private VBox edtDisplayArea;

    // NOUVEAU: Champs pour l'onglet Notifications
    @FXML
    private Label notificationResultLabel;
    @FXML
    private VBox notificationDisplayArea;

    private Enseignant loggedInEnseignant;

    public void setLoggedInUser(Enseignant enseignant) {
        this.loggedInEnseignant = enseignant;
        welcomeLabel.setText("Bienvenue " + enseignant.getNom() + " " + enseignant.getPrenom() + " !");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        if (loggedInEnseignant != null) {
            loggedInEnseignant.seDeconnecter();
            System.out.println("Déconnexion de l'enseignant.");
        }

        try {
            Parent loginRoot = FXMLLoader.load(getClass().getResource("LoginView.fxml"));
            Scene loginScene = new Scene(loginRoot);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(loginScene);
            stage.setTitle("Connexion au Système");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            CustomAlertDialog.showError("Erreur de déconnexion", "Erreur lors du chargement de la vue de connexion.");
        }
    }

    private void clearAndSetMessage(String message) {
        edtDisplayArea.getChildren().clear();
        edtResultLabel.setText(message);
    }

    @FXML
    private void consulterPlanning() {
        if (loggedInEnseignant != null) {
            clearAndSetMessage("Affichage de votre planning...");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            PrintStream old = System.out;
            System.setOut(ps);

            loggedInEnseignant.consulterEmploiDuTemps();

            System.out.flush();
            System.setOut(old);

            String output = baos.toString();
            if (output.trim().isEmpty() || output.contains("Vous n'avez aucun cours assigné pour le moment.")) {
                edtDisplayArea.getChildren().add(new Label("Vous n'avez aucun cours planifié."));
                edtResultLabel.setText("Aucun cours trouvé.");
            } else {
                edtDisplayArea.getChildren().clear();
                String[] lines = output.split(System.lineSeparator());
                for (String line : lines) {
                    edtDisplayArea.getChildren().add(new Label(line));
                }
                edtResultLabel.setText("Planning affiché.");
            }
        } else {
            CustomAlertDialog.showError("Erreur", "Enseignant non connecté.");
        }
    }

    @FXML
    private void consulterListeEtudiants() {
        if (loggedInEnseignant == null) {
            CustomAlertDialog.showError("Erreur", "Enseignant non connecté.");
            return;
        }

        clearAndSetMessage("Affichage de la liste des étudiants par cours...");
        StringBuilder sb = new StringBuilder();
        List<Cours> coursDonnes = loggedInEnseignant.getCoursDonnes();

        if (coursDonnes.isEmpty()) {
            sb.append("Vous n'avez aucun cours pour lequel afficher les étudiants.");
        } else {
            for (Cours cours : coursDonnes) {
                sb.append("\n--- Cours: ").append(cours.getNom()).append(" (").append(cours.getDateCours()).append(" ").append(cours.getHeureDebut()).append("-").append(cours.getHeureFin()).append(") ---\n");
                List<Etudiant> etudiantsInscrits = cours.getEtudiantsInscrits();
                if (etudiantsInscrits.isEmpty()) {
                    sb.append("  Aucun étudiant inscrit à ce cours.\n");
                } else {
                    for (Etudiant etudiant : etudiantsInscrits) {
                        sb.append("  - ").append(etudiant.getPrenom()).append(" ").append(etudiant.getNom()).append(" (").append(etudiant.getNumEtudiant()).append(")\n");
                    }
                }
            }
        }
        edtDisplayArea.getChildren().clear();
        String[] lines = sb.toString().split(System.lineSeparator());
        for (String line : lines) {
            edtDisplayArea.getChildren().add(new Label(line));
        }
        edtResultLabel.setText("Liste des étudiants affichée.");
    }

    @FXML
    private void signalerConflit() {
        if (loggedInEnseignant == null) {
            CustomAlertDialog.showError("Erreur", "Enseignant non connecté.");
            return;
        }
        clearAndSetMessage("Vérification et signalement des conflits...");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);

        loggedInEnseignant.signalerConflit();

        System.out.flush();
        System.setOut(old);

        String output = baos.toString();
        edtDisplayArea.getChildren().clear();
        String[] lines = output.split(System.lineSeparator());
        for (String line : lines) {
            edtDisplayArea.getChildren().add(new Label(line));
        }
        edtResultLabel.setText("Conflits vérifiés.");
        CustomAlertDialog.showInformation("Conflits", "La vérification des conflits a été effectuée. Consultez l'affichage pour les détails.");
    }

    // Nouvelle méthode générique pour afficher l'emploi du temps en capturant la sortie console
    private void displayEmploiDuTemps(String dateStr, String typeAffichage, Consumer<EmploiDuTemps> displayMethod) {
        if (dateStr == null || dateStr.isEmpty()) {
            edtResultLabel.setText("Veuillez entrer une date de référence (AAAA-MM-JJ).");
            return;
        }
        try {
            LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
            EmploiDuTemps edt = new EmploiDuTemps(dateStr);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            PrintStream old = System.out;
            System.setOut(ps);

            displayMethod.accept(edt); // Appelle la méthode d'affichage appropriée (afficherJour, Semaine, Mois)

            System.out.flush();
            System.setOut(old);

            String output = baos.toString();
            displayEdtResultsArea(output, typeAffichage); // Utilise une méthode d'affichage séparée pour le contenu
            edtResultLabel.setText("Emploi du temps du " + typeAffichage + " affiché.");
        } catch (DateTimeParseException e) {
            edtResultLabel.setText("Format de date invalide. Utilisez AAAA-MM-JJ.");
            CustomAlertDialog.showError("Erreur de format de date", "Le format de la date est invalide. Utilisez AAAA-MM-JJ.");
        }
    }

    // Méthode d'affichage du contenu dans la VBox (renommée pour éviter la confusion)
    private void displayEdtResultsArea(String content, String typeAffichage) {
        edtDisplayArea.getChildren().clear();
        edtResultLabel.setText("Emploi du temps de la " + typeAffichage + " :");

        if (content == null || content.trim().isEmpty()) {
            edtDisplayArea.getChildren().add(new Label("Aucun cours trouvé pour cette période."));
        } else {
            String[] lines = content.split(System.lineSeparator());
            for (String line : lines) {
                edtDisplayArea.getChildren().add(new Label(line));
            }
        }
    }

    @FXML
    private void afficherEdtJour() {
        displayEmploiDuTemps(edtDateReferenceField.getText(), "jour", edt -> edt.afficherJour());
    }

    @FXML
    private void afficherEdtSemaine() {
        displayEmploiDuTemps(edtDateReferenceField.getText(), "semaine", edt -> edt.afficherSemaine());
    }

    @FXML
    private void afficherEdtMois() {
        displayEmploiDuTemps(edtDateReferenceField.getText(), "mois", edt -> edt.afficherMois());
    }

    @FXML
    private void rechercherCours() {
        String critere = edtDateReferenceField.getText(); // Utilisation du même champ pour le critère de recherche
        if (critere == null || critere.isEmpty()) {
            edtResultLabel.setText("Veuillez entrer un critère de recherche.");
            return;
        }

        EmploiDuTemps edt = new EmploiDuTemps("2000-01-01"); // Date de référence arbitraire pour la recherche

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);

        // Appel de la méthode rechercherCours sans le deuxième argument List<Cours>
        // car votre EmploiDuTemps.java prend maintenant la liste globalement via Administrateur.getTousLesCours()
        edt.rechercherCours(critere);

        System.out.flush();
        System.setOut(old);

        String output = baos.toString();
        displayEdtResultsArea(output, "recherche");
        edtResultLabel.setText("Résultats de la recherche affichés.");
    }

    /**
     * Affiche toutes les notifications collectées par le NotificationManager.
     */
    @FXML
    private void showNotifications() {
        if (notificationDisplayArea != null) {
            notificationDisplayArea.getChildren().clear();
        }
        if (notificationResultLabel != null) {
            notificationResultLabel.setText("Chargement des notifications...");
        }

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
            controller.setSender(loggedInEnseignant); // L'enseignant est l'expéditeur

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
