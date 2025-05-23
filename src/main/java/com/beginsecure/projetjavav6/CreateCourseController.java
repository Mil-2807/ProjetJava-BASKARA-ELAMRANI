package com.beginsecure.projetjavav6;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

public class CreateCourseController {

    @FXML
    private TextField courseNameField;
    @FXML
    private TextField durationField;
    @FXML
    private TextField startTimeField;
    @FXML
    private TextField endTimeField;
    @FXML
    private TextField dateField;
    @FXML
    private ComboBox<Enseignant> teacherComboBox;
    @FXML
    private ComboBox<Salle> roomComboBox;
    @FXML
    private Label errorMessageLabel;

    private Administrateur adminInstance; // Pour appeler creerCours sur l'instance admin

    // Méthode appelée après le chargement du FXML
    @FXML
    public void initialize() {
        // Peupler les ComboBox avec les données globales
        ObservableList<Enseignant> enseignants = FXCollections.observableArrayList(Administrateur.getEnseignantsGlobaux());
        teacherComboBox.setItems(enseignants);
        // Afficher le nom complet de l'enseignant dans le ComboBox
        teacherComboBox.setConverter(new javafx.util.StringConverter<Enseignant>() {
            @Override
            public String toString(Enseignant enseignant) {
                return enseignant != null ? enseignant.getPrenom() + " " + enseignant.getNom() + " (" + enseignant.getNumEnseignant() + ")" : "";
            }

            @Override
            public Enseignant fromString(String string) {
                // Non utilisé pour ce cas, mais doit être implémenté
                return null;
            }
        });

        ObservableList<Salle> salles = FXCollections.observableArrayList(Administrateur.getToutesLesSalles());
        roomComboBox.setItems(salles);
        // Afficher le numéro de salle dans le ComboBox
        roomComboBox.setConverter(new javafx.util.StringConverter<Salle>() {
            @Override
            public String toString(Salle salle) {
                return salle != null ? salle.getNumSalle() + " (Capacité: " + salle.getCapacite() + ")" : "";
            }

            @Override
            public Salle fromString(String string) {
                // Non utilisé pour ce cas, mais doit être implémenté
                return null;
            }
        });
    }

    // Méthode pour injecter l'instance de l'administrateur connecté
    public void setAdminInstance(Administrateur admin) {
        this.adminInstance = admin;
    }

    @FXML
    private void handleCreateCourse(ActionEvent event) {
        String courseName = courseNameField.getText();
        String duration = durationField.getText();
        String startTime = startTimeField.getText();
        String endTime = endTimeField.getText();
        String date = dateField.getText();
        Enseignant selectedTeacher = teacherComboBox.getSelectionModel().getSelectedItem();
        Salle selectedRoom = roomComboBox.getSelectionModel().getSelectedItem();

        // Validation des champs
        if (courseName.isEmpty() || duration.isEmpty() || startTime.isEmpty() || endTime.isEmpty() || date.isEmpty() || selectedTeacher == null || selectedRoom == null) {
            errorMessageLabel.setText("Veuillez remplir tous les champs et sélectionner un enseignant et une salle.");
            return;
        }

        // Validation du format de l'heure
        try {
            LocalTime.parse(startTime);
            LocalTime.parse(endTime);
        } catch (DateTimeParseException e) {
            errorMessageLabel.setText("Format d'heure invalide. Utilisez HH:MM.");
            return;
        }

        // Validation du format de la date (simple, juste pour s'assurer que c'est parsable)
        try {
            java.time.LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            errorMessageLabel.setText("Format de date invalide. Utilisez AAAA-MM-JJ.");
            return;
        }

        // Appeler la méthode creerCours de l'administrateur
        if (adminInstance != null) {
            // La méthode creerCours gère déjà la vérification de disponibilité de la salle
            // et l'ajout aux listes globales.
            adminInstance.creerCours(courseName, duration, startTime, endTime, date, selectedTeacher, selectedRoom);
            CustomAlertDialog.showInformation("Succès", "Cours '" + courseName + "' créé avec succès.");
            closeForm();
        } else {
            errorMessageLabel.setText("Erreur: Instance Administrateur non définie.");
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        closeForm();
    }

    private void closeForm() {
        Stage stage = (Stage) courseNameField.getScene().getWindow();
        stage.close();
    }
}
