package com.beginsecure.projetjavav6;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class EnrollStudentToCourseController {

    @FXML
    private ComboBox<Cours> courseComboBox;
    @FXML
    private ComboBox<Etudiant> studentComboBox;
    @FXML
    private Label errorMessageLabel;

    private Administrateur adminInstance; // Pour accéder aux listes globales

    /**
     * Définit l'instance de l'administrateur pour accéder aux données globales.
     * @param admin L'instance de l'administrateur.
     */
    public void setAdminInstance(Administrateur admin) {
        this.adminInstance = admin;
        populateComboBoxes(); // Peupler les ComboBoxes une fois l'admin défini
    }

    /**
     * Peuple les ComboBoxes avec les cours et les étudiants disponibles.
     */
    private void populateComboBoxes() {
        if (adminInstance != null) {
            // Peupler les cours
            ObservableList<Cours> courses = FXCollections.observableArrayList(Administrateur.getTousLesCours());
            courseComboBox.setItems(courses);

            // Peupler les étudiants
            ObservableList<Etudiant> students = FXCollections.observableArrayList(Administrateur.getEtudiantsGlobaux());
            studentComboBox.setItems(students);
        } else {
            System.err.println("L'instance Administrateur n'est pas définie dans EnrollStudentToCourseController. Impossible de peupler les ComboBoxes.");
        }
    }

    /**
     * Gère l'action d'inscription de l'étudiant au cours sélectionné.
     * @param event L'événement d'action.
     */
    @FXML
    private void handleEnroll(ActionEvent event) {
        Cours selectedCourse = courseComboBox.getSelectionModel().getSelectedItem();
        Etudiant selectedStudent = studentComboBox.getSelectionModel().getSelectedItem();

        if (selectedCourse == null || selectedStudent == null) {
            errorMessageLabel.setText("Veuillez sélectionner un cours ET un étudiant.");
            return;
        }

        // Appeler la méthode ajouterEtudiant du cours
        selectedCourse.ajouterEtudiant(selectedStudent);
        CustomAlertDialog.showInformation("Succès de l'inscription", "L'étudiant " + selectedStudent.getNom() + " a été inscrit au cours " + selectedCourse.getNom() + ".");
        closeForm();
    }

    /**
     * Gère l'action d'annulation et ferme le formulaire.
     * @param event L'événement d'action.
     */
    @FXML
    private void handleCancel(ActionEvent event) {
        closeForm();
    }

    /**
     * Ferme la fenêtre du formulaire.
     */
    private void closeForm() {
        Stage stage = (Stage) courseComboBox.getScene().getWindow();
        stage.close();
    }
}
