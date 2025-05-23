package com.beginsecure.projetjavav6;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateEtudiantController {

    @FXML
    private TextField lastNameField;
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField formationField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorMessageLabel;

    @FXML
    private void handleCreateEtudiant(ActionEvent event) {
        String lastName = lastNameField.getText().trim();
        String firstName = firstNameField.getText().trim();
        String formation = formationField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String password = passwordField.getText(); // Mot de passe en clair

        // Validation des champs
        if (lastName.isEmpty() || firstName.isEmpty() || formation.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            errorMessageLabel.setText("Veuillez remplir tous les champs.");
            return;
        }

        if (password.length() < 6) {
            errorMessageLabel.setText("Le mot de passe doit contenir au moins 6 caractères.");
            return;
        }

        // Validation simple du format d'email
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            errorMessageLabel.setText("Le format de l'adresse e-mail est invalide.");
            return;
        }

        // Vérifier si l'email existe déjà pour un étudiant
        boolean emailExists = Administrateur.getEtudiantsGlobaux().stream()
                .anyMatch(etu -> etu.getEmail().equalsIgnoreCase(email));
        if (emailExists) {
            errorMessageLabel.setText("Cette adresse e-mail est déjà utilisée par un autre étudiant.");
            return;
        }

        // Génération du numéro d'étudiant unique
        String newNumEtudiant = generateUniqueEtudiantId();

        // Création du nouvel étudiant avec le mot de passe en clair
        Etudiant nouvelEtudiant = new Etudiant(newNumEtudiant, formation, lastName, firstName, email, phone, password);

        // Ajout de l'étudiant à la liste globale
        Administrateur.ajouterEtudiantGlobal(nouvelEtudiant);

        // Sauvegarde de tous les utilisateurs (y compris le nouveau) dans le fichier CSV
        // Note: Si le mot de passe est stocké en clair, cela est une faille de sécurité.
        // Assurez-vous que la méthode saveUsersToCsv() dans LoginController gère cela.
        LoginController.saveUsersToCsv();

        CustomAlertDialog.showInformation("Succès", "Étudiant '" + firstName + " " + lastName + "' créé avec succès.\nSon numéro d'étudiant est: " + newNumEtudiant);
        closeForm();
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        closeForm();
    }

    private void closeForm() {
        Stage stage = (Stage) lastNameField.getScene().getWindow();
        stage.close();
    }

    /**
     * Génère un numéro d'étudiant unique en se basant sur le plus grand ID existant.
     * Format: ETUxxx
     * @return Le nouveau numéro d'étudiant unique.
     */
    private String generateUniqueEtudiantId() {
        Optional<Integer> maxId = Administrateur.getEtudiantsGlobaux().stream()
                .map(etu -> {
                    String num = etu.getNumEtudiant();
                    if (num != null && num.startsWith("ETU") && num.length() > 3) {
                        try {
                            return Integer.parseInt(num.substring(3));
                        } catch (NumberFormatException e) {
                            return 0;
                        }
                    }
                    return 0;
                })
                .max(Integer::compare);

        int nextId = maxId.orElse(0) + 1;
        return String.format("ETU%03d", nextId);
    }
}
