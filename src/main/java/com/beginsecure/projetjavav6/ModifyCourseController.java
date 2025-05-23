package com.beginsecure.projetjavav6;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ModifyCourseController {

    @FXML
    private ComboBox<Cours> courseComboBox; // ComboBox pour sélectionner le cours à modifier
    @FXML
    private TextField courseNameField;      // Champ pour le nouveau nom du cours
    @FXML
    private TextField durationField;        // Champ pour la nouvelle durée
    @FXML
    private TextField startTimeField;       // Champ pour la nouvelle heure de début
    @FXML
    private TextField endTimeField;         // Champ pour la nouvelle heure de fin
    @FXML
    private TextField dateField;            // Champ pour la nouvelle date
    @FXML
    private ComboBox<Enseignant> teacherComboBox; // ComboBox pour le nouvel enseignant
    @FXML
    private ComboBox<Salle> roomComboBox;   // ComboBox pour la nouvelle salle
    @FXML
    private Label errorMessageLabel;        // Label pour afficher les messages d'erreur

    private Administrateur adminInstance;   // Instance de l'administrateur connecté
    private Cours selectedCourse;           // Le cours actuellement sélectionné dans la ComboBox

    /**
     * Initialise le contrôleur après que tous les éléments FXML ont été chargés.
     * Peuple les ComboBox des enseignants et des salles.
     * Ajoute un écouteur pour la sélection du cours dans la ComboBox.
     */
    @FXML
    public void initialize() {
        // Peupler la ComboBox des enseignants
        ObservableList<Enseignant> enseignants = FXCollections.observableArrayList(Administrateur.getEnseignantsGlobaux());
        teacherComboBox.setItems(enseignants);
        // Définir comment afficher les enseignants dans la ComboBox
        teacherComboBox.setConverter(new javafx.util.StringConverter<Enseignant>() {
            @Override
            public String toString(Enseignant enseignant) {
                return enseignant != null ? enseignant.getPrenom() + " " + enseignant.getNom() + " (" + enseignant.getNumEnseignant() + ")" : "";
            }
            @Override
            public Enseignant fromString(String string) { return null; } // Non utilisé pour la sélection
        });

        // Peupler la ComboBox des salles
        ObservableList<Salle> salles = FXCollections.observableArrayList(Administrateur.getToutesLesSalles());
        roomComboBox.setItems(salles);
        // Définir comment afficher les salles dans la ComboBox
        roomComboBox.setConverter(new javafx.util.StringConverter<Salle>() {
            @Override
            public String toString(Salle salle) {
                return salle != null ? salle.getNumSalle() + " (Capacité: " + salle.getCapacite() + ")" : "";
            }
            @Override
            public Salle fromString(String string) { return null; } // Non utilisé pour la sélection
        });

        // Ajouter un écouteur de changement de sélection à la ComboBox des cours
        courseComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedCourse = newVal;
                populateFieldsWithCourseData(newVal); // Pré-remplir les champs avec les données du cours sélectionné
            } else {
                clearFields(); // Effacer les champs si aucun cours n'est sélectionné
            }
        });
    }

    /**
     * Définit l'instance de l'administrateur connecté pour que le contrôleur puisse l'utiliser.
     * @param admin L'instance de l'Administrateur.
     */
    public void setAdminInstance(Administrateur admin) {
        this.adminInstance = admin;
    }

    /**
     * Charge la liste des cours dans la ComboBox de sélection des cours.
     * Cette méthode est appelée depuis AdminDashboardController.
     * @param courses La liste de tous les cours disponibles.
     */
    public void loadCoursesIntoComboBox(List<Cours> courses) {
        ObservableList<Cours> observableCourses = FXCollections.observableArrayList(courses);
        courseComboBox.setItems(observableCourses);
        // Définir comment afficher les cours dans la ComboBox
        courseComboBox.setConverter(new javafx.util.StringConverter<Cours>() {
            @Override
            public String toString(Cours cours) {
                return cours != null ? cours.getNom() + " le " + cours.getDateCours() + " de " + cours.getHeureDebut() : "";
            }
            @Override
            public Cours fromString(String string) { return null; } // Non utilisé pour la sélection
        });
    }

    /**
     * Pré-remplit les champs de saisie du formulaire avec les données du cours sélectionné.
     * @param cours Le cours dont les données doivent être affichées.
     */
    private void populateFieldsWithCourseData(Cours cours) {
        if (cours != null) {
            courseNameField.setText(cours.getNom());
            durationField.setText(cours.getDuree()); // Assurez-vous que cette méthode existe dans Cours
            startTimeField.setText(cours.getHeureDebut());
            endTimeField.setText(cours.getHeureFin());
            dateField.setText(cours.getDateCours());
            teacherComboBox.getSelectionModel().select(cours.getEnseignant());
            roomComboBox.getSelectionModel().select(cours.getSalle());
        }
    }

    /**
     * Efface le contenu de tous les champs de saisie du formulaire.
     */
    private void clearFields() {
        courseNameField.clear();
        durationField.clear();
        startTimeField.clear();
        endTimeField.clear();
        dateField.clear();
        teacherComboBox.getSelectionModel().clearSelection();
        roomComboBox.getSelectionModel().clearSelection();
        errorMessageLabel.setText("");
    }

    /**
     * Gère l'action de modification du cours lorsque le bouton "Modifier le Cours" est cliqué.
     * Récupère les nouvelles valeurs des champs, effectue des validations et appelle la méthode de modification de l'administrateur.
     * @param event L'événement d'action.
     */
    @FXML
    private void handleModifyCourse(ActionEvent event) {
        if (selectedCourse == null) {
            errorMessageLabel.setText("Veuillez sélectionner un cours à modifier.");
            return;
        }

        // Récupérer les nouvelles valeurs des champs. Utiliser 'null' si le champ est vide pour indiquer pas de changement.
        String newCourseName = courseNameField.getText().isEmpty() ? null : courseNameField.getText();
        String newDuration = durationField.getText().isEmpty() ? null : durationField.getText();
        String newStartTime = startTimeField.getText().isEmpty() ? null : startTimeField.getText();
        String newEndTime = endTimeField.getText().isEmpty() ? null : endTimeField.getText();
        String newDate = dateField.getText().isEmpty() ? null : dateField.getText();
        Enseignant newTeacher = teacherComboBox.getSelectionModel().getSelectedItem(); // Prend l'objet Enseignant sélectionné
        Salle newRoom = roomComboBox.getSelectionModel().getSelectedItem();           // Prend l'objet Salle sélectionné

        // Validation des formats de temps et de date si les champs ne sont pas vides
        try {
            if (newStartTime != null && !newStartTime.isEmpty()) LocalTime.parse(newStartTime);
            if (newEndTime != null && !newEndTime.isEmpty()) LocalTime.parse(newEndTime);
        } catch (DateTimeParseException e) {
            errorMessageLabel.setText("Format d'heure invalide. Utilisez HH:MM.");
            return;
        }
        try {
            if (newDate != null && !newDate.isEmpty()) LocalDate.parse(newDate);
        } catch (DateTimeParseException e) {
            errorMessageLabel.setText("Format de date invalide. Utilisez AAAA-MM-JJ.");
            return;
        }

        // Appeler la méthode modifierCours de l'administrateur
        if (adminInstance != null) {
            adminInstance.modifierCours(
                    selectedCourse,     // Le cours à modifier
                    newCourseName,      // Nouveau nom (peut être null)
                    newDuration,        // Nouvelle durée (peut être null)
                    newStartTime,       // Nouvelle heure de début (peut être null)
                    newEndTime,         // Nouvelle heure de fin (peut être null)
                    newDate,            // Nouvelle date (peut être null)
                    newTeacher,         // Nouvel enseignant (peut être null)
                    newRoom             // Nouvelle salle (peut être null)
            );
            CustomAlertDialog.showInformation("Succès", "Cours '" + selectedCourse.getNom() + "' modifié avec succès.");
            closeForm(); // Fermer le formulaire après la modification
        } else {
            errorMessageLabel.setText("Erreur: Instance Administrateur non définie. Impossible de modifier le cours.");
        }
    }

    /**
     * Gère l'action d'annulation lorsque le bouton "Annuler" est cliqué.
     * Ferme simplement le formulaire.
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
